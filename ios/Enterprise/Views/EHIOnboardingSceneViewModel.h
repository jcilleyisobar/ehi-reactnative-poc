//
//  EHIOnboardingSceneViewModel.h
//  Enterprise
//
//  Created by Stu Buchbinder on 1/11/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

#define kEmptyParallaxPadding 0
#define kMinimumParallaxPadding 20
#define kDefaultParallaxPadding 100
#define kMaximumParallaxPadding 300

typedef NS_ENUM(NSUInteger, EHIOnboardingSceneViewType){
    EHIOnboardingSceneViewTypeBalloon,
    EHIOnboardingSceneViewTypeCards,
    EHIOnboardingSceneViewTypeSun,
    EHIOnboardingSceneViewTypeFerrisWheel
};

@interface EHIOnboardingSceneViewModel : EHIViewModel <MTRReactive>

- (instancetype)initWithType:(EHIOnboardingSceneViewType)type;

@property (assign, nonatomic) EHIOnboardingSceneViewType type;
@property (assign, nonatomic) int scene;
@property (assign, nonatomic) int numberOfLayers;
@property (assign, nonatomic) BOOL welcomeScreen;
@property (copy  , nonatomic) NSAttributedString *attributedTitle;
@property (copy  , nonatomic) NSArray *offsets;
@property (copy  , nonatomic) NSArray *animations;
@property (copy  , nonatomic) NSString *animationAtlasName;

- (NSString *)nameForAnimationSpriteAtIndex:(int)index;
- (NSString *)nameForLayerSpriteAtIndex:(int)index;

@end
