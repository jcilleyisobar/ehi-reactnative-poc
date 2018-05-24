//
//  EHISigninField.m
//  Enterprise
//
//  Created by Ty Cobb on 4/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHISigninField.h"
#import "EHITextField.h"
#import "Reactor.h"

@interface EHISigninField () <UITextFieldDelegate>
@property (assign, nonatomic) BOOL isSecured;
@property (copy  , nonatomic) NSString *securedEntry;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet EHITextField *textField;
@end

@implementation EHISigninField

- (void)awakeFromNib
{
    [super awakeFromNib];

    self.textField.actionButtonType = EHIButtonTypeVisibility;
    self.textField.actionButton.imageEdgeInsets = (UIEdgeInsets){ .right = EHIMediumPadding };
    
    BOOL usesCustomColor = self.alertColor != nil;
    if(usesCustomColor) {
        self.textField.alertBorderColor = self.alertColor;
    }
}

# pragma mark - Setters

- (void)setValue:(NSString *)value
{
    _value = value;

    [self invalidateTextFieldText];
}

- (void)setModel:(EHISigninFieldModel *)model
{
    _model = model;
   
    self.titleLabel.text = model.title;
 
    // capture the default secure entry state
    self.isSecured = model.isSecure;
    self.textField.placeholder = model.placeholder ?: model.title;
    self.textField.actionButton.alpha = model.isSecure ? 1.0f : 0.0f;
    self.textField.returnKeyType = model.returnType;
}

- (void)setShowAlert:(BOOL)showAlert
{
    _showAlert = showAlert;
    
    [self.textField setShowsAlertBorder:showAlert];
}

//
// Helpers
//

- (void)invalidateTextFieldText
{
    NSString *newText = self.value;
    
    if(self.isSecured) {
        // only show last when entering a single character
        BOOL maskLast = self.textField.text.length + 1 == newText.length;
        
        newText = [newText ehi_securedText:maskLast];
    }
    
    self.textField.text = newText;
}

# pragma mark - UITextFieldDelegate

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [self ehi_performAction:@selector(didReturnForSigninField:) withSender:self];
    
    if([self.delegate respondsToSelector:@selector(didReturnForSigninField:)]) {
        [self.delegate didReturnForSigninField:self];
    }
    
    return YES;
}

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    // use saved value to account for custom secured text
    NSString *oldText = self.value ?: @"";
    
    // set the value without side effects, so that the reaction can update the field
    self.value = [oldText stringByReplacingCharactersInRange:range withString:string];
    
    // force reactions to run, this is necessary because iOS's quicktype cycle is faster than reactor's flush
    [[MTRReactor reactor] flush];
    
    // notify listeners of the value change
    [self sendActionsForControlEvents:UIControlEventValueChanged];
    
    return NO;
}

- (void)textFieldDidBeginEditing:(UITextField *)textField
{
    [self ehi_performAction:@selector(didBeginEditingForSigninField:) withSender:self];
    
    if([self.delegate respondsToSelector:@selector(didBeginEditingForSigninField:)]) {
        [self.delegate didBeginEditingForSigninField:self];
    }
}

- (void)textFieldDidEndEditing:(UITextField *)textField
{
    [self ehi_performAction:@selector(didEndEditingForSigninField:) withSender:self];

    if([self.delegate respondsToSelector:@selector(didEndEditingForSigninField:)]) {
        [self.delegate didEndEditingForSigninField:self];
    }
    
    // mask everything when leaving
    if(self.isSecured) {
        [self invalidateTextFieldText];
    }
}

# pragma mark - Interface Actions

- (IBAction)didTapVisibilityButton:(UIButton *)button
{
    // toggle text entry state
    self.isSecured = !self.isSecured;
}

# pragma mark - Secure

- (void)setIsSecured:(BOOL)isSecured
{
    _isSecured = isSecured;
    
    // mask text as needed
    [self invalidateTextFieldText];
    
    // show correct icon state
    self.textField.actionButton.selected = !isSecured;
    // if `secureTextEntry` is always true, this feature won't work, because the text will always be hidden
    self.textField.secureTextEntry = isSecured;
}

# pragma mark - UIResponder

- (BOOL)becomeFirstResponder
{
    return [self.textField becomeFirstResponder] || [super becomeFirstResponder];
}

- (BOOL)resignFirstResponder
{
    return [self.textField resignFirstResponder] || [super resignFirstResponder];
}

# pragma mark - Accessors

- (UIButton *)actionButton
{
    return self.textField.actionButton;
}

@end
