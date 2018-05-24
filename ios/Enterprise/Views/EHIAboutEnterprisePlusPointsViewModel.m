//
//  EHIAboutEnterprisePlusPointsViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 12/23/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIAboutEnterprisePlusPointsViewModel.h"

@interface EHIAboutEnterprisePlusPointsViewModel ()
@property (assign, nonatomic) EHIAboutEnterprisePlusPointsType type;
@property (assign, nonatomic) BOOL isLast;
@end

@implementation EHIAboutEnterprisePlusPointsViewModel

# pragma mark - Accessors

- (NSString *)header
{
    switch(self.type) {
        case EHIAboutEnterprisePlusPointsEarn:
            return EHILocalizedString(@"about_e_p_steps_title", @"How points work", @"");
        default:
            return nil;
    }
}

- (NSString *)title
{
    switch(self.type) {
        case EHIAboutEnterprisePlusPointsEarn:
            return EHILocalizedString(@"about_e_p_earn_points_title", @"Earn Points", @"");
        case EHIAboutEnterprisePlusPointsRedeem:
            return EHILocalizedString(@"choose_your_rate_redeem_points_title", @"Redeem Points", @"");
        case EHIAboutEnterprisePlusPointsTransfer:
            return EHILocalizedString(@"about_e_p_transfer_points_title", @"Transfer Points", @"");
    }
}

- (NSString *)detail
{
    switch(self.type) {
        case EHIAboutEnterprisePlusPointsEarn:
            return EHILocalizedString(@"about_e_p_earn_points_detail", @"Enterprise Plus memebers earn one point for every qualifying dollar spent. Silver, Gold and Platinum members earn more points when they rent.", @"");
        case EHIAboutEnterprisePlusPointsRedeem:
            return EHILocalizedString(@"about_e_p_redeem_points_detail", @"You can redeem for free a rental day with as few as 400 points. The amount of points required will vary based on the rental details you provide.", @"");
        case EHIAboutEnterprisePlusPointsTransfer:
            return EHILocalizedString(@"about_e_p_transfer_points_detail", @"Transfer points to a friend or family member who is also an Enterprise Plus member once per year in 500 points increments up to 7500 points.", @"");
    }
}

- (NSString *)iconImageName
{
    switch(self.type) {
        case EHIAboutEnterprisePlusPointsEarn:
            return @"icon_earn";
        case EHIAboutEnterprisePlusPointsRedeem:
            return @"icon_redeem";
        case EHIAboutEnterprisePlusPointsTransfer:
            return @"icon_transfer_points";
    }
}

# pragma mark - Generator

+ (NSArray *)all
{
     return @[
        @(EHIAboutEnterprisePlusPointsEarn),
        @(EHIAboutEnterprisePlusPointsRedeem),
        @(EHIAboutEnterprisePlusPointsTransfer)]
    .map(^(NSNumber *type){
        EHIAboutEnterprisePlusPointsViewModel *model = [EHIAboutEnterprisePlusPointsViewModel new];
        model.type = type.integerValue;
        return model;
    }).each(^(EHIAboutEnterprisePlusPointsViewModel *model, int index, NSArray *all) {
        BOOL isLast = index == all.count - 1;
        [model setIsLast:isLast];
    });
}

@end
