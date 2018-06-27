//
//  EHILabel.m
//  Enterprise
//
//  Created by mplace on 2/12/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILabel.h"
#import "UILabel+Autoshrink.h"

@interface EHILabel ()
@property (strong, nonatomic) UIView *strikethrough;
@property (assign, nonatomic) BOOL shouldAutomaticallyShrinkText;
@property (strong, nonatomic) UILongPressGestureRecognizer *copyableGesture;
@end

@implementation EHILabel

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    // only apply shrinking to text that explcitly wraps
    self.shouldAutomaticallyShrinkText = self.numberOfLines != 1;
}

- (void)layoutSubviews
{
    [super layoutSubviews];

    // set preferred max layout based on bounds from Auto Layout
    [self invalidatePreferredMaxLayoutWidth];
    
    // allow shrinking when only 1 word
    if(self.shouldAutomaticallyShrinkText) {
        [self shrinkTextIfNeeded:self.text rect:self.frame];
    }
    
    // ensure the strikethrough is in the correct place after the layout pass
    if(self.appliesStrikethrough) {
        [self invalidateStrikethroughFrame];
    }
}

- (void)setText:(NSString *)text
{
    self.attributedText = [NSAttributedString attributedStringWithString:text font:self.font color:self.textColor];
}

//
// Helpers
//

- (void)invalidatePreferredMaxLayoutWidth
{
    // default to label's width, unless special container insets are given
    UIEdgeInsets insets = self.insetsForPreferredWidthRelativeToParent;
    CGFloat maxLayoutWidth = UIEdgeInsetsEqualToEdgeInsets(insets, UIEdgeInsetsZero)
    ? self.bounds.size.width
    : self.superview.bounds.size.width - (insets.left + insets.right);
    
    if(self.preferredMaxLayoutWidth != maxLayoutWidth) {
        // if we update the max layout width, run a second layout pass
        [self setPreferredMaxLayoutWidth:maxLayoutWidth];
        [self layoutIfNeeded];
        
        if(self.layer.ehi_showsShadow) {
            [self.layer ehi_showsShadow];
        }
    }
}

# pragma mark - Strikethrough

- (void)setAppliesStrikethrough:(BOOL)appliesStrikethrough
{
    _appliesStrikethrough = appliesStrikethrough;
    
    if(appliesStrikethrough && ![self.subviews containsObject:self.strikethrough]) {
        [self invalidateStrikethroughFrame];
        [self addSubview:self.strikethrough];
    } else if(!appliesStrikethrough) {
        [self.strikethrough removeFromSuperview];
    }
}

- (void)invalidateStrikethroughFrame
{
    CGRect frame = self.bounds;
    frame.size.height = 2.f;
    frame.origin.y = self.font.lineHeight / 2;
    
    self.strikethrough.frame = frame;
}

# pragma mark - Autoshrink

- (void)setDisablesAutoShrink:(BOOL)disablesAutoShrink
{
    _disablesAutoShrink = disablesAutoShrink;
    
    [self setNeedsLayout];
}

# pragma mark - Accessors

- (UIView *)strikethrough
{
    if(!_strikethrough) {
        _strikethrough = [UIView new];
        _strikethrough.backgroundColor = [UIColor blackColor];
    }
    
    return _strikethrough;
}

# pragma mark - Computed

- (BOOL)shouldAutomaticallyShrinkText
{
    // override if auto shrink has been explicitly disabled
    return _shouldAutomaticallyShrinkText && !self.disablesAutoShrink;
}

# pragma mark - Copyable

- (void)setCopyable:(BOOL)copyable
{
    _copyable = copyable;
    
    self.userInteractionEnabled = copyable;
    
    if(_copyable) {
        [self addGestureRecognizer:self.copyableGesture];
    } else {
        [self removeGestureRecognizer:self.copyableGesture];
    }
}

#pragma mark - Copyable Gesture

- (UILongPressGestureRecognizer *)copyableGesture
{
    if(_copyableGesture) {
        return _copyableGesture;
    }
    
    _copyableGesture = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(handleLongPress:)];
    
    return _copyableGesture;
}

- (void)handleLongPress:(UILongPressGestureRecognizer *)recognizer
{
    if(recognizer.state == UIGestureRecognizerStateRecognized) {
        [self becomeFirstResponder];
        UIMenuController *menu = [UIMenuController sharedMenuController];
        [menu setTargetRect:self.frame inView:self.superview];
        [menu setMenuVisible:YES animated:YES];
    }
}

#pragma mark Clipboard

- (void)copy:(id)sender
{
    [[UIPasteboard generalPasteboard] setString:self.text ?: @""];
}

- (BOOL)canPerformAction:(SEL)action withSender:(id)sender
{
    if(self.copyable && action == @selector(copy:)) {
        return YES;
    }
    
    return [super canPerformAction:action withSender:sender];
}

- (BOOL)canBecomeFirstResponder
{
    return self.copyable;
}

@end
