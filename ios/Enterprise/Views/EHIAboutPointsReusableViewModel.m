//
//  EHIAboutPointsReusableViewModel.m
//  Enterprise
//
//  Created by frhoads on 1/13/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIAboutPointsReusableViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHICustomerSupportSelectionViewModel.h"
#import "EHIConfiguration.h"

@interface EHIAboutPointsReusableViewModel()
@property (assign, nonatomic) EHIAboutPointsModelType type;
@end

@implementation EHIAboutPointsReusableViewModel

- (NSString *)imageName
{
    switch(self.type) {
        case EHIAboutPointsModelTypeHistory:
            return @"icon_earn";
        case EHIAboutPointsModelTypeTransfer:
            return @"icon_transfer_points";
        case EHIAboutPointsModelTypeLostPoints:
            return @"icon_phone_large";
        default:
            return nil;
    }
}

- (NSString *)titleText
{
    switch(self.type) {
        case EHIAboutPointsModelTypeHistory:
            return EHILocalizedString(@"about_points_earning", @"Earning & Spending History", @"");
        case EHIAboutPointsModelTypeTransfer:
            return EHILocalizedString(@"about_points_transfer_title", @"Transfer Points", @"");
        case EHIAboutPointsModelTypeLostPoints:
            return EHILocalizedString(@"about_points_request_lost_title", @"Request Lost Points", @"");
        default:
            return nil;
    }
}

- (NSString *)subtitleText
{
    switch(self.type) {
        case EHIAboutPointsModelTypeHistory:
            return EHILocalizedString(@"about_points_history_text", @"View the points you've earned or redeemed on past rentals.", @"");
        case EHIAboutPointsModelTypeTransfer:
            return EHILocalizedString(@"about_points_transfer_text", @"Transfer points to a friend or family member who is also an Enterprise Plus member once per year in 500 point increments up to 7500 points.", @"");
        case EHIAboutPointsModelTypeLostPoints:
            return EHILocalizedString(@"about_points_request_lost_message", @"To request you missing rental points call us", @"");
        default:
            return nil;
    }
}

- (NSString *)buttonText
{
    return EHILocalizedString(@"profile_edit_member_info_non_editable_action_title", @"CALL US", @"");
}

- (void)promptPhoneCall
{
    [EHIAnalytics trackAction:self.analyticsState handler:nil];
    
    [UIApplication ehi_promptPhoneCall:self.eplusPhoneNumber];
}

//
// Helpers
//

- (NSString *)analyticsState
{
    switch(self.type) {
        case EHIAboutPointsModelTypeHistory:
            return EHIAnalyticsRewardBenefitsAuthActionHistory;
        case EHIAboutPointsModelTypeTransfer:
            return EHIAnalyticsRewardBenefitsAuthActionTransfer;
        case EHIAboutPointsModelTypeLostPoints:
            return EHIAnalyticsRewardBenefitsAuthActionRequest;
    }
}

-(NSString *)eplusPhoneNumber
{
    EHIConfiguration *configuration = [EHIConfiguration configuration];
    
    switch(self.type) {
        case EHIAboutPointsModelTypeHistory:
            return configuration.eplusPhone.number ?: @"";
        case EHIAboutPointsModelTypeTransfer:
            return configuration.eplusPhone.number ?: @"";
        case EHIAboutPointsModelTypeLostPoints:
            return configuration.eplusPhone.number ?: @"";
        default:
            return nil;
    }
}


# pragma mark - Generator

+ (NSArray *)all
{
    return @[
        @(EHIAboutPointsModelTypeLostPoints),
    ].map(^(NSNumber *pointsCell) {
        EHIAboutPointsReusableViewModel *model = [EHIAboutPointsReusableViewModel new];
        EHIAboutPointsModelType type = pointsCell.integerValue;
        model.type = type;
        
        return model;
    });
}

@end
