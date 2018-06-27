//
//  EHIRewardsLearnMoreViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 6/15/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIRewardsAnalyticsViewModel.h"
#import "EHISectionHeaderModel.h"
#import "EHIOnboardingViewModel.h"

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSUInteger, EHIRewardsLearnMoreLayout) {
    EHIRewardsLearnMoreLayoutMenu,
    EHIRewardsLearnMoreLayoutEnroll
};

typedef NS_ENUM(NSUInteger, EHIRewardsLearnMoreSection) {
    EHIRewardsLearnMoreSectionSummary,
    EHIRewardsLearnMoreSectionTiers,
    EHIRewardsLearnMoreSectionTips
};

@interface EHIRewardsLearnMoreViewModel : EHIRewardsAnalyticsViewModel <MTRReactive>
@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) NSString *joinButtonTitle;
@property (copy  , nonatomic, readonly) NSString *signInButtonTitle;
@property (copy  , nonatomic, readonly) NSString *learnMoreButtonTitle;
@property (assign, nonatomic, readonly) BOOL authenticated;
@property (assign, nonatomic, readonly) BOOL hideLearnMoreButton;
@property (strong, nonatomic) NSArray *tipViewModels;
@property (strong, nonatomic) EHIOnboardingViewModel *onboardingViewModel;
@property (assign, nonatomic) EHIRewardsLearnMoreLayout layout;

- (EHISectionHeaderModel *)headerForSection:(EHIRewardsLearnMoreSection)section;
- (void)joinEnterprisePlus;
- (void)signIn;
- (void)learnMore;
- (void)close;

@end

NS_ASSUME_NONNULL_END
