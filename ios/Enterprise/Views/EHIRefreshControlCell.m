//
//  EHIRefreshControlCell.m
//  Enterprise
//
//  Created by Ty Cobb on 7/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIRefreshControlCell.h"

@interface EHIRefreshControlCell ()
@property (strong, nonatomic) CAShapeLayer *progressLayer;
@property (strong, nonatomic) EHIRefreshControlViewModel *model;
@end

@implementation EHIRefreshControlCell

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.progressLayer = [CAShapeLayer new];
    self.progressLayer.fillColor   = [UIColor clearColor].CGColor;
    self.progressLayer.strokeColor = [UIColor ehi_greenColor].CGColor;
    self.progressLayer.lineWidth   = 4.0f;
    
    [self.contentView.layer addSublayer:self.progressLayer];
}

- (void)layoutSubviews
{
    [super layoutSubviews];
  
    // precompute the size of the progress indicator
    CGFloat length = 36.0f;
    CGSize  size   = (CGSize){
        .width = length, .height = length
    };
    
    // rotate the ellipse so that the stroke starts at the bottom
    CGAffineTransform rotation = CGAffineTransformIdentity;
    rotation = CGAffineTransformRotate(rotation, M_PI_2);
    rotation = CGAffineTransformTranslate(rotation, 0.0f, -length);
    
    // update the frame / path of the progress layer
    CGPathRef progressPath = CGPathCreateWithEllipseInRect((CGRect){ .size = size }, &rotation); {
        self.progressLayer.frame = CGRectWithCenterAndSize(CGRectGetCenter(self.bounds), size);
        self.progressLayer.path  = progressPath;
    } CGPathRelease(progressPath);
}

# pragma mark - Reactions

- (void)registerReactions:(EHIRefreshControlViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:^(MTRComputation *computation) {
        [CALayer ehi_performUnanimated:^{
            self.progressLayer.strokeEnd = model.percentComplete;
        }];
    }];
}

# pragma mark - Layout

+ (EHILayoutMetrics *)defaultMetrics
{
    EHILayoutMetrics *metrics = [super defaultMetrics];
    metrics.fixedSize = (CGSize) {
        .width  = EHILayoutValueNil,
        .height = 88.0f,
    };
    
    return metrics;
}

@end
