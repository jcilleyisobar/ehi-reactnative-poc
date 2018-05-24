//
//  EHIRewardsBenefitsPointsViewModel.m
//  Enterprise
//
//  Created by frhoads on 1/2/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIRewardsBenefitsPointsViewModel.h"
#import "EHIUserLoyalty.h"

@interface EHIRewardsBenefitsPointsViewModel()
@end

@implementation EHIRewardsBenefitsPointsViewModel

- (void)updateWithModel:(EHIUserLoyalty *)model
{
    [super updateWithModel:model];
    
    if(model) {
        self.points = [self pointsTitleWithLoyaltyPoints:model.pointsToDate];
    }
}

- (NSAttributedString *)pointsTitleWithLoyaltyPoints:(NSInteger)points
{
    NSString *pointsString = EHILocalizedString(@"rewards_points_title", @"Points", @"");
    NSString *userPoints = [NSNumberFormatter localizedStringFromNumber:@(points) numberStyle:NSNumberFormatterDecimalStyle];

    return EHIAttributedStringBuilder.new
            .text(userPoints)
            .fontStyle(EHIFontStyleLight, 26.0f).space
            .appendText(pointsString)
            .fontStyle(EHIFontStyleLight, 16.0f).string;
}

@end
