//
//  EHIFlightDetailsViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 6/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFlightDetailsViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIFormFieldLabelViewModel.h"
#import "EHIFormFieldDropdownViewModel.h"
#import "EHIFormFieldTextViewModel.h"
#import "EHIReservationBuilder.h"
#import "EHIToastManager.h"
#import "EHIFormFieldButtonViewModel.h"
#import "EHIFlightDetailsSearchViewModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIFlightDetailsViewModel () <EHIFormFieldDelegate>
@property (strong   , nonatomic) NSDictionary *airlines;
@property (assign   , nonatomic) BOOL invalidForm;
@property (assign   , nonatomic) BOOL isLoading;
@property (nonatomic, readonly) EHIReservationBuilder *builder;
@property (strong   , nonatomic) EHIAirline *walkInAirline;
@property (strong   , nonatomic) EHIAirline *currentAirline;
@end

@implementation EHIFlightDetailsViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title          = EHILocalizedString(@"flight_details_screen_title", @"Flight Detail", @"");
        _submitTitle    = EHILocalizedString(@"flight_details_submit_button_title", @"SUBMIT", @"");
        _currentAirline = self.builder.airline;
    }
    
    return self;
}

- (void)updateWithModel:(id)model
{
    if([model isKindOfClass:[NSNumber class]]) {
        self.state = ((NSNumber *)model).integerValue;
    }
}

# pragma mark - Accessors

- (EHIFormFieldLabelViewModel *)helpModel
{
    if(!_helpModel) {
        NSString *helpTitle = [self helpTitle];
        _helpModel = [EHIFormFieldLabelViewModel viewModelWithTitle:helpTitle];
        _helpModel.isLastInGroup = NO;
    }
    
    return _helpModel;
}

- (NSString *)helpTitle
{
    if(self.isMultiTerminal) {
        
        return EHILocalizedString(@"flight_details_help_description_multi_terminal", @"You are traveling to an airport where we have multiple enterprise locations. Let us know your airlines and we’ll know which terminal to have your rental ready at!", @"");
    } else {
        return EHILocalizedString(@"flight_details_help_description", @"Provide your flight details to help us serve your reservation better. All fields are required.", @"");
    }
}

- (EHIFormFieldButtonViewModel *)noFlightModel
{
    if(!_noFlightModel) {
        _noFlightModel          = [EHIFormFieldButtonViewModel new];
        _noFlightModel.title    = EHILocalizedString(@"flight_details_no_flight", @"I don't have a flight", @"");
        _noFlightModel.delegate = self;
    }
    return _noFlightModel;
}

- (EHIFormFieldTextViewModel *)flightNumberModel
{
    if(!_flightNumberModel) {
        _flightNumberModel = [EHIFormFieldTextViewModel new];
        _flightNumberModel.attributedTitle = [self optionalTitleWithFieldName: EHILocalizedString(@"flight_details_flight_number_title", @"FLIGHT NUMBER", @"").uppercaseString];
        _flightNumberModel.placeholder     = EHILocalizedString(@"flight_details_flight_number_placeholder", @"Your Flight Number", @"");
        _flightNumberModel.inputValue      = self.builder.airline.flightNumber;
    }
    return _flightNumberModel;
}

- (EHIFlightDetailsSearchViewModel *)searchModel
{
    if(!_searchModel) {
        _searchModel = [[EHIFlightDetailsSearchViewModel alloc] initWithModel:self.currentAirline];
    }
    
    return _searchModel;
}

- (NSAttributedString *)optionalTitleWithFieldName:(NSString *)fieldName
{
    NSString *optionalTitle = EHILocalizedString(@"form_title_optional_field", @"(Optional)", @"");
    
    return EHIAttributedStringBuilder.new.fontStyle(EHIFontStyleBold, 14.0)
        .text(fieldName)
        .space.appendText(optionalTitle).fontStyle(EHIFontStyleLight, 14.0).string;
}

# pragma mark - EHIFormFieldDelegate

- (void)formFieldViewModelDidChangeValue:(EHIFormFieldViewModel *)viewModel
{
    [viewModel validate:NO];
}

- (void)formFieldViewModelButtonTapped:(EHIFormFieldViewModel *)viewModel
{
    [self didTapNoFlight];
}

# pragma mark - Actions

- (void)submitFlightDetails
{
    // show errors if needed
    if(self.invalidForm) {
        if(self.isMultiTerminal) {
            [EHIToastManager showMessage:EHILocalizedString(@"flight_details_no_flight_toast", @"You must provide an airline to continue", @"")];
        }
        return;
    }
    
    // track the action
    [EHIAnalytics trackAction:EHIAnalyticsResActionSaveFlight handler:nil];

    [self finalizeFlightInfo:self.buildAirline];
}

- (EHIAirline *)buildAirline
{
    // the airline
    NSString *airlineName  = self.currentAirline.details;
    NSString *flightNumber = self.flightNumberModel.inputValue;
    
    EHIAirline *airline = nil;
    
    // populate airline if user has entered information
    if(airlineName) {
        airline = [EHIAirline modelWithDictionary:@{
            @key(airline.code)    : self.airlines[airlineName] ?: @"",
            @key(airline.details) : airlineName,
        }];
        
        // include flight number, if any
        if(flightNumber) {
            [airline updateWithDictionary:@{
                @key(airline.flightNumber) : flightNumber
            }];
        }
    }
    
    return airline;
}

- (void)didTapNoFlight
{
    [EHIAnalytics trackAction:EHIAnalyticsResActionNoFlight handler:nil];
    
    [self finalizeFlightInfo:self.walkInAirline];
}

- (void)finalizeFlightInfo:(EHIAirline *)airline
{
    // update the builder
    self.builder.airline = airline;
    
    // track the action
    [EHIAnalytics trackAction:EHIAnalyticsResActionSaveFlight handler:nil];
    
    void (^completionBlock)() = ^{
        switch(self.state) {
            case EHIFlightDetailsStateNone:
                self.router.transition.pop(1).start(nil);
                break;
            case EHIFlightDetailsStateReview:
                self.router.transition.push(EHIScreenReservationReview).start(nil);
                break;
        }
    };

    if(self.builder.isModifyingReservation) {
        [self updateFlightInfo:airline handler:completionBlock];
    } else {
        completionBlock();
    }
}

- (void)updateFlightInfo:(EHIAirline *)airline handler:(void(^)())completion
{
    self.isLoading = YES;
    
    EHIReservation *reservation = self.builder.reservation;
    [[EHIServices sharedInstance] modifyDriver:reservation.driverInfo airline:airline reservation:reservation handler:^(EHIReservation *reservation, EHIServicesError *error) {
        self.isLoading = NO;
        if(!error.hasFailed) {
            ehi_call(completion)();
        }
    }];
}

- (void)showSearchAirlines
{
    __weak typeof(self) welf = self;
    self.router.transition.push(EHIScreenReservationAirlineSearch).handler(^(EHIAirline *airline){
        welf.currentAirline = airline;
    }).start(nil);
}

# pragma mark - Accessors

- (void)setCurrentAirline:(EHIAirline *)currentAirline
{
    _currentAirline = currentAirline;
    [self.searchModel updateWithModel:currentAirline];
}

- (BOOL)invalidForm
{
    return self.isMultiTerminal && !self.currentAirline.code;
}

- (NSDictionary *)airlines
{
    if(!_airlines) {
        _airlines = self.builder.reservation.pickupLocation.airlines.map(^(EHIAirline *airline) {
            return @[airline.details, airline.code];
        }).dict;
    }
    
    return _airlines;
}
    
- (EHIAirline *)walkInAirline
{
    if(!_walkInAirline) {
        _walkInAirline = self.builder.reservation.pickupLocation.airlines.find(^(EHIAirline *airline){
            return airline.isWalkIn;
        });
    }
    
    return _walkInAirline;
}

# pragma mark - Passthrough

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

- (BOOL)isMultiTerminal
{
    return self.builder.promptsMultiTerminal;
}

@end

NS_ASSUME_NONNULL_END
