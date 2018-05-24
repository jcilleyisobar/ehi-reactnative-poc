//
//  EHIRedemptionLineItemsViewModel.m
//  Enterprise
//
//  Created by fhu on 8/12/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIRedemptionLineItemsViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIReservationBuilder.h"
#import "EHIPriceFormatter.h"

@interface EHIRedemptionLineItemsViewModel () <EHIReservationBuilderReadinessListener>
@property (nonatomic, readonly) EHIReservationBuilder *builder;
@property (nonatomic, readonly) EHIPrice *creditSavings;
@end

@implementation EHIRedemptionLineItemsViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _redeemingTitle = EHILocalizedString(@"redemption_days_value_title", @"Redeeming", @"");
        _pointsTitle = EHILocalizedString(@"redemption_points_value_title", @"Points spent", @"");
        _creditTitle = EHILocalizedString(@"redemption_money_value_title", @"Credit", @"");
    }
    
    return self;
}

- (void)didBecomeActive
{
    [super didBecomeActive];
    
    [self.builder waitForReadiness:self];
}

- (void)builderIsReady:(EHIReservationBuilder *)builder
{
    [MTRReactor autorun:self action:@selector(invalidateLineItems:)];
}

- (void)invalidateLineItems:(MTRComputation *)computation
{
    self.redeemingDays = [NSString stringWithFormat:@"%ld %@",
                          (long)self.builder.daysRedeemed,
                          self.builder.daysRedeemed != 1 ?
                            EHILocalizedString(@"reservation_rate_daily_unit_plural", @"Days", @"") :
                            EHILocalizedString(@"reservation_rate_daily_unit", @"Day", @"")];
    self.points        = [NSString stringWithFormat:@"%@ %@",
                            [@(self.builder.pointsUsed) ehi_localizedDecimalString],
                            EHILocalizedString(@"rewards_points_title", @"Points", @"")];
    self.credits       = [EHIPriceFormatter format:self.creditSavings].scalesChange(NO).size(EHIPriceFontSizeSmall).attributedString;
}

#pragma mark - Passthrough

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

- (EHIPrice *)creditSavings
{
    return [self.builder.selectedCarClass vehicleRateForPrepay:self.builder.reservation.prepaySelected].priceSummary.redemptionSavings.total;
}

@end