//
//  EHIActivityIndicator.m
//  Enterprise
//
//  Created by mplace on 2/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIActivityIndicator.h"

typedef EHI_STRUCT(EHIActivityIndicatorStyle) {
    CGSize size;
    NSInteger frameCount;
    char *imagePrefix;
};

@interface EHIActivityIndicator ()
@property (strong, nonatomic) UIImageView *indicator;
@end

@implementation EHIActivityIndicator

- (instancetype)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        [self initializeWithType:EHIActivityIndicatorTypeGreen];
    }
    
    return self;
}

- (instancetype)initWithFrame:(CGRect)frame
{
    return [self initWithFrame:frame type:EHIActivityIndicatorTypeGreen];
}

- (instancetype)initWithFrame:(CGRect)frame type:(EHIActivityIndicatorType)type
{
    if(self = [super initWithFrame:frame]) {
        [self initializeWithType:type];
    }
    
    return self;
}

- (void)initializeWithType:(EHIActivityIndicatorType)type
{
    self.hidesWhenStopped = YES;
   
    // update the default type
    _type = type;
   
    // force a setup pass
    [self invalidateIndicatorType];
    [self invalidateIsAnimating];
}

# pragma mark - Type

- (void)setType:(EHIActivityIndicatorType)type
{
    // filter redundant updates
    if(_type != type) {
        [self setType:type size:EHILayoutSizeNil];
    }
}

- (void)setType:(EHIActivityIndicatorType)type size:(CGSize)size
{
    _type = type;
   
    // get the style and apply the custom size, if necessary
    EHIActivityIndicatorStyle style = [self styleForType:self.type];
    if(!CGSizeEqualToSize(size, EHILayoutSizeNil)) {
        style.size = size;
    }
    
    [self invalidateIndicatorWithStyle:style];
}

# pragma mark - Style

- (void)invalidateIndicatorType
{
    EHIActivityIndicatorStyle style = [self styleForType:self.type];
    [self invalidateIndicatorWithStyle:style];
}

- (void)invalidateIndicatorWithStyle:(EHIActivityIndicatorStyle)style
{
    // load the frames for the animation
    self.indicator.animationImages = [self framesForStyle:style];
    
    // animation duration and repeat count
    self.indicator.animationDuration = 2.0;
    self.indicator.animationRepeatCount = 0;
    
    // determine size based on style; if nil use image size
    CGSize size = style.size;
    if(size.width == EHIFloatValueNil) {
        UIImage *sampleImage = self.indicator.animationImages.firstObject;
        size = sampleImage.size;
    }
    
    // force the indicator to be that size
    [self.indicator mas_updateConstraints:^(MASConstraintMaker *make) {
        make.size.mas_equalTo(size);
    }];   
}

- (NSArray *)framesForStyle:(EHIActivityIndicatorStyle)style
{
    NSMutableArray *frames = [[NSMutableArray alloc] initWithCapacity:style.frameCount];
    for(int i=1; i<=style.frameCount ; i++) {
        [frames addObject:[UIImage imageNamed:[NSString stringWithFormat:@"%s_%d", style.imagePrefix, (int)i]]];
    }
    
    return [frames copy];
}

- (EHIActivityIndicatorStyle)styleForType:(EHIActivityIndicatorType)type
{
    switch(type) {
        case EHIActivityIndicatorTypeGreen:
            return (EHIActivityIndicatorStyle) {
                .imagePrefix = "green_activity_indicator",
                .frameCount  = 47,
                .size.width  = EHIFloatValueNil,
                .size.height = EHIFloatValueNil,
            };
        case EHIActivityIndicatorTypeSmallWhite:
            return (EHIActivityIndicatorStyle) {
                .imagePrefix = "white_activity_indicator",
                .frameCount  = 47,
                .size.width  = 31.0f,
                .size.height = 31.0f,
            };
        case EHIActivityIndicatorTypeELoader:
            return (EHIActivityIndicatorStyle) {
                .imagePrefix = "ent_logo_anim",
                .frameCount  = 91,
                .size.width  = [UIScreen mainScreen].bounds.size.width,
                .size.height = [UIScreen mainScreen].bounds.size.width * 18 / 125,
            };
    }
}

# pragma mark - Animating State

- (void)startAnimating
{
    self.isAnimating = YES;
}

- (void)stopAnimating
{
    self.isAnimating = NO;
}

- (void)setIsAnimating:(BOOL)isAnimating
{
    if(_isAnimating != isAnimating) {
        _isAnimating = isAnimating;
        [self invalidateIsAnimating];
    }
}

- (void)invalidateIsAnimating
{
    if(self.isAnimating) {
        [self.indicator startAnimating];
    } else { // !isAnimating
        [self.indicator stopAnimating];
    }
    
    if(self.hidesWhenStopped) {
        [UIView animateWithDuration:0.25f animations:^{
            self.alpha = self.isAnimating ? 1.0f : 0.0f;
        }];
    }
}

# pragma mark - Accessors

- (UIImageView *)indicator
{
    if(_indicator) {
        return _indicator;
    }
    
    _indicator = [UIImageView new];
   
    // center the indicator
    [self addSubview:_indicator];
    [_indicator mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self.mas_centerX);
        make.centerY.equalTo(self.mas_centerY);
    }];
    
    return _indicator;
}

@end
