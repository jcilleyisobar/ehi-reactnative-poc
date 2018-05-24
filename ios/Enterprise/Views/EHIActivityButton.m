//
//  EHIActivityButton.m
//  Enterprise
//
//  Created by Ty Cobb on 5/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIActivityButton.h"
#import "EHIActivityIndicator.h"

@interface EHIActivityButton ()
@property (weak  , nonatomic) EHIActivityIndicator *indicator;
@property (assign, nonatomic) BOOL wasEnabled;
@end

@implementation EHIActivityButton

- (void)applyDefaults
{
    [super applyDefaults];
    
    self.indicatorType = EHIActivityIndicatorTypeSmallWhite;
}

- (void)willMoveToWindow:(UIWindow *)window
{
    [super willMoveToWindow:window];
    
    // don't apply default insets if we're using custom alignment
    BOOL appliesDefaultInsets = window && !self.hasCustomImageAlignment;
    
    // apply default title insets if they're zero
    if(appliesDefaultInsets && UIEdgeInsetsEqualToEdgeInsets(self.titleEdgeInsets, UIEdgeInsetsZero)) {
        self.titleEdgeInsets = (UIEdgeInsets){ .top = 2.0f };
    }
    // add default image insets if they're zero
    if(appliesDefaultInsets && UIEdgeInsetsEqualToEdgeInsets(self.imageEdgeInsets, UIEdgeInsetsZero)) {
        self.imageEdgeInsets = (UIEdgeInsets){ .right = EHILightPadding };
    }
}

# pragma mark - EHILoadable

- (void)setIsLoading:(BOOL)isLoading
{
    [self setIsLoading:isLoading animated:YES];
}

- (void)setIsLoading:(BOOL)isLoading animated:(BOOL)animated
{
    if(_isLoading == isLoading) {
        return;
    }
    
    // update the indicator's state
    _isLoading = isLoading;
    self.indicator.isAnimating = isLoading;
    
    // the amount we'll offset the title/image by to make room
    const CGFloat indicatorOffset = 10.0f;
    
    // update the inidicator position before we show it
    if(isLoading) {
        CGFloat titleDestination = CGRectGetMaxX(self.titleLabel.frame) - indicatorOffset;
        [self.indicator mas_updateConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.mas_left).offset(titleDestination);
        }];
        
        [self layoutIfNeeded];
    }
    
    // animate the buttton content
    UIView.animate(animated).duration(0.25f).option(UIViewAnimationOptionBeginFromCurrentState).transform(^{
        self.titleLabel.transform = isLoading
            ? CGAffineTransformMakeTranslation(-indicatorOffset, 0.0f)
            : CGAffineTransformIdentity;
    }).start(nil);
    
    // update the disabled state if necessary
    if(self.isDisabledWhileLoading) {
        // capture the enabled state if we're going to start loading
        if(isLoading) {
            self.wasEnabled = self.enabled;
        }
        
        self.enabled = isLoading ? NO : self.wasEnabled;
    }
}

- (EHIActivityIndicator *)indicator
{
    if(_indicator) {
        return _indicator;
    }
    
    EHIActivityIndicator *indicator = [[EHIActivityIndicator alloc] initWithFrame:(CGRect){
        .size.width = 44.0f, .size.height = 44.0f
    } type:EHIActivityIndicatorTypeSmallWhite];
    
    if(self.indicatorType != EHIActivityIndicatorTypeSmallWhite) {
        [indicator setType:self.indicatorType size:(CGSize){
            .width = 31.0f, .height = 31.0f
        }];
    }
   
    [self addSubview:indicator];
    
    // position the indicator to the right of the title label
    [indicator mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(self.mas_centerY);
        make.size.mas_equalTo(indicator.bounds.size);
    }];
    
    _indicator = indicator;
    
    return _indicator;
}

@end
