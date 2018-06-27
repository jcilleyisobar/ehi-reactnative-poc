//
//  EHIOnboardingScene.h
//  Enterprise
//
//  Created by Stu Buchbinder on 12/14/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import <SpriteKit/SpriteKit.h>
#import "EHIOnboardingSceneViewModel.h"

@interface EHIOnboardingScene : SKScene

@property (copy, nonatomic) NSAttributedString *title;

- (instancetype)initWithSize:(CGSize)size model:(EHIOnboardingSceneViewModel *)model;
- (void)updateLayerOffset:(CGFloat)offset;
- (void)unload;
- (SKNode *)firstNode;

@end
