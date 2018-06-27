//
//  EHIBenefitsView.m
//  Enterprise
//
//  Created by Rafael Ramos on 8/12/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIBenefitsView.h"
#import "EHIBenefitsViewModel.h"
#import "EHIRestorableConstraint.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIBenefitsView ()
@property (strong, nonatomic) EHIBenefitsViewModel *viewModel;
@property (strong, nonatomic) CAShapeLayer *plusLayer;
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@property (weak  , nonatomic) IBOutlet UIImageView *heroImage;
@property (weak  , nonatomic) IBOutlet UIView *borderView;
@property (weak  , nonatomic) IBOutlet UIView *plusContentView;
@property (weak  , nonatomic) IBOutlet UILabel *plusLabel;
@property (weak  , nonatomic) IBOutlet UILabel *descriptionLabel;
@end

@implementation EHIBenefitsView

- (nullable instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIBenefitsViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.borderView.layer.borderWidth = 6.0f;
    self.borderView.layer.borderColor = [UIColor ehi_greenColor].CGColor;
}

- (void)layoutSublayersOfLayer:(CALayer *)layer
{
    [super layoutSublayersOfLayer:layer];
    
    if([layer isEqual:self.layer]) {
        [self invalidatePlusFrame];
    }
}

//
// Helpers
//

- (void)invalidatePlusFrame
{
    const CGFloat margin = 36.0f;
    CGRect frame = self.plusLayer.frame;
    frame.origin = (CGPoint){
        .x = self.bounds.size.width - margin - frame.size.width,
        .y = margin
    };
    
    self.plusLayer.frame = frame;
}

# pragma mark - Reactions

- (void)updateWithModel:(EHIBenefitsViewModel *)model metrics:(EHILayoutMetrics *)metrics
{
    [super updateWithModel:model metrics:metrics];
    
    self.plusLabel.attributedText        = model.plusTitle;
    self.descriptionLabel.attributedText = model.descriptionTitle;
}

# pragma mark - Accessors

- (CAShapeLayer *)plusLayer
{
    if(_plusLayer) {
        return _plusLayer;
    }
    
    // drawing variables
    CGFloat plusSize = 42.0f;
    CGFloat mid = plusSize / 2.0;
    
    // construct plus layer
    _plusLayer = [CAShapeLayer layer];
    _plusLayer.frame = CGRectMake(0, 0, plusSize, plusSize);
    _plusLayer.strokeColor = [UIColor ehi_greenColor].CGColor;
    _plusLayer.lineWidth = 6.0;
    
    // draw plus
    CGMutablePathRef path = CGPathCreateMutable(); {
        CGPathAdvanceToPoint(path, CGPointMake(0, mid), NO);
        CGPathAdvanceToPoint(path, CGPointMake(plusSize, mid), YES);
        CGPathAdvanceToPoint(path, CGPointMake(mid, 0), NO);
        CGPathAdvanceToPoint(path, CGPointMake(mid, plusSize), YES);
        
        // apply drawing
        _plusLayer.path = path;
        [self.layer addSublayer:_plusLayer];
    } CGPathRelease(path);
    
    return _plusLayer;
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize){
        .width = EHILayoutValueNil,
        .height = CGRectGetHeight(self.containerView.frame)
    };
}

+ (BOOL)isReplaceable
{
    return YES;
}

@end

NS_ASSUME_NONNULL_END
