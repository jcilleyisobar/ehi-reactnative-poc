//
//  EHIButton.m
//  Enterprise
//
//  Created by Ty Cobb on 1/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIButton.h"
#import "EHIMenuButton.h"
#import "EHIButtonState.h"
#import "EHIAnimatedLayer.h"
#import "UILabel+Autoshrink.h"

#define EHIButtonAlignmentNil (-1)

@interface EHIButton ()
@property (strong, nonatomic) NSMutableDictionary *states;
@property (nonatomic, readonly) NSMutableDictionary *lazyStates;
@property (nonatomic, readonly) CGSize imageSize;
@end

@implementation EHIButton

+ (instancetype)ehi_buttonWithType:(EHIButtonType)type
{
    EHIButton *button = [EHIButton buttonWithType:UIButtonTypeSystem];
    [button setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [button setType:type];
    return button;
}

- (instancetype)initWithType:(EHIButtonType)type
{
    if(self = [self init]) {
        self.type = type;
    }
    
    return self;
}

- (instancetype)initWithFrame:(CGRect)frame
{
    if(self = [super initWithFrame:frame]) {
        [self synchronizeDefaults];
    }
    
    return self;
}

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        [self synchronizeDefaults];
    }
    
    return self;
}

- (void)layoutSubviews
{
    [super layoutSubviews];
    
    // set preferred max layout based on bounds from Auto Layout
    [self invalidatePreferredMaxLayoutWidth];
}

# pragma mark - Subclassing Hooks

- (void)synchronizeDefaults
{
    [self applyDefaults];
    [self synchronize];
}

- (void)applyDefaults
{
    // don't align image by default
    self.imageHorizontalAlignment = EHIButtonAlignmentNil;
    
    // allow line breaks in the title by default
    self.titleLabel.numberOfLines = 0;
    self.titleLabel.lineBreakMode = NSLineBreakByWordWrapping;
}

# pragma mark - Setters

- (void)setShowsBorder:(BOOL)showsBorder
{
    if(_showsBorder == showsBorder) {
        return;
    }
    
    _showsBorder = showsBorder;
    self.layer.borderWidth = showsBorder ? 1.0f : 0.0f;
    self.layer.borderColor = self.borderColor.CGColor ?: [UIColor ehi_grayColor2].CGColor;
}

- (void)setBorderColor:(UIColor *)borderColor
{
    if(_borderColor == borderColor) {
        return;
    }
    
    _borderColor = borderColor;
    self.layer.borderColor = self.borderColor.CGColor;
}

# pragma mark - Dynamic Style

- (void)synchronize
{
    [self didUpdateControlState:self.state animated:NO];
}

- (void)setBackgroundColor:(UIColor *)color forState:(UIControlState)controlState
{
    EHIButtonState *state = [self lazyStateForControlState:controlState];
    state.backgroundColor = color;
}

- (void)setTintColor:(UIColor *)color forState:(UIControlState)controlState
{
    EHIButtonState *state = [self lazyStateForControlState:controlState];
    state.tintColor = color;
}

- (void)didUpdateControlState:(UIControlState)controlState animated:(BOOL)isAnimated
{
    // the state that most closely matches the control state
    EHIButtonState *state  = [self stateForControlState:controlState];
    // the parent state (mask out the highlighted state)
    EHIButtonState *parent = [self stateForControlState:(controlState ^ UIControlStateHighlighted) & controlState];
    // our last fallback, whatever normal specifies
    EHIButtonState *normal = [self stateForControlState:UIControlStateNormal];
 
    UIColor *tintColor       = state.tintColor ?: parent.tintColor ?: normal.tintColor;
    UIColor *backgroundColor = state.backgroundColor ?: parent.backgroundColor ?: normal.backgroundColor;
    
    UIView.animate(isAnimated).duration(0.25).transform(^{
        // update tint color if a state matched
        if(tintColor) {
            self.tintColor = tintColor;
        }
        // update background color if a state matched
        if(backgroundColor) {
            self.backgroundColor = backgroundColor;
        }
    }).start(nil);
}

//
// Helpers
//

- (EHIButtonState *)lazyStateForControlState:(UIControlState)controlState
{
    // get any existing state for this control state
    id key = @(controlState);
    EHIButtonState *state = self.lazyStates[key];
   
    // if we don't have one, create it
    if(!state) {
        state = [EHIButtonState new];
        self.states[key] = state;
    }
   
    return state;
}

- (NSMutableDictionary *)lazyStates
{
    if(!self.states) {
        self.states = [NSMutableDictionary new];
    }
    
    return self.states;
}

- (EHIButtonState *)stateForControlState:(UIControlState)controlState
{
    return self.states[@(controlState)];
}

# pragma mark - Control State

- (void)setEnabled:(BOOL)enabled
{
    [super setEnabled:enabled];
    // invalidate control state
    [self didUpdateControlState:self.state animated:YES];
}

- (void)setHighlighted:(BOOL)highlighted
{
    [super setHighlighted:highlighted];
    // invalidate control state
    [self didUpdateControlState:self.state animated:YES];
}

- (void)setSelected:(BOOL)selected
{
    [super setSelected:selected];
    // invalidate control state
    [self didUpdateControlState:self.state animated:YES];
}

- (void)setIsFauxDisabled:(BOOL)isFauxDisabled
{
    if (self.isFauxDisabled == isFauxDisabled) {
        return;
    }
    [self setIsFauxDisabled:isFauxDisabled animated:YES];
}

- (void)setIsFauxDisabled:(BOOL)isFauxDisabled animated:(BOOL)animated
{
    _isFauxDisabled = isFauxDisabled;
    
    // invalidate control state
    [self didUpdateControlState:self.state animated:animated];
   
    // force base UIButton styles to re-layout
    [self setNeedsLayout];
    [self layoutIfNeeded];
}

//
// Accessors
//

- (UIControlState)state
{
    // pretend to be disabled on when this property is set
    return self.isFauxDisabled ? UIControlStateDisabled : [super state];
}

# pragma mark - Types

- (void)setType:(EHIButtonType)type
{
    if(_type == type) {
        return;
    }
    
    _type = type;
    
    // get the map of control state -> state models
    NSDictionary *states = [EHIButtonState statesForType:type];
    
    // apply the states as needed
    for(NSNumber *key in states) {
        // get the control state and custom state model
        UIControlState controlState = key.unsignedIntegerValue;
        EHIButtonState *state = states[key];
     
        // synchronize built in properties
        UIImage *image = state.imageName ? [[UIImage imageNamed:state.imageName] imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate] : nil;
        [self setImage:image forState:controlState];

        if(state.titleColor) {
            [self setTitleColor:state.titleColor forState:controlState];
        }
        
        if(state.borderColor) {
            [self setBorderColor:state.borderColor];
            [self setShowsBorder:YES];
        }
        
        if(state.title) {
            [self setTitle:state.title forState:controlState];
        }
        
        if (state.attributedTitle) {
            [self setAttributedTitle:state.attributedTitle forState:controlState];
        }
        
        //  only save the state if we have properties UIButton doesn't support
        if(state.hasCustomProperties) {
            self.lazyStates[key] = state;
        }
        
        // ensure we accomodate our content
        [self sizeToFit];
    }
    
    // synchronize any custom properties
    [self synchronize];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    CGSize intrinsicSize           = [super intrinsicContentSize];
    CGRect contentRectForBounds    = [self contentRectForBounds:self.bounds];
    CGRect titleRectForContentRect = [self titleRectForContentRect:contentRectForBounds];

    // use largest height to fit all text
    CGFloat height = MAX(titleRectForContentRect.size.height, intrinsicSize.height);
    CGSize size  = (CGSize) { .width = intrinsicSize.width, .height = height };
    
    size.width  += self.titleEdgeInsets.left + self.titleEdgeInsets.right;
    size.height += self.titleEdgeInsets.top + self.titleEdgeInsets.bottom;
    return size;
}

- (CGRect)imageRectForContentRect:(CGRect)contentRect
{
    CGRect imageRect = [super imageRectForContentRect:contentRect];
   
    // return a custom image frame if our alignment is different from the standard
    if(self.hasCustomImageAlignment) {
        // start with the base image size
        imageRect.size = self.imageSize;
        
        // set the base x-position from the inset
        imageRect.origin.x = self.imageEdgeInsets.left - self.imageEdgeInsets.right;
        
        switch(self.imageHorizontalAlignment) {
            case UIControlContentHorizontalAlignmentLeft:
                imageRect.origin.x += contentRect.origin.y; break;
            case UIControlContentHorizontalAlignmentRight:
                imageRect.origin.x += CGRectGetMaxX(contentRect) - imageRect.size.width; break;
            case UIControlContentHorizontalAlignmentCenter:
                imageRect.origin.x += CGRectGetCenter(contentRect).x + imageRect.size.width / 2.0f; break;
            default: break;
        }
    }
    
    return imageRect;
}

- (CGRect)titleRectForContentRect:(CGRect)contentRect
{
    CGRect titleRect = [super titleRectForContentRect:contentRect];
   
    // move the title over to account for the image if its alignment differs
    if(self.hasCustomImageAlignment) {
        titleRect.origin.x -= self.imageSize.width - (self.imageEdgeInsets.left + self.imageEdgeInsets.right) / 2.0f;
    }
    
    return titleRect;
}

- (UIEdgeInsets)alignmentRectInsets
{
    if(!UIEdgeInsetsEqualToEdgeInsets(self.customAlignmentRectInsets, EHILayoutEdgeInsetsNil)) {
        return self.customAlignmentRectInsets;
    }

    return [super alignmentRectInsets];
}

//
// Helpers
//

- (void)invalidatePreferredMaxLayoutWidth
{
    CGRect contentRectForBounds    = [self contentRectForBounds:self.bounds];
    CGRect titleRectForContentRect = [self titleRectForContentRect:contentRectForBounds];
    
    CGFloat maxLayoutWidth = titleRectForContentRect.size.width;
    
    if(self.titleLabel.preferredMaxLayoutWidth != maxLayoutWidth) {
        // if we update the max layout width, run a second layout pass
        [self.titleLabel setPreferredMaxLayoutWidth:maxLayoutWidth];
        [self.titleLabel layoutIfNeeded];
    }
}

- (BOOL)hasCustomImageAlignment
{
    return (NSInteger)self.imageHorizontalAlignment != EHIButtonAlignmentNil;
}

- (CGSize)imageSize
{
    return [self imageForState:self.state].size;
}

# pragma mark - Title

- (void)setTitle:(NSString *)title forState:(UIControlState)state
{
    [super setTitle:title forState:state];

    // if this is the normal title, we need to set correctly colored strings for the other states as well
    if(state == UIControlStateNormal) {
        [self invalidateAttributedTitleForState:UIControlStateNormal];
        [self invalidateAttributedTitleForState:UIControlStateDisabled];
        [self invalidateAttributedTitleForState:UIControlStateSelected];
        [self invalidateAttributedTitleForState:UIControlStateSelectedHighlighted];
    }
    // otherwise, invalidate the title for this state only
    else {
        [self invalidateAttributedTitleForState:state];
    }
    
    [self shrinkTextIfNeeded];
}

- (void)shrinkTextIfNeeded
{
    [self layoutIfNeeded];

    NSString *title = [self titleForState:UIControlStateNormal];
    CGRect contentRectForBounds    = [self contentRectForBounds:self.bounds];
    CGRect titleRectForContentRect = [self titleRectForContentRect:contentRectForBounds];

    [self.titleLabel shrinkTextIfNeeded:title rect:titleRectForContentRect];
}

- (void)invalidateAttributedTitleForState:(UIControlState)state
{
    NSString *title = [self titleForState:state];
    if(!title) {
        return;
    }
    
    // construct state custom attributes
    NSMutableDictionary *attributes = [NSMutableDictionary dictionaryWithDictionary:@{
        // self.titleLabel accessor is inexorably slow so kerning based on font is inadvisable
        NSKernAttributeName : @(-0.5),
    }];
    
    // may be nil in which case current font color is used
    [attributes setValue:[self titleColorForState:state] forKey:NSForegroundColorAttributeName];
    
    NSAttributedString *attributedTitle = [[NSAttributedString alloc] initWithString:title attributes:attributes];

    [self setAttributedTitle:attributedTitle forState:state];
}

- (UIColor *)titleColorForState:(UIControlState)state
{
    UIColor *titleColor = [super titleColorForState:state];
    
    // make text for disabled buttons less opaque
    return state == UIControlStateDisabled ? [titleColor colorWithAlphaComponent:.4] : titleColor;
}

# pragma mark - Computed Properties

- (void)setEhi_title:(NSString *)title
{
    [self setTitle:title forState:UIControlStateNormal];
}

- (NSString *)ehi_title
{
    return [self titleForState:UIControlStateNormal];
}

- (void)setEhi_selectedTitle:(NSString *)title
{
    [UIView performWithoutAnimation:^{
        [self setTitle:title forState:UIControlStateSelected];
        [self setTitle:title forState:UIControlStateSelectedHighlighted];
        [self layoutIfNeeded];
    }];
}

- (NSString *)ehi_selectedTitle
{
    return [self titleForState:UIControlStateSelected];
}

- (void)setEhi_attributedTitle:(NSAttributedString *)attributedTitle
{
    [UIView performWithoutAnimation:^{
        [self setAttributedTitle:attributedTitle forState:UIControlStateNormal];
        [self layoutIfNeeded];
    }];
}

- (NSAttributedString *)ehi_attributedTitle
{
    return [self attributedTitleForState:UIControlStateNormal];
}

- (void)setEhi_titleColor:(UIColor *)titleColor
{
    [self setTitleColor:titleColor forState:UIControlStateNormal];
}

- (UIColor *)ehi_titleColor
{
    return [self titleColorForState:UIControlStateNormal];
}

- (void)setEhi_image:(UIImage *)image
{
    [self setImage:image forState:UIControlStateNormal];
}

- (UIImage *)ehi_image
{
    return [self imageForState:UIControlStateNormal];
}

- (void)setEhi_imageName:(NSString *)ehi_imageName
{
    UIImage *image = [UIImage imageNamed:ehi_imageName];
    self.ehi_image = image;
}

# pragma mark - Layer

+ (Class)layerClass
{
    return [EHIAnimatedLayer class];
}

@end
