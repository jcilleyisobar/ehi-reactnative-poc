//
//  EHICalendarDayCell.m
//  Enterprise
//
//  Created by Ty Cobb on 3/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICalendarDayCell.h"
#import "EHICalendarDayViewModel.h"
#import "EHIArrowLayer.h"

@interface EHICalendarDayCell ()
@property (assign, nonatomic) BOOL preventsStyleAnimation;
@property (weak  , nonatomic) EHIArrowLayer *arrowLayer;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (nonatomic, readonly) EHIAnimatedShapeLayer *shapeLayer;
@end

@implementation EHICalendarDayCell

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHICalendarDayViewModel new];
    }
    
    return self;
}

- (void)prepareForReuse
{
    [super prepareForReuse];
  
    // prevent animating the background color on re-use
    self.preventsStyleAnimation = YES;
}

# pragma mark - Reactions

- (void)registerReactions:(EHICalendarDayViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateTextColor:)];
    [MTRReactor autorun:self action:@selector(invalidateBackgroundStyle:)];
   
    model.bind.map(@{
        source(model.title) : dest(self, .titleLabel.text),
    });
}

- (void)invalidateTextColor:(MTRComputation *)computation
{
    NSTimeInterval duration = computation.isFirstRun ? 0.0 : 0.25;
    
    [UIView transitionWithView:self.titleLabel duration:duration options:UIViewAnimationOptionTransitionCrossDissolve animations:^{
        self.titleLabel.textColor = [self currentTextColor];
    } completion:nil];
}

- (void)invalidateBackgroundStyle:(MTRComputation *)computation
{
    // synchronize the arrow style
    EHICalendarDayStyle style = self.viewModel.style;
    
    [self updateArrowForStyle:style];
    [self updateBorderForStyle:style];

    // update the background color
    [CALayer ehi_performAnimated:!computation.isFirstRun && !self.preventsStyleAnimation transform:^{
        self.shapeLayer.fillColor = [self currentBackgroundColor].CGColor;
    }];
    
    self.preventsStyleAnimation = NO;
}

//
// Helpers
//

- (void)updateArrowForStyle:(EHICalendarDayStyle)style
{
    BOOL showsArrow = style >= EHICalendarDayStyleLeft && self.isSelected;
    
    // update the the arrow style un-animatedly
    [CALayer ehi_performUnanimated:^{
        self.clipsToBounds   = !showsArrow;
        self.layer.zPosition = showsArrow ? 0.0f : -1.0f;
        
        self.arrowLayer.fillColor = [self currentBackgroundColor].CGColor;
        self.arrowLayer.frame = [self arrowFrameForEndpoint:style];
        self.arrowLayer.direction = style == EHICalendarDayStyleLeft ? EHIArrowDirectionRight : EHIArrowDirectionLeft;
    }];
   
    // show the arrow animatedly
    self.arrowLayer.opacity = showsArrow ? 1.0f : 0.0f;
}

- (void)updateBorderForStyle:(EHICalendarDayStyle)style
{
    BOOL isCombined  = style == EHICalendarDayStyleCombined;
    BOOL isToday     = self.viewModel.isToday && style == EHICalendarDayStyleNone;
    BOOL showsBorder = isToday || (isCombined && self.isSelected);
    
    // the width of the border, when visible
    const CGFloat borderWidth = 1.0f;
    const CGFloat backroundInset = 3.0f;

    [CALayer ehi_performAnimated:!self.preventsStyleAnimation transform:^{
        self.layer.borderColor = isCombined  ? [UIColor ehi_greenColor].CGColor : [UIColor blackColor].CGColor;
        self.layer.borderWidth = showsBorder ? borderWidth : 0.0f;
    }];

    // inset the background rect if we're showing the combined day border
    CGRect backgroundRect = isCombined
        ? CGRectInset(self.bounds, backroundInset, backroundInset) : self.bounds;
    
    CGPathRef shapePath = CGPathCreateWithRect(backgroundRect, NULL);
        self.shapeLayer.path = shapePath;
    CGPathRelease(shapePath); 
}

- (CGRect)arrowFrameForEndpoint:(EHICalendarDayStyle)endpoint
{
    CGRect frame = self.arrowLayer.frame;
    // center vertically
    frame.origin.y = (self.bounds.size.height - frame.size.height) / 2.0f;

    // apply the correct x-position based on direction
    if(endpoint == EHICalendarDayStyleRight) {
        frame.origin.x = -frame.size.width;
    } else if(endpoint == EHICalendarDayStyleLeft) {
        frame.origin.x = CGRectGetMaxX(self.bounds);
    }
    
    return frame;
}

# pragma mark - Colors

- (UIColor *)currentTextColor
{
    BOOL isSelectable = self.viewModel.isSelectable;
    
    if(self.isSelected) {
        return [UIColor whiteColor];
    } else if(!isSelectable) {
        return [UIColor ehi_grayColor3];
    } else {
        return [UIColor ehi_blackColor];
    }
}

- (UIColor *)currentBackgroundColor
{
    // access the endpoint / selectability regardless so that they're reactive
    EHICalendarDayStyle style = self.viewModel.style;
    BOOL isSelectable = self.viewModel.isSelectable;
    
    if(self.isSelected) {
        return style == EHICalendarDayStyleNone ? [UIColor ehi_darkGreenColor] : [UIColor ehi_greenColor];
    } else if(!isSelectable) {
        return [UIColor ehi_grayColor1];
    } else {
        return [UIColor whiteColor];
    }
}

# pragma mark - Selection

- (void)setSelected:(BOOL)selected
{
    [super setSelected:selected];
 
    // set our style to none when we're deselected
    if(!self.selected) {
        self.viewModel.style = EHICalendarDayStyleNone;
    }
    
    [self invalidateTextColor:nil];
}

# pragma mark - Accessors

- (EHIArrowLayer *)arrowLayer
{
    if(_arrowLayer) {
        return _arrowLayer;
    }
   
    // create the arrow layer with the default size
    EHIArrowLayer *arrowLayer = [EHIArrowLayer new];
    arrowLayer.frame = (CGRect){
        .size = (CGSize){ .width = 6.0f, .height = 12.0f }
    };
   
    // insert and store it
    [self.layer insertSublayer:arrowLayer atIndex:0];
    _arrowLayer = arrowLayer;
    
    return _arrowLayer;
}

# pragma mark - Layer

- (EHIAnimatedShapeLayer *)shapeLayer
{
    return (EHIAnimatedShapeLayer *)self.layer;
}

+ (Class)layerClass
{
    return [EHIAnimatedShapeLayer class];
}

@end
