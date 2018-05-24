//
//  EHIToastView.m
//  Enterprise
//
//  Created by Alex Koller on 4/7/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIToastView.h"
#import "EHILabel.h"

#define EHIToastAnimationDuration (0.3)

@interface EHIToastView ()
@property (strong, nonatomic) EHIToast *toast;
@property (nonatomic, readonly) EHIAnimationBuilder *hideAnimation;
@property (weak, nonatomic) IBOutlet EHILabel *messageLabel;
@property (weak, nonatomic) IBOutlet UIView *containerView;
@end

@implementation EHIToastView

+ (instancetype)instanceWithToast:(EHIToast *)toast
{
    EHIToastView *view = [EHIToastView ehi_instanceFromNib];
    [view updateWithModel:toast];
    return view;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    // configure the border
    self.containerView.layer.borderWidth = 1.0f;
    self.containerView.layer.cornerRadius = 5.0f;
}

# pragma mark - EHIUpdatable

- (void)updateWithModel:(EHIToast *)toast metrics:(EHILayoutMetrics *)metrics
{
    [super updateWithModel:toast metrics:metrics];

    BOOL isLight = toast.style == EHIToastStyleLight;
    
    // configure content
    self.toast = toast;
    self.messageLabel.text = toast.message;
    
    // configure styling
    self.containerView.backgroundColor = isLight ? [UIColor whiteColor] : [UIColor ehi_blackSpecialColor];
    self.containerView.layer.borderColor = isLight ? [UIColor ehi_grayColor3].CGColor : [UIColor clearColor].CGColor;
    self.messageLabel.textColor = isLight ? [UIColor ehi_blackColor] : [UIColor whiteColor];
}

# pragma mark - Animations

- (void)show
{
    // prepare for animation
    self.alpha = 0.0f;
    
    // fade in using fixed duration
    UIView.animate(YES).duration(EHIToastAnimationDuration).transform(^{
        self.alpha = 1.0f;
    }).then(self.hideAnimation).wait(self.toast.duration).start(^(BOOL finished) {
        [self removeFromSuperview];
    });
}

- (void)hide
{
    self.hideAnimation.start(^(BOOL finished) {
        [self removeFromSuperview];
    });
}

//
// Accesors
//

- (EHIAnimationBuilder *)hideAnimation
{
    return UIView.animate(YES).duration(EHIToastAnimationDuration).transform(^{
        self.alpha = 0.0f;
    });
}

# pragma mark - Actions

- (IBAction)didRecognizeTapGesture:(UITapGestureRecognizer *)gesture
{
    [self hide];
}

# pragma mark - Layout

- (EHILayoutMetrics *)metrics
{
    EHILayoutMetrics *metrics = [EHILayoutMetrics new];
    metrics.insets = (UIEdgeInsets){ .left = 30.0f, .right = 30.0f };

    // offset the center by 0.5 so that the 1px isn't blurry lol coregraphics
    metrics.centerOffset = self.offset;

    return metrics;
}

- (UIOffset)offset
{
    switch(self.toast.position) {
        case EHIToastPositionCenter:
            return (UIOffset){ .horizontal = EHILayoutValueNil, .vertical = 0.5f };
        case EHIToastPositionBottom: {
            CGFloat height = CGRectGetHeight(UIScreen.mainScreen.bounds);
            return (UIOffset){ .horizontal = EHILayoutValueNil, .vertical = height * 0.30f };
        }
    }
}

- (CGSize)intrinsicContentSize
{
    return (CGSize){
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.containerView.frame)
    };
}

@end
