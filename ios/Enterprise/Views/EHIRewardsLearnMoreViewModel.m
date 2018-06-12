//
//  EHIRewardsLearnMoreViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 6/15/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIRewardsLearnMoreViewModel.h"
#import "EHIUserManager.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIRewardsLearnMoreViewModel () <EHIUserListener>
@property (strong, nonatomic) NSDictionary *sectionHeaders;
@end

@implementation EHIRewardsLearnMoreViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title                = EHILocalizedString(@"rewards_learn_more_title", @"Enterprise Plus", @"");
        _joinButtonTitle      = EHILocalizedString(@"enroll_join_action", @"Join", @"");
        _signInButtonTitle    = EHILocalizedString(@"login_action_title", @"SIGN IN", @"");
        _learnMoreButtonTitle = [EHILocalizedString(@"rewards_learn_more_about_eplus", @"learn more about enterprise plus", @"") uppercaseString];
        _onboardingViewModel  = [[EHIOnboardingViewModel alloc] initWithModel:@(EHIOnboardingViewModelTypeRewards)];
    }
    return self;
}

- (void)didBecomeActive
{
    [super didBecomeActive];
    
    [EHIAnalytics changeScreen:EHIScreenRewardsLearnMore state:EHIScreenRewardsLearnMore];
    [EHIAnalytics trackState:nil];
}

# pragma mark - Actions

- (void)joinEnterprisePlus
{
    [EHIAnalytics trackAction:EHIAnalyticsRewardBenefitsUnauthActionJoin handler:self.appendAnimationState];
    
    self.router.transition.push(EHIScreenEnrollmentStepOne).start(nil);
}

- (void)signIn
{
    [EHIAnalytics trackAction:EHIAnalyticsRewardBenefitsUnauthActionSignIn handler:self.appendAnimationState];
    
    __weak typeof(self) welf = self;
    self.router.transition.present(EHIScreenMainSignin).handler(^{
        BOOL isLogged = [EHIUser currentUser] != nil;
        if(isLogged){
            [welf showRewards];
        }
    }).start(nil);
}

- (void)showRewards
{
    EHIMainRouter.router.transition.animate(EHIScreenMenu, NO).start(nil);
    
    EHIMainRouter.router.transition
        .animateWithOptions(EHIScreenMenu, NAVAnimationOptionsHidden | NAVAnimationOptionsAsync)
        .root(EHIScreenRewardsBenefitsAuth).animated(NO).start(nil);
}

- (void)learnMore
{
    [EHIAnalytics trackAction:EHIAnalyticsRewardBenefitsUnauthActionLearnMore handler:nil];

    self.router.transition.push(EHIScreenAboutEnterprisePlus).start(nil);
}

- (void)close
{
    self.router.transition.dismiss.start(nil);
}

# pragma mark - Accessors

- (EHISectionHeaderModel *)headerForSection:(EHIRewardsLearnMoreSection)section
{
    return self.sectionHeaders[@(section)];
}

- (NSDictionary *)sectionHeaders
{
    if(_sectionHeaders) {
        return _sectionHeaders;
    }
    
    _sectionHeaders = [EHISectionHeaderModel modelsWithTitles:@[
        [NSNull null],
        EHILocalizedString(@"rewards_learn_more_tiers_section_title", @"TIERS", @""),
        EHILocalizedString(@"rewards_learn_more_tips_section_title", @"QUICK TIPS", @""),
    ]];
    
    return _sectionHeaders;
}

- (BOOL)authenticated
{
    return [[EHIUserManager sharedInstance] currentUser] != nil && ![[EHIUserManager sharedInstance] isEmeraldUser];
}

- (BOOL)hideLearnMoreButton
{
    return self.onboardingViewModel.currentSceneIndex < self.onboardingViewModel.scenes.count - 1;
}

# pragma mark - Analytics

- (void (^)(EHIAnalyticsContext *))appendAnimationState
{
    NSString *currentAnimationTrackState = self.onboardingViewModel.currentAnimationTrackState;
    return ^(EHIAnalyticsContext *context) {
        [context setState:currentAnimationTrackState];
    };
}

@end

NS_ASSUME_NONNULL_END
