//
//  EHIOnboardingViewModel.m
//  Enterprise
//
//  Created by Stu Buchbinder on 12/9/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIOnboardingViewModel.h"
#import "EHIViewModel_Subclass.h"

@implementation EHIOnboardingViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _currentSceneIndex = 0;
        _scenes        = @[];
        _benefitsModel = [EHIOnboardingBenefitsViewModel new];

        if([model isKindOfClass:NSNumber.class]) {
            _type = (EHIOnboardingViewModelType)[model integerValue];
        }
    }
    
    return self;
}

- (void)didBecomeActive
{
    [super didBecomeActive];
    
    [self trackAnimationChange];
}

- (void)setCurrentSceneIndex:(NSInteger)currentSceneIndex
{
    _currentSceneIndex = currentSceneIndex;
    
    [self trackAnimationChange];
}

- (void)trackAnimationChange
{
    NSString *state = [self currentAnimationTrackState];
    NSString *screen = self.type == EHIOnboardingViewModelTypeWelcome ? EHIScreenOnboarding : EHIScreenRewardsLearnMore;
    [EHIAnalytics trackState:^(EHIAnalyticsContext *context) {
        [context setRouterScreen:screen];
        context.state = state;
    }];
}

- (BOOL)isRewards
{
	return self.type == EHIOnboardingViewModelTypeRewards;
}

- (BOOL)isWelcome
{
	return self.type == EHIOnboardingViewModelTypeWelcome;
}

- (NSString *)currentAnimationTrackState
{
    switch (self.currentSceneIndex) {
        case 0:
            return EHIAnalyticsRewardBenefitsUnauthAnimation1;
        case 1:
            return EHIAnalyticsRewardBenefitsUnauthAnimation2;
        case 2:
            return EHIAnalyticsRewardBenefitsUnauthAnimation3;
        case 3:
            return EHIAnalyticsRewardBenefitsUnauthAnimation4;
        case 4:
            return EHIAnalyticsRewardBenefitsUnauthAnimation5;
        case 5:
            return EHIAnalyticsRewardBenefitsUnauthAnimation6;
        default:
            return nil;
    }
}

@end
