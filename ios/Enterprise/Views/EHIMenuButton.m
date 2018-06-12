//
//  EHIMenuButton.m
//  Enterprise
//
//  Created by Ty Cobb on 3/31/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIMenuButton.h"
#import "EHIMenuAnimationProgress.h"
#import "EHIAnimatedShapeLayer.h"

@interface EHIMenuButton () <EHIMenuAnimationProgressListener>
@property (assign, nonatomic) CGRect shapeBounds;
@property (nonatomic, readonly) CAShapeLayer *shapeLayer;
@end

@implementation EHIMenuButton

- (void)awakeFromNib
{
    [super awakeFromNib];
   
    // pre-calculate the bounds for the shape
    CGRect bounds = CGRectInset(self.bounds, 11.0f, 15.0f);
    self.shapeBounds = bounds;
    
    // configure the shape layer
    self.shapeLayer.lineWidth = 2.0f;
    self.shapeLayer.strokeColor = [UIColor whiteColor].CGColor;
    
    // register for animation updates
    [[EHIMenuAnimationProgress sharedInstance] addListener:self];
}

# pragma mark - Highlighting

- (void)setHighlighted:(BOOL)highlighted
{
    [super setHighlighted:highlighted];
    
    self.shapeLayer.strokeColor = highlighted ? [UIColor ehi_grayColor0].CGColor : [UIColor whiteColor].CGColor;
}

# pragma mark - Animation

- (void)setPercentComplete:(CGFloat)percentComplete
{
    CGRect bounds = self.shapeBounds;
    
    CGPoint topRight   = CGRectGetTopRight(bounds);
    CGPoint bottomLeft = CGRectGetBottomLeft(bounds);
    CGPoint center     = CGRectGetCenter(bounds);
    
     // declare subpaths
    CGMutablePathRef topPath;
    CGPathRef middlePath;
    CGPathRef bottomPath;
   
    // create the top bar
    topPath = CGPathCreateMutable();
    CGPathAdvanceToPoint(topPath, bounds.origin, NO);
    CGPathAdvanceToPoint(topPath, topRight, YES);
  
    // create the middle and bottom bars
    CGAffineTransform transform = CGAffineTransformMakeTranslation(0.0f, bounds.size.height / 2.0f);
    CGAffineTransform offsetTransform = transform;
    offsetTransform.ty += 0.5f;
    
    middlePath = CGPathCreateCopyByTransformingPath(topPath, &offsetTransform);
    bottomPath = CGPathCreateCopyByTransformingPath(middlePath, &transform);

    // map scale time-value to be decreasing and to hit 0.0 at a flexible zero time
    CGFloat scaleZeroTime = 0.25f;
    CGFloat scaleFactor   = 1.0f / scaleZeroTime;
    CGFloat scalePercentage = MAX((scaleFactor - percentComplete * scaleFactor) - scaleFactor + 1.0f, 0.0f);
   
    // pre-calculate transform values
    CGFloat translation = (bounds.size.width - bounds.size.height) / 8.0f * percentComplete;
    CGFloat rotation    = M_PI_4 * percentComplete;
    CGFloat scale       = 1.0f * scalePercentage;

    // calculate percentage based transforms
    CGAffineTransform topTransform    = EHIMakeRotationTransform(rotation, -translation, bounds.origin);
    CGAffineTransform middleTransform = EHIMakeScaleTransform(scale, scale, center);
    CGAffineTransform bottomTransform = EHIMakeRotationTransform(-rotation, translation, bottomLeft);

    // merge the subpaths into a single path
    CGMutablePathRef shapePath = CGPathCreateMutable();
    CGPathAddPath(shapePath, &topTransform, topPath);
    CGPathAddPath(shapePath, &middleTransform, middlePath);
    CGPathAddPath(shapePath, &bottomTransform, bottomPath);
   
    // release the subpaths
    CGPathRelease(topPath);
    CGPathRelease(middlePath);
    CGPathRelease(bottomPath);
    
    self.shapeLayer.path = shapePath;
    
    CGPathRelease(shapePath);
   
    // side of a square is the length of the diagonal (the line width) divided by sqrt(2)
    CGFloat rotatedHeight = self.shapeBounds.size.width / sqrtf(2.0f);
   
    // calculate the transform for the button's rotation
    rotation = M_PI * percentComplete;
    translation = (self.shapeBounds.size.width - rotatedHeight) / 2.0f * percentComplete;
    self.shapeLayer.transform = EHIMakeMenuButtonLayerTransform(rotation, translation);
}

CG_INLINE CGAffineTransform EHIMakeRotationTransform(CGFloat rotation, CGFloat translation, CGPoint anchor)
{
    CGAffineTransform transform = CGAffineTransformIdentity;
    
    transform = CGAffineTransformSetAnchorPoint(transform, anchor);
    transform = CGAffineTransformRotate(transform, rotation);
    transform = CGAffineTransformUnsetAnchorPoint(transform, anchor);
    transform = CGAffineTransformTranslate(transform, 0.0f, translation);
    
    return transform;
}

CG_INLINE CGAffineTransform EHIMakeScaleTransform(CGFloat x, CGFloat y, CGPoint anchor)
{
    CGAffineTransform transform = CGAffineTransformIdentity;
    
    transform = CGAffineTransformSetAnchorPoint(transform, anchor);
    transform = CGAffineTransformScale(transform, x, y);
    transform = CGAffineTransformUnsetAnchorPoint(transform, anchor);
    
    return transform;
}

CG_INLINE CATransform3D EHIMakeMenuButtonLayerTransform(CGFloat rotation, CGFloat translation)
{
    CATransform3D rotationTransform = CATransform3DIdentity;
    
    rotationTransform = CATransform3DRotate(rotationTransform, rotation, 0.0f, 0.0f, 1.0f);
    rotationTransform = CATransform3DTranslate(rotationTransform, translation, 0.0f, 0.0f);
    
    return rotationTransform;
}

# pragma mark - EHIMenuAnimationProgressListener

- (void)menuAnimationDidUpdate:(EHIMenuAnimationProgress *)progress
{
    if(!progress.isAnimating) {
        [CATransaction begin];
        [CATransaction setDisableActions:YES];
    }
    
    self.percentComplete = progress.percentComplete;
    
    if(!progress.isAnimating) {
        [CATransaction commit];
    }
}

# pragma mark - Layer

- (CAShapeLayer *)shapeLayer
{
    return (CAShapeLayer *)[super layer];
}

+ (Class)layerClass
{
    return [EHIAnimatedShapeLayer class];
}

@end
