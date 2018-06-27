//
//  EHIOnboardingSceneViewModel.m
//  Enterprise
//
//  Created by Stu Buchbinder on 1/11/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import <SpriteKit/SpriteKit.h>
#import "EHIOnboardingSceneViewModel.h"
#import "balloon_sprites.h"
#import "sun_sprites.h"
#import "ferriswheel_sprites.h"

@interface EHIOnboardingSceneViewModel ()

@property (copy, nonatomic) NSString *title;
@property (copy, nonatomic) NSString *welcome;

@end
@implementation EHIOnboardingSceneViewModel

- (instancetype)initWithType:(EHIOnboardingSceneViewType)type
{
    if (self = [super init]) {
        _type = type;
        _welcomeScreen = YES;
    }
    return self;
}

#pragma mark - Helpers

- (BOOL)isWelcomeScreen
{
    return self.type == EHIOnboardingSceneViewTypeBalloon && self.welcomeScreen;
}

#pragma mark - Title

- (NSString *)title
{
	switch(self.type) {
		case EHIOnboardingSceneViewTypeBalloon:
			return EHILocalizedString(@"onboarding_scene1_title", @"Get more from the app with Enterprise Plus\u00AE", @"");
		case EHIOnboardingSceneViewTypeCards:
			return EHILocalizedString(@"onboarding_scene2_title", @"Track your rentals & get up-to-date notifications", @"");
		case EHIOnboardingSceneViewTypeSun:
			return EHILocalizedString(@"onboarding_scene3_title", @"Make reservations faster & pickups easier", @"");
		case EHIOnboardingSceneViewTypeFerrisWheel:
			return EHILocalizedString(@"onboarding_scene4_title", @"Earn points toward free rental days", @"");
	}
}

- (EHIAttributedStringBuilder *)builderWithText:(NSString *)text
{
    return EHIAttributedStringBuilder
        .new.text(text)
        .fontStyle(EHIFontStyleBold, 24.0f)
        .color([UIColor ehi_blackColor]);
}

- (NSAttributedString *)attributedTitle
{
    EHIAttributedStringBuilder *builder;
    
    if(self.isWelcomeScreen) {
        NSString *welcomeText = EHILocalizedString(@"onboarding_welcome", @"WELCOME!", @"");
        NSString *text = [NSString stringWithFormat:@"%@\n%@", welcomeText, self.title];
        NSAttributedString *welcome = [NSAttributedString attributedStringWithString:welcomeText
                                                                                font:[UIFont ehi_fontWithStyle:EHIFontStyleBold size:16.0]
                                                                               color:[UIColor ehi_greenColor]];
        
        builder = [self builderWithText:text].replace(welcomeText, welcome);
    } else {
        builder = [self builderWithText:self.title];
    }
    
    return builder.string;
}

#pragma mark - Layers

- (int)scene
{
    switch(self.type) {
        case EHIOnboardingSceneViewTypeBalloon:
            return 1;
        case EHIOnboardingSceneViewTypeCards:
            return 2;
        case EHIOnboardingSceneViewTypeSun:
            return 3;
        case EHIOnboardingSceneViewTypeFerrisWheel:
            return 4;
    }
}

- (int)numberOfLayers
{
    switch(self.type) {
        case EHIOnboardingSceneViewTypeBalloon:
            return 8;
        case EHIOnboardingSceneViewTypeCards:
            return 3;
        case EHIOnboardingSceneViewTypeSun:
            return 5;
        case EHIOnboardingSceneViewTypeFerrisWheel:
            return 5;
    }
}

- (NSArray *)offsets
{
    switch(self.type) {
        case EHIOnboardingSceneViewTypeBalloon:
            return @[
				@(kEmptyParallaxPadding),
				@(kMaximumParallaxPadding),
				@(kMaximumParallaxPadding),
				@(kMaximumParallaxPadding),
				@(kDefaultParallaxPadding),
				@(kDefaultParallaxPadding),
				@(kDefaultParallaxPadding),
				@(kDefaultParallaxPadding),
				@(kEmptyParallaxPadding)
			];

        case EHIOnboardingSceneViewTypeCards:
            return @[
				@(kMaximumParallaxPadding),
				@(kDefaultParallaxPadding),
				@(kMinimumParallaxPadding)
			];
            
        case EHIOnboardingSceneViewTypeSun:
            return @[
				@(kMinimumParallaxPadding),
				@(kEmptyParallaxPadding),
				@(kMaximumParallaxPadding),
				@(kDefaultParallaxPadding),
				@(kDefaultParallaxPadding),
				@(kMinimumParallaxPadding)
			];

        case EHIOnboardingSceneViewTypeFerrisWheel:
            return @[
				@(kEmptyParallaxPadding),
				@(kDefaultParallaxPadding),
				@(kDefaultParallaxPadding),
				@(kDefaultParallaxPadding),
				@(kMinimumParallaxPadding),
				@(kMaximumParallaxPadding)
			];

        default:
            return @[];
    }
}

#pragma mark - Animation

- (NSString *)animationAtlasName
{
    switch(self.type) {
        case EHIOnboardingSceneViewTypeBalloon:
            return BALLOON_ANIMATION_ATLAS_NAME;
        case EHIOnboardingSceneViewTypeSun:
            return SUN_ANIMATION_ATLAS_NAME;
        case EHIOnboardingSceneViewTypeFerrisWheel:
            return FERRISWHEEL_ANIMATION_ATLAS_NAME;
        case EHIOnboardingSceneViewTypeCards:
            return @"";
    }
}

- (NSArray *)animations
{
    switch(self.type) {
        case EHIOnboardingSceneViewTypeBalloon:
            return  BALLOON_ANIMATION_ANIM_BALLOON;
        case EHIOnboardingSceneViewTypeCards:
            return @[];
        case EHIOnboardingSceneViewTypeSun:
            return SUN_ANIMATION_ANIM_SUN;
        case EHIOnboardingSceneViewTypeFerrisWheel:
            return FERRISWHEEL_ANIMATION_ANIM_FERRIS_WHEEL;
    }
}

- (NSString *)animationSpritePrefix
{
    switch(self.type) {
        case EHIOnboardingSceneViewTypeBalloon:
            return BALLOON_ANIMATION_SPRITE_PREFIX;
        case EHIOnboardingSceneViewTypeSun:
            return SUN_ANIMATION_SPRITE_PREFIX;
        case EHIOnboardingSceneViewTypeFerrisWheel:
            return FERRISWHEEL_ANIMATION_SPRITE_PREFIX;
        case EHIOnboardingSceneViewTypeCards:
            return @"";
    }
}

- (NSString *)nameForAnimationSpriteAtIndex:(int)index
{
    return [NSString stringWithFormat:@"%@%d", self.animationSpritePrefix, index];
}

- (NSString *)nameForLayerSpriteAtIndex:(int)index
{
    return [NSString stringWithFormat:@"scene%d/scene%d_layer%d", self.scene, self.scene, index];
}
@end
