//
//  EHIRedemptionPickerViewModel.m
//  Enterprise
//
//  Created by mplace on 8/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIRedemptionPickerViewModel.h"
#import "EHIReservationBuilder.h"
#import "EHIToastManager.h"
#import "EHICarClass.h"

@interface EHIRedemptionPickerViewModel ()
@property (strong, nonatomic) EHICarClass *carClass;
@end

@implementation EHIRedemptionPickerViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title = EHILocalizedString(@"redemption_picker_cell_title", @"How many free days would you like to redeem?", @"title for the redemption picker cell");
    }
    
    return self;
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHICarClass class]]) {
        self.carClass = model;
    }
}

- (void)setCarClass:(EHICarClass *)carClass
{
    _carClass = carClass;
    
    self.daysRedeemed = carClass.daysToRedeem;
    
    // build subtitle
    NSString *subtitleFormat = EHILocalizedString(@"redemption_picker_cell_subtitle", @"#{points} points per free day", @"subtitle for the redemption picker cell");
    self.subtitle = [subtitleFormat ehi_applyReplacementMap:@{
        @"points" : [@(carClass.redemptionPoints) ehi_localizedDecimalString] ?: @""
    }];
    
    // build footer title
    NSString *footer = EHILocalizedString(@"redemption_picker_cell_footer", @"Points Spent:", @"footer for the redemption picker cell");
    NSString *days = [NSString stringWithFormat:@"%@", [@(carClass.pointsUsed) ehi_localizedDecimalString]];
    
    self.footerTitle = EHIAttributedStringBuilder.new
        .text(footer).fontStyle(EHIFontStyleLight, 24.f).space
        .appendText(days).fontStyle(EHIFontStyleBold, 24.f).string;
}

# pragma mark - Stepper

- (BOOL)plusButtonEnabled
{
    return self.daysRedeemed < self.carClass.maxRedemptionDays;
}

- (BOOL)minusButtonEnabled
{
    return self.daysRedeemed != 0;
}

- (void)setDaysRedeemed:(NSInteger)daysRedeemed
{
    // don't let the user go above the maximum redemption days
    if(daysRedeemed > self.carClass.maxRedemptionDays) {
        [self showMaxDaysToast];
        return;
    }
    
    // don't let them go below zero
    if(daysRedeemed < 0) {
        return;
    }
    
    [EHIAnalytics trackAction:daysRedeemed < _daysRedeemed ? EHIAnalyticsResActionRedemptionDaysMinus : EHIAnalyticsResActionRedemptionDaysPlus handler:nil];
    
    _daysRedeemed = daysRedeemed;
    self.carClass.daysToRedeem = daysRedeemed;
}

- (NSAttributedString *)stepperTitleForDaysRedeemed:(NSInteger)daysRedeemed
{
    NSString *prefix = EHILocalizedString(@"redemption_picker_cell_stepper_title", @"Days:", @"title prefix for the stepper on the redemption picker cell");

    return EHIAttributedStringBuilder.new
        .text(prefix).fontStyle(EHIFontStyleLight, 24.f).space
        .appendText([NSString stringWithFormat:@"%@", @(daysRedeemed)]).fontStyle(EHIFontStyleBold, 24.f).string;
}

//
// Helpers
//

- (void)showMaxDaysToast
{
    EHIToast *toast = [EHIToast new];
    toast.message = EHILocalizedString(@"redemption_max_redeemable_days_reached_toast", @"You have reached the maximum number of redeemable free days for this rental.", @"toast message letting the user know they cannot redeem any more free days");
    toast.duration = EHIToastDurationLong;
    
    [EHIToastManager showToast:toast];
}

# pragma mark - Accessors

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

@end
