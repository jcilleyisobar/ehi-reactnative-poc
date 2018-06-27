//
//  EHIPaymentInputView.m
//  Enterprise
//
//  Created by Alex Koller on 1/14/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPaymentInputView.h"
#import "EHIPaymentInputViewModel.h"
#import "EHITextField.h"
#import "EHILabel.h"
#import "EHIToggleButton.h"

@interface EHIPaymentInputView () <UITextFieldDelegate>
@property (strong, nonatomic) EHIPaymentInputViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *nameLabel;
@property (weak  , nonatomic) IBOutlet EHITextField *nameField;

@property (weak  , nonatomic) IBOutlet UILabel *cardNumberLabel;
@property (weak  , nonatomic) IBOutlet EHITextField *cardNumberField;

@property (weak  , nonatomic) IBOutlet UILabel *expirationLabel;
@property (weak  , nonatomic) IBOutlet EHITextField *expirationMonthField;
@property (weak  , nonatomic) IBOutlet EHITextField *expirationYearField;

@property (weak  , nonatomic) IBOutlet UILabel *cvvCodeLabel;
@property (weak  , nonatomic) IBOutlet EHITextField *cvvCodeField;

@property (weak  , nonatomic) IBOutlet UIView *termsContainer;
@property (weak  , nonatomic) IBOutlet EHILabel *termsLabel;
@property (weak  , nonatomic) IBOutlet EHIToggleButton *termsToggleButton;

@property (weak  , nonatomic) IBOutlet UIView *saveContainer;
@property (weak  , nonatomic) IBOutlet UILabel *saveLabel;
@property (weak  , nonatomic) IBOutlet EHIToggleButton *saveToggleButton;

@end

@implementation EHIPaymentInputView

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    // replace missing numpad return key with done toolbar
    self.cardNumberField.usesDoneToolbar = YES;
    self.cardNumberField.sensitive       = YES;
    self.expirationMonthField.usesDoneToolbar = YES;
    self.expirationYearField.usesDoneToolbar  = YES;
    self.cvvCodeField.usesDoneToolbar = YES;
    
    // use disabled button as card type
    self.cardNumberField.actionButtonType = EHIButtonTypePlaceholder;
    self.cardNumberField.actionButton.userInteractionEnabled = NO;
    
    self.saveToggleButton.style  = EHIToggleButtonStyleWhite;
    self.termsToggleButton.style = EHIToggleButtonStyleWhite;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIPaymentInputViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateCardNumber:)];
    [MTRReactor autorun:self action:@selector(invalidateCardType:)];
    [MTRReactor autorun:self action:@selector(invalidateInputErrors:)];
    [MTRReactor autorun:self action:@selector(invalidateTerms:)];
    [MTRReactor autorun:self action:@selector(invalidateSave:)];
    
    model.bind.map(@{
        source(model.nameTitle)                  : dest(self, .nameLabel.text),
        source(model.name)                       : dest(self, .nameField.text),
        source(model.namePlaceholder)            : dest(self, .nameField.placeholder),
        source(model.cardNumberTitle)            : dest(self, .cardNumberLabel.text),
        source(model.cardNumberPlaceholder)      : dest(self, .cardNumberField.placeholder),
        source(model.expirationTitle)            : dest(self, .expirationLabel.text),
        source(model.expirationMonthPlaceholder) : dest(self, .expirationMonthField.placeholder),
        source(model.expirationYearPlaceholder)  : dest(self, .expirationYearField.placeholder),
        source(model.cvvTitle)                   : dest(self, .cvvCodeLabel.text),
        source(model.cvv)                        : dest(self, .cvvCodeField.text),
        source(model.cvvPlaceholder)             : dest(self, .cvvCodeField.placeholder),
        source(model.policiesRead)               : dest(self, .termsToggleButton.selected),
        source(model.termsTitle)                 : dest(self, .termsLabel.attributedText),
        source(model.saveTitle)                  : dest(self, .saveLabel.text),
        source(model.saveCard)                   : dest(self, .saveToggleButton.selected),
        source(model.expirationMonth)            : dest(self, .expirationMonthField.text),
        source(model.expirationYear)             : dest(self, .expirationYearField.text)
    });
}

- (void)invalidateCardNumber:(MTRComputation *)computation
{
    self.cardNumberField.text = self.viewModel.cardNumber;
}

- (void)invalidateCardType:(MTRComputation *)computation
{
    NSString *imageName = self.viewModel.cardImageName;
    UIImage *image = imageName ? [UIImage imageNamed:imageName] : nil;
    
    [self.cardNumberField.actionButton setImage:image forState:UIControlStateNormal];
}

- (void)invalidateInputErrors:(MTRComputation *)computation
{
    UIColor *errorColor = [UIColor ehi_redColor];
    
    self.nameField.borderColor             = self.viewModel.showNameError ? errorColor : nil;
    self.cardNumberField.borderColor       = self.viewModel.showCardNumberError ? errorColor : nil;
    self.expirationMonthField.borderColor  = self.viewModel.showExpirationMonthError ? errorColor : nil;
    self.expirationYearField.borderColor   = self.viewModel.showExpirationYearError ? errorColor : nil;
    self.cvvCodeField.borderColor          = self.viewModel.showCvvError ? errorColor : nil;
}

- (void)invalidateTerms:(MTRComputation *)computation
{
    BOOL hideTerms = self.viewModel.hideTerms;
    
    MASLayoutPriority priority = hideTerms ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    
    [self.termsContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0).priority(priority);
    }];
}

- (void)invalidateSave:(MTRComputation *)computation
{
    BOOL hideSave = self.viewModel.hideSave;
    
    MASLayoutPriority priority = hideSave ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    
    [self.saveContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0).priority(priority);
    }];
}

- (IBAction)togglePoliciesRead:(EHIToggleButton *)sender
{
    self.viewModel.policiesRead = !sender.isSelected;
}

- (IBAction)toggleSaveCard:(EHIToggleButton *)sender
{
    self.viewModel.saveCard = !sender.isSelected;
}

# pragma mark - UITextFieldDelegate

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    NSString *text = [textField.text stringByReplacingCharactersInRange:range withString:string];
    
    if([textField isEqual:self.nameField]) {
        self.viewModel.name = text;
    } else if([textField isEqual:self.cardNumberField]) {
        self.viewModel.cardNumber = text;
        if(self.viewModel.cardNumber.ehi_stripNonDecimalCharacters.length >= 16) {
            [self.expirationMonthField becomeFirstResponder];
        }
    } else if([textField isEqual:self.expirationMonthField]) {
        self.viewModel.expirationMonth = text;
        if(self.viewModel.expirationMonth.length >= 2) {
            [self.expirationYearField becomeFirstResponder];
        }
    } else if([textField isEqual:self.expirationYearField]) {
        self.viewModel.expirationYear = text;
        if(self.viewModel.expirationYear.length >= 2) {
            [self.cvvCodeField becomeFirstResponder];
        }
    } else if([textField isEqual:self.cvvCodeField]) {
        self.viewModel.cvv = text;
    }
    
    // force reactions to run, this is necessary because iOS's quicktype cycle is faster than reactor's flush
    [[MTRReactor reactor] flush];
    
    // let view model process input and set text
    return NO;
}

- (void)textFieldDidEndEditing:(UITextField *)textField
{
    if([textField isEqual:self.expirationMonthField] && self.viewModel.expirationMonth.length < 2) {
        self.viewModel.expirationMonth = textField.text.integerValue != 0 ? textField.text : nil;
    }
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    
    return NO;
}

# pragma mark - Replaceability

+ (BOOL)isReplaceable
{
    return YES;
}

@end
