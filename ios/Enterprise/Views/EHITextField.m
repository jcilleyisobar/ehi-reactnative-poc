//
//  EHITextField.m
//  Enterprise
//
//  Created by Ty Cobb on 1/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITextField.h"
#import "EHIButton.h"
#import "EHIBarButtonItem.h"
#import "EHIAnimatedLayer.h"
#import "EHIAnalytics.h"

@interface EHITextField ()
@property (weak, nonatomic) EHIButton *internalActionButton;
/** @c YES if the action button is enabled for this field */
@property (assign, nonatomic) BOOL showsActionButton;
@end

@implementation EHITextField

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        [self applyDefaults];
    }
    
    return self;
}

- (instancetype)initWithFrame:(CGRect)frame
{
    if(self = [super initWithFrame:frame]) {
        [self applyDefaults];
        
        // apply default text style when created in code
        self.font = [UIFont ehi_fontWithStyle:EHIFontStyleRegular size:16.0f];
        self.textColor = [UIColor ehi_blackColor];
    }
    
    return self;
}

- (void)applyDefaults
{
    // default to bordering the field; opt-in to setter side-effects
    self.borderType = EHITextFieldBorderField;
}

# pragma mark - UIResponderStandardEditActions

- (BOOL)canPerformAction:(SEL)action withSender:(id)sender
{
    BOOL sensitive = self.sensitive;
    BOOL forbidden = self.forbiddenSelectors.any(^(NSString *selector){
        return [selector isEqualToString:NSStringFromSelector(action)];
    });
    
    if(sensitive && forbidden) {
        return NO;
    }
    
    return [super canPerformAction:action withSender:sender];
}

- (NSArray *)forbiddenSelectors
{
    #if defined(DEBUG)
    return @[];
    #else
    return @[NSStringFromSelector(@selector(copy:)),
             NSStringFromSelector(@selector(paste:)),
             NSStringFromSelector(@selector(cut:)),
             // UIMenuController has the share option, which has a copy button, so we need to skip it also
             @"_share:"];
    #endif
}

- (void)setSensitive:(BOOL)sensitive
{
    _sensitive = sensitive;

    if(sensitive) {
        // disable keyboard caching for sensitive fields
        self.autocorrectionType = UITextAutocorrectionTypeNo;
        
        // mark as sensitive on Appsee
        [EHIAnalytics markViewAsSensitive:self];
    }
}

# pragma mark - Lifecycle

- (void)willMoveToWindow:(UIWindow *)window
{
    [super willMoveToWindow:window];
    
    // autolayout is messing around with our button's frame, let's fix it before we display
    [self invalidateActionButtonFrame];
}

- (void)didMoveToWindow
{
    [super didMoveToWindow];
    
    self.layer.borderColor = [self defaultBorderColor].CGColor;
}

# pragma mark - Text

- (void)setPhoneModel:(EHIFormattedPhone *)phoneModel
{
    _phoneModel = phoneModel;
    
    self.text = phoneModel.formattedPhone;
}

- (void)setText:(NSString *)text
{
    NSInteger newCursorPosition = self.phoneModel == nil
        ? [self resultingCursorPositionForCurrentPosition:self.cursorPosition oldText:self.text newText:text]
        : [self resultPhoneCursorPositionForPhone:self.phoneModel currentPosition:self.cursorPosition oldText:self.text newText:text];
   
    self.attributedText = [NSAttributedString attributedStringWithString:text font:self.font color:self.textColor];
    self.cursorPosition = newCursorPosition;
}

- (void)setPlaceholder:(NSString *)placeholder
{
    self.attributedPlaceholder = [NSAttributedString attributedStringWithString:placeholder font:self.font];
}

//
// Helpers
//

- (NSInteger)resultingCursorPositionForCurrentPosition:(NSInteger)currentPosition oldText:(NSString *)oldText newText:(NSString *)newText
{
    NSInteger resultDelta = newText.length - oldText.length;
    
    // custom handling when cutting or quick delete (holding backspace)
    if(resultDelta < -1) {
        NSString *oldBeforeCursor = [oldText substringToIndex:currentPosition];
        NSString *newBeforeCursor = [newText substringToIndex:MIN(newText.length, currentPosition)];
    
        // when cutting, cursor won't move so content before it will be same length
        if(oldBeforeCursor.length == newBeforeCursor.length) {
            resultDelta = 0;
        }
    }
    
    return currentPosition + resultDelta;
}

- (NSInteger)resultPhoneCursorPositionForPhone:(EHIFormattedPhone *)phone currentPosition:(NSInteger)currentPosition oldText:(NSString *)oldText newText:(NSString *)newText
{
    NSInteger resultDelta = newText.length - oldText.length;
    NSInteger entryDelta  = phone.originalPhone.length - oldText.length;
    
    // when cutting, keep cursor position the same (does NOT support quick delete)
    if(entryDelta < -1) {
        resultDelta = 0;
    }
    
    return currentPosition + resultDelta;
}

# pragma mark - State

- (void)setEnabled:(BOOL)enabled
{
    [super setEnabled:enabled];
    
    self.alpha = enabled ? 1.0f : 0.5f;
}

# pragma mark - Layout

- (void)setFrame:(CGRect)frame
{
    [super setFrame:frame];

    // animating the frame of a text field doesn't update the placeholder layer's Eframe,
    // so we're going to update it manually
    [self fixPlaceholderFrameWithBounds:self.bounds];
}

- (void)fixPlaceholderFrameWithBounds:(CGRect)bounds
{
    CALayer *placeholderLayer = nil;
   
    // search through our sublayers to find the placeholder, if we have one
    if(self.layer.sublayers.count) {
        placeholderLayer = self.layer.sublayers.find(^(CALayer *sublayer) {
            return [sublayer.delegate isKindOfClass:[UILabel class]];
        });
    }

    // and update it to the correct, inset frame if found
    placeholderLayer.frame = [self placeholderRectForBounds:self.bounds]; 
}

# pragma mark - UITextField

- (CGRect)textRectForBounds:(CGRect)bounds
{
    return [self insetTextRect:bounds];
}

- (CGRect)editingRectForBounds:(CGRect)bounds
{
    return [self insetTextRect:bounds];
}

- (CGRect)placeholderRectForBounds:(CGRect)bounds
{
    return [self insetTextRect:bounds];
}

- (CGRect)caretRectForPosition:(UITextPosition *)position
{
    return self.hidesCursor ? CGRectZero : [super caretRectForPosition:position];
}

//
// Helpers
//

- (CGRect)insetTextRect:(CGRect)rect
{
    CGRect result = rect;
    
    // only inset if against left edge
    if(self.contentHorizontalAlignment == UIControlContentHorizontalAlignmentLeft) {
        result.origin.x += 20.0f;
        result.size.width -= result.origin.x;
    }
   
    // inset right edge if we have an action button
    if(self.showsActionButton) {
        result.size.width -= self.internalActionButton.bounds.size.width;
    }
    
    return result;
}

# pragma mark - Border

- (CGFloat)defaultBorderWidth
{
    return (self.borderType & EHITextFieldBorderField) ? 1.0f : 0.0f;
}

- (UIColor *)defaultBorderColor
{
    return self.borderColor ?: [UIColor ehi_grayColor2];
}

- (void)setBorderType:(EHITextFieldBorder)borderType
{
    _borderType = borderType;
    
    [self invalidateBorder];
}

- (void)setBorderColor:(UIColor *)borderColor
{
    _borderColor = borderColor;
    
    [self invalidateBorder];
}

- (void)setShowsAlertBorder:(BOOL)showsAlertBorder
{
    _showsAlertBorder = showsAlertBorder;

    [self invalidateBorder];
}

//
// Helpers
//

- (void)invalidateBorder
{
    UIColor *alertColor = self.alertBorderColor ?: [UIColor ehi_yellowSpecialColor];
    self.layer.borderWidth = self.showsAlertBorder ? 2.0f : [self defaultBorderWidth];
    self.layer.borderColor = self.showsAlertBorder ? alertColor.CGColor : [self defaultBorderColor].CGColor;
    
    self.internalActionButton.showsBorder = self.borderType & EHITextFieldBorderButton;
}

# pragma mark - Action Button

- (void)setShowsActionButton:(BOOL)showsActionButton
{
    if(_showsActionButton == showsActionButton) {
        return;
    }
    
    _showsActionButton = showsActionButton;
  
    // insert a new action button if we're showing one
    if(showsActionButton) {
        self.internalActionButton = [self insertActionButton];
    }
    // remove action button if we're hiding it
    else {
        [self.internalActionButton removeFromSuperview];
    }
}

- (void)setActionButtonType:(EHIButtonType)actionButtonType
{
    self.showsActionButton = actionButtonType != EHIButtonTypeNone;
    self.internalActionButton.type = actionButtonType;
}

- (EHIButtonType)actionButtonType
{
    return self.internalActionButton.type;
}

- (void)setActionButtonAlpha:(CGFloat)actionButtonAlpha
{
    self.internalActionButton.alpha = actionButtonAlpha;
}

- (CGFloat)actionButtonAlpha
{
    return self.internalActionButton.alpha;
}

- (void)didTapActionButton:(UIButton *)button
{
    [self sendActionsForControlEvents:UIControlEventTouchUpInside];
}

//
// Helpers
//

- (EHIButton *)insertActionButton
{
    EHIButton *button = [EHIButton buttonWithType:UIButtonTypeCustom];
    button.type = self.actionButtonType ?: EHIButtonTypeChevron;
    
    // abandon autolayout for this button, can't get it to behave during the dashboard transition
    button.translatesAutoresizingMaskIntoConstraints = YES;
    button.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleHeight;
   
    // insert into view hierarchy
    [self addSubview:button];

    // hook into action
    [button addTarget:self action:@selector(didTapActionButton:) forControlEvents:UIControlEventTouchUpInside];
   
    // customize button style
    button.showsBorder = self.borderType & EHITextFieldBorderButton;
    button.borderColor = self.borderColor;
    
    return button;
}

- (void)invalidateActionButtonFrame
{
    // pin the button to the right side of the text field
    CGFloat height = self.bounds.size.height;
    self.internalActionButton.frame = (CGRect){
        .origin.x = CGRectGetMaxX(self.bounds) - height,
        .size = (CGSize){ .width = height, .height = height }
    };
}

- (UIButton *)actionButton
{
    return self.internalActionButton;
}

# pragma mark - Cursor

- (NSInteger)cursorPosition
{
    return [self offsetFromPosition:self.beginningOfDocument toPosition:self.selectedTextRange.start];
}

- (void)setCursorPosition:(NSInteger)position
{
    UITextPosition *start = [self positionFromPosition:self.beginningOfDocument offset:position];
    UITextPosition *end   = [self positionFromPosition:start offset:0];
    [self setSelectedTextRange:[self textRangeFromPosition:start toPosition:end]];
}

# pragma mark - Toolbar

- (UIView *)inputAccessoryView
{
    UIToolbar *inputView = (UIToolbar *)[super inputAccessoryView];
    
    // lazy load toolbar if needed
    if(self.usesDoneToolbar && inputView == nil) {
        inputView = [UIToolbar new];
        
        [inputView sizeToFit];
        [inputView setItems:@[
            [EHIBarButtonItem flexibleSpace],
            [EHIBarButtonItem buttonWithType:EHIButtonTypeDoneGreen target:self action:@selector(didTapAccessoryViewDoneButton)],
            [EHIBarButtonItem fixedSpace:1.0f],
        ]];
        
        [super setInputAccessoryView:inputView];
    }
    
    return inputView;
}

- (void)setUsesDoneToolbar:(BOOL)usesDoneToolbar
{
    _usesDoneToolbar = usesDoneToolbar;
    
    // wipe out any existing accessory view when set to false
    if(!usesDoneToolbar) {
        self.inputAccessoryView = nil;
    }
}

//
// Helpers
//

- (void)didTapAccessoryViewDoneButton
{
    if([self.delegate respondsToSelector:@selector(textFieldShouldReturn:)]) {
        [self.delegate textFieldShouldReturn:self];
    }
}

# pragma mark - Layer

+ (Class)layerClass
{
    return [EHIAnimatedLayer class];
}

@end
