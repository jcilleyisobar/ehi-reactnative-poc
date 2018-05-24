//
//  EHIInvoiceTripSummaryViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 7/19/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIInvoiceTripSummaryViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIUserRental.h"
#import "EHIPriceFormatter.h"

@implementation EHIInvoiceTripSummaryViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        
        self.pickupTitle = EHILocalizedString(@"trip_summary_pick_up", @"PICK-UP", @"");
        self.returnTitle = EHILocalizedString(@"trip_summary_return", @"RETURN", @"");
        self.totalTitle  = EHILocalizedString(@"trip_summary_final_total", @"Final Total", @"");
        self.pointsTitle = EHILocalizedString(@"trip_summary_points_earned", @"Points Earned", @"");
    }
    
    return self;
}

- (void)updateWithModel:(EHIUserRental *)model
{
    [super updateWithModel:model];
    
    if ([model isKindOfClass:[EHIUserRental class]]) {
        [self updateWithRental:model];
    }
}

- (void)updateWithRental:(EHIUserRental *)rental
{
    [self bindPickup:rental];
    [self bindReturn:rental];
    [self bindTotals:rental];
}

# pragma mark - Pickup

- (void)bindPickup:(EHIUserRental *)rental
{
    self.pickupDate     = rental.pickupTimeDisplay;
    self.pickupLocation = rental.pickupLocation.displayName;
    self.pickupCity  = [rental.pickupLocation.address formattedAddress:YES];
    self.pickupPhone = rental.pickupLocation.formattedPhoneNumber;
}

# pragma mark - Return

- (void)bindReturn:(EHIUserRental *)rental
{
    self.returnDate     = rental.returnTimeDisplay;
    self.returnLocation = rental.returnLocation.displayName;
    self.returnCity  = [rental.returnLocation.address formattedAddress:YES];
    self.returnPhone = rental.returnLocation.formattedPhoneNumber;
}

# pragma mark - Totals and Points

- (void)bindTotals:(EHIUserRental *)rental
{
    self.totalPrice     = [EHIPriceFormatter format:rental.priceSummary].string;
    self.points         = rental.pointsEarned;
    self.showPoints     = rental.pointsEarned.length > 0;
}

# pragma mark - Actions

- (void)promptCallTo:(EHIInvoiceTripSummaryPhone)location
{
    NSString *phone;
    switch (location) {
        case EHIInvoiceTripSummaryPhoneReturnLocation:
            phone = self.returnPhone;
            break;
        case EHIInvoiceTripSummaryPhonePickupLocation:
            phone = self.pickupPhone;
            break;
    }
    
    [EHIAnalytics trackAction:EHIAnalyticsReceiptActionPhoneLink handler:nil];
    
    [UIApplication ehi_promptPhoneCall:phone];
}

@end
