//
//  EHIOnboardingViewModel.h
//  Enterprise
//
//  Created by Stu Buchbinder on 12/9/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIOnboardingSceneViewModel.h"
#import "EHIOnboardingBenefitsViewModel.h"
#import "EHIOnboardingJoinNowViewModel.h"

typedef NS_ENUM(NSUInteger, EHIOnboardingViewModelType) {
    EHIOnboardingViewModelTypeWelcome,
    EHIOnboardingViewModelTypeRewards
};

@interface EHIOnboardingViewModel : EHIViewModel <MTRReactive>

@property (strong, nonatomic) EHIOnboardingBenefitsViewModel *benefitsModel;
@property (copy  , nonatomic) NSArray *scenes;
@property (assign, nonatomic) NSInteger currentSceneIndex;
@property (assign, nonatomic) EHIOnboardingViewModelType type;
@property (assign, nonatomic, readonly) BOOL isRewards;
@property (assign, nonatomic, readonly) BOOL isWelcome;
@property (copy  , nonatomic, readonly) NSString *currentAnimationTrackState;

@end
