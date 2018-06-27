//
//  EHIDismissableView.m
//  Enterprise
//
//  Created by Ty Cobb on 1/30/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDismissableView.h"

@interface EHIDismissableView ()
@property (nonatomic, readonly) EHILayoutMetrics *contentMetrics;
@end

@implementation EHIDismissableView

- (instancetype)init
{
    return [super initWithFrame:CGRectZero];
}

- (instancetype)initWithFrame:(CGRect)frame
{
    if(self = [super initWithFrame:frame]) {
        self.backgroundColor = [UIColor clearColor];
        self.alpha = 0.0f;
    }
    
    return self;
}

- (BOOL)pointInside:(CGPoint)point withEvent:(UIEvent *)event
{
    point = [self.contentView convertPoint:point fromView:self];
    BOOL isInsideContent = [self.contentView pointInside:point withEvent:event];

    if(!isInsideContent) {
        self.isVisible = NO;
    }
    
    return isInsideContent;
}

# pragma mark - Setters

- (void)setContentView:(UIView<EHILayoutable> *)contentView
{
    [self setContentView:contentView metrics:nil];
}

- (void)setContentView:(UIView<EHILayoutable> *)contentView metrics:(EHILayoutMetrics *)metrics
{
    // remove our lingering content view, if we have one
    [_contentView removeFromSuperview];
   
    // insert the new content view
    _contentView = contentView;
    [self addSubview:contentView];
 
    // apply constraints to the view
    if(!metrics) {
        metrics = [contentView.class metrics];
    }
    
    CGFloat padding = CGRectGetHeight(self.contentView.frame);
    [contentView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self).offset(padding);
        make.bottom.equalTo(self);
        make.leading.equalTo(self);
        make.trailing.equalTo(self);
        if(SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(10)) {
            make.height.equalTo(self);
        }
    }];
}

- (void)setIsVisible:(BOOL)isVisible
{
    if(_isVisible == isVisible) {
        return;
    }
    
    _isVisible = isVisible;
  
    self.alpha = isVisible ? 0.0f : 1.0f;
    [self layoutIfNeeded];
    
    // ensure we're laid out properly before animating
    CGFloat padding = isVisible ? 0.0f : CGRectGetHeight(self.contentView.frame);
    [self.contentView mas_updateConstraints:^(MASConstraintMaker *make) {
        make.top.offset(padding);
    }];
    
    if(!isVisible && [self.delegate respondsToSelector:@selector(dismissableViewWillDismiss:)]) {
        [self.delegate dismissableViewWillDismiss:self];
    }
    
    [UIView animateWithDuration:0.25
                          delay:0.0
                        options:UIViewAnimationOptionAllowUserInteraction | UIViewAnimationOptionBeginFromCurrentState
                     animations:^{
        self.alpha = isVisible ? 1.0f : 0.0f;
        [self layoutIfNeeded];
    } completion:^(BOOL finished) {
        if(!isVisible) {
            [self removeFromSuperview];
            
            // and notify the delegate
            if([self.delegate respondsToSelector:@selector(dismissableViewDidDismmiss:)]) {
                [self.delegate dismissableViewDidDismmiss:self];
            }
        }
    }];
}

@end
