//
//  EHIAboutPointsRedeemViewModel.m
//  Enterprise
//
//  Created by frhoads on 1/12/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIAboutPointsRedeemViewModel.h"
#import "EHIViewModel_Subclass.h"

@implementation EHIAboutPointsRedeemViewModel

- (NSString *)titleText
{
    return EHILocalizedString(@"about_points_redeeming_title", @"Redeeming points is easy.", @"");
}

- (NSString *)subtitleText
{
    return EHILocalizedString(@"about_points_redeeming_text", @"Just start a reservation and use your points to choose how many free rental days you want.", @"");
}

- (NSString *)buttonText
{
    return EHILocalizedString(@"dashboard_search_title", @"START A RESERVATION", @"");
}

- (void)showStartReservation
{
    [EHIAnalytics trackAction:EHIAnalyticsRewardBenefitsAuthActionRes handler:nil];

    self.router.transition
        .push(EHIScreenLocations)
        .start(nil);
}

@end
