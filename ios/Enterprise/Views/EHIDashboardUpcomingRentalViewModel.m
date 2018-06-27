//
//  EHIDashboardUpcomingRentalViewModel.m
//  Enterprise
//
//  Created by mplace on 5/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIDashboardUpcomingRentalViewModel.h"
#import "EHIConfirmationViewModel.h"
#import "EHIUserRental.h"
#import "EHILocationDetailsViewModel.h"

@interface EHIDashboardUpcomingRentalViewModel ()
@property (strong, nonatomic) EHIUserRental *upcomingRental;
@property (readonly) NSArray *wayfindings;
@end

@implementation EHIDashboardUpcomingRentalViewModel

- (instancetype)initWithModel:(id)model
{
    if (self = [super initWithModel:model]) {
        NSString *upcoming = EHILocalizedString(@"dashboard_upcoming_rental_cell_title_upcoming", @"UPCOMING", @"first half of the title for the upcoming rental cell");
        _upcomingRentalTitle = [NSAttributedString attributedSplitLineTitle:upcoming font:[UIFont ehi_fontWithStyle:EHIFontStyleHeavy size:26.f]];
        _confirmationNumberTitle = EHILocalizedString(@"dashboard_upcoming_rental_cell_confirmation_title", @"CONFIRMATION NO.", @"confirmation title for the upcoming rental cell");
        _directionsButtonTitle = EHILocalizedString(@"dashboard_upcoming_rental_cell_get_directions_title", @"GET DIRECTIONS", @"title for the get directions button in the upcoming rental cell");
        _detailsButtonTitle = EHILocalizedString(@"dashboard_upcoming_rental_cell_view_details_title", @"VIEW DETAILS", @"title for the view details button in the upcoming rental cell");
        _directionsFromTerminalText = EHILocalizedString(@"terminal_directions_header_title", @"DIRECTIONS FROM TERMINAL", @"");
    }
    
    return self;
}

- (void)updateWithModel:(EHIUserRental *)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHIUserRental class]]) {
        self.upcomingRental = model;
    }
}

- (void)setUpcomingRental:(EHIUserRental *)upcomingRental
{
    _upcomingRental = upcomingRental;
    
    self.confirmationNumber      = [NSString stringWithFormat:@"#%@", upcomingRental.confirmationNumber];
    self.pickupDateTime          = upcomingRental.pickupTimeDisplay;
    self.pickupLocation          = [self titleForLocation:upcomingRental.pickupLocation];
    self.shouldHideAirport       = upcomingRental.pickupLocation.type != EHILocationTypeAirport;
    self.vehicleImage            = upcomingRental.carClassDetails.images.firstObject;
    self.relativePickupDateTitle = [self relativeDateTitleForDate:upcomingRental.pickupDate];
}

- (NSString *)relativeDateTitleForDate:(NSDate *)date
{
    if(!date) {
        return nil;
    }
    
    NSInteger numberOfDays =  [[NSDate ehi_today] ehi_daysUntilDate:date];
    
    // display a default message of the rental is more than a week out
    if(numberOfDays > 7) {
        return EHILocalizedString(@"dashboard_upcoming_rental_cell_soon", @"See you soon!", @"a title for the upcoming rental cell on the dashboard letting the user know that their rental is soon (> 7 days).");
    }
    // display a message with the correct number of days if less than a week away
    else if(numberOfDays > 1) {
        NSString *sooner = EHILocalizedString(@"dashboard_upcoming_rental_cell_days_until_rental", @"See you in #{number_of_days} days!", @"a title for the upcoming rental cell on the dashboard letting the user know how many days until their rental.");
        return [sooner ehi_applyReplacementMap:@{
            @"number_of_days" : @(numberOfDays)
        }];
    }
    // display a message denoting 'tomorrow' if the rental is in fact tomorrow
    else if(numberOfDays == 1){
        return EHILocalizedString(@"dashboard_upcoming_rental_cell_tomorrow", @"See you tomorrow!", @"a title for the upcoming rental cell on the dashboard letting the user know that their rental is tomorrow.");
    }
    // display a message with the time of the rental if the rental is today
    else {
        NSString *time = EHILocalizedString(@"dashboard_upcoming_rental_cell_time", @"See you at #{time}", @"a title for the upcoming rental cell on the dashboard letting the user know that their rental is today at a certain time.");
        return [time ehi_applyReplacementMap:@{
            @"time" : [date ehi_localizedTimeString]
        }];
    }
}

- (NSAttributedString *)titleForLocation:(EHILocation *)location
{
    EHIAttributedStringBuilder *builder = EHIAttributedStringBuilder.new
        .color([UIColor blackColor]).fontStyle(EHIFontStyleLight, 24.f)
        .text(location.displayName).color([UIColor ehi_greenColor]);
    
    if(location.type == EHILocationTypeAirport) {
        builder.space.appendText(location.airportCode).fontStyle(EHIFontStyleLight, 18.f);
    }
    
    return builder.string;
}

# pragma mark - Actions

- (void)showDirections
{
    [EHIAnalytics trackAction:EHIAnalyticsActionGetDirections handler:nil];
    
    // prompt the user to open the maps application or to copy the address to the pasteboard
    [UIApplication ehi_promptDirectionsForLocation:self.upcomingRental.pickupLocation];
}

- (void)showDetails
{
    [EHIAnalytics trackAction:EHIAnalyticsDashActionDetails handler:nil];
    
    // navigate to the confirmation screen
    self.router.transition
        .present(EHIScreenReservation).object(self.upcomingRental).start(nil);
}

- (void)showLocationDetails
{
    EHILocationDetailsViewModel *viewModel = [[EHILocationDetailsViewModel alloc] initWithModel:self.upcomingRental.pickupLocation];
    viewModel.disablesSelection = YES;
    
    // push the location details screen with the correct location model
    self.router.transition
    .push(EHIScreenLocationDetails).object(viewModel).start(nil);
}

- (void)showDirectionsFromTerminal
{
    self.router.transition
        .push(EHIScreenLocationWayfinding).object(self.wayfindings).start(nil);
}

#pragma mark - Accessors

- (BOOL)shouldHideDirectionsFromTerminal
{
    return !self.wayfindings.count;
}

- (NSArray *)wayfindings
{
    return self.upcomingRental.pickupLocation.wayfindings;
}

@end
