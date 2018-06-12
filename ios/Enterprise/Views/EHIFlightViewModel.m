//
//  EHIReviewAirlineViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 6/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFlightViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIPlaceholder.h"
#import "EHIAirline.h"
#import "EHIFlightDetailsViewModel.h"


NS_ASSUME_NONNULL_BEGIN

@interface EHIFlightViewModel ()
@property (strong, nonatomic, nullable) EHIAirline *airline;
@property (copy  , nonatomic) NSAttributedString *detailsTitle;
@end

@implementation EHIFlightViewModel 

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title    = EHILocalizedString(@"reservation_flight_info_title", @"FLIGHT INFO", @"");
        _subtitle = EHILocalizedString(@"flight_info_subtitle", @"Allow us to better plan for your arrival", @"");
    }
    
    return self;
}

- (NSAttributedString *)addTitle
{
    NSString *title    = EHILocalizedString(@"reservation_flight_details_add_button_title", @"Add Flight", @"");
    NSString *optional = EHILocalizedString(@"form_title_optional_field", @"(Optional)", @"");

    CGFloat fontSize = 16.0f;
    return EHIAttributedStringBuilder.new
    .appendText(title).fontStyle(EHIFontStyleBold, fontSize).space
    .appendText(optional).fontStyle(EHIFontStyleLight, fontSize).string;

}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHIPlaceholder class]]) {
        [self updateWithAirline:nil];
    } else if([model isKindOfClass:[EHIAirline class]]) {
        [self updateWithAirline:model];
    }
}

//
// Helpers
//

- (void)updateWithAirline:(nullable EHIAirline *)airline
{
    self.airline      = airline;
    self.detailsTitle = [self constructDetailsTitle];
}

- (nullable NSAttributedString *)constructDetailsTitle
{
    if(!self.airline) {
        return nil;
    }
    
    BOOL walkIn = self.airline.isWalkIn;
    NSString *airlineName = !walkIn ? (self.airline.details ?: self.airline.code ?: @"") : EHILocalizedString(@"flight_details_no_flight", @"I don't have a flight", @"");
    
    EHIAttributedStringBuilder *builder = EHIAttributedStringBuilder.new.size(18).paragraphSpacing(15).text(airlineName);
    
    // optionally append flight number
    if(self.airline.flightNumber) {
        builder.newline.appendText(self.airline.flightNumber);
    }
    
    return builder.string;
}

# pragma mark - Actions

- (void)addFlightDetails
{
    [EHIAnalytics trackAction:EHIAnalyticsResActionAddFlight handler:nil];
    
    self.router.transition
        .push(EHIScreenReservationFlightDetails).object(@(EHIFlightDetailsStateNone)).start(nil);
}

# pragma mark - Computed

- (BOOL)showsAddButton
{
    return self.airline == nil;
}

@end

NS_ASSUME_NONNULL_END
