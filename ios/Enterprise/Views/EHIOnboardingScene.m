//
//  EHIOnboardingScene.m
//  Enterprise
//
//  Created by Stu Buchbinder on 12/14/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIOnboardingScene.h"
#import "sprites.h"

@interface EHIOnboardingScene()
@property (strong, nonatomic) EHIOnboardingSceneViewModel *viewModel;
@property (strong, nonatomic) SKTextureAtlas *textureAtlas;
@end

@implementation EHIOnboardingScene

static NSString const* EHIOnboardSceneLayerPrefix = @"layer";

- (instancetype)initWithSize:(CGSize)size model:(EHIOnboardingSceneViewModel *)model
{
    if(self = [super initWithSize:size]) {
        self.viewModel       = model;
        self.scaleMode       = SKSceneScaleModeAspectFit;
        self.backgroundColor = [UIColor whiteColor];
        self.anchorPoint     = CGPointMake(0.5f, 0.5f);
    }
    
    return self;
}

- (void)didMoveToView:(SKView *)view
{
    [super didMoveToView:view];
    
    view.ignoresSiblingOrder = NO;
    
    [self setupSpriteLayers];
}

#pragma mark - Helpers

- (NSString *)nameForChildNodeAtIndex:(int)index
{
    return [NSString stringWithFormat:@"%@%d", EHIOnboardSceneLayerPrefix,index];
}

#pragma mark - Sprites

- (void)setupSpriteLayers
{
    int numberOfLayers = self.viewModel.numberOfLayers;
    NSArray *layers    = [NSArray ehi_arrayWithCapacity:numberOfLayers];
    
    layers.each(^(id obj, int index) {
        NSString *textureName = [self.viewModel nameForLayerSpriteAtIndex:index + 1];
        SKTexture *texture    = [self.textureAtlas textureNamed:textureName];
        if(texture != nil) {
            SKSpriteNode *node = [[SKSpriteNode alloc] initWithTexture:texture];
            node.name          = [self nameForChildNodeAtIndex:index];
            node.anchorPoint   = CGPointMake(0.5f, 0.5f);
            node.zPosition     = numberOfLayers - index;
            [self addChild:node];
        }
    });
    
    [self setupAnimation];
}

- (void)setupAnimation
{
    NSArray *animations = self.viewModel.animations.copy;
    if(animations.count) {
        SKTextureAtlas *animationAtlas = [SKTextureAtlas atlasNamed:self.viewModel.animationAtlasName];
        
        NSArray *textures = [NSArray ehi_arrayWithCapacity:animations.count].map(^(id obj, int index) {
            NSString *textureName = [self.viewModel nameForAnimationSpriteAtIndex:index];
            SKTexture *texture     = [animationAtlas textureNamed:textureName];
            
            return texture;
        });
        
        SKSpriteNode *animationSprite = [[SKSpriteNode alloc] initWithTexture:textures[0]];
        animationSprite.name = [self nameForChildNodeAtIndex:self.viewModel.numberOfLayers];
        animationSprite.zPosition = 0;
        animationSprite.anchorPoint = CGPointMake(0.5f, 0.5f);
        [self addChild:animationSprite];

        CGFloat fps = 1.0f / 30.0f;
        SKAction *action = [SKAction animateWithTextures:textures timePerFrame:fps];
        [animationSprite runAction:[SKAction repeatActionForever:action]];
    }
}

- (void)updateLayerOffset:(CGFloat)offset
{
    CGFloat dX = (offset > 0) ? (1.0f - offset) : offset;
    
    NSArray *offsets = (self.viewModel.offsets) ?: @[];
    offsets.each(^(id obj, int index) {
        CGFloat nodeOffset = [obj floatValue] * dX;
        SKNode *node = [self childNodeWithName:[self nameForChildNodeAtIndex:index]];
        node.position = CGPointMake(nodeOffset, 0);
    });
}

#pragma mark - Accessors

- (SKTextureAtlas *)textureAtlas
{
    if(!_textureAtlas) {
        _textureAtlas = [SKTextureAtlas atlasNamed:SPRITES_ATLAS_NAME];
    }
    
    return _textureAtlas;
}

-(NSAttributedString *)title
{
    return self.viewModel.attributedTitle;
}

- (SKNode *)firstNode
{
    return [self childNodeWithName:[NSString stringWithFormat:@"%@0", EHIOnboardSceneLayerPrefix]];
}

#pragma mark - Memory Management

- (void)dealloc
{
    [self unload];
}

- (void)unload
{
	(self.children ?: @[]).each(^(id obj, int index) {
        [obj removeAllActions];
        [obj removeAllChildren];
        [obj removeFromParent];
    });
    
    [self removeAllActions];
    [self removeAllChildren];
    [self removeFromParent];
    self.textureAtlas = nil;
}

@end
