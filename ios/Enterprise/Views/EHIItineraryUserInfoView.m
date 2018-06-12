//
//  EHIItineraryUserInfoView.m
//  Enterprise
//
//  Created by Michael Place on 3/10/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIItineraryUserInfoView.h"
#import "EHIItineraryUserInfoViewModel.h"
#import "EHIRestorableConstraint.h"
#import "EHITextField.h"
#import "EHIButton.h"

@interface EHIItineraryUserInfoView () <UITextFieldDelegate, UIPickerViewDataSource, UIPickerViewDelegate>
@property (strong, nonatomic) EHIItineraryUserInfoViewModel *viewModel;

// driver age
@property (weak, nonatomic) IBOutlet UIView *ageContainer;
@property (weak, nonatomic) IBOutlet UILabel *ageLabel;
@property (weak, nonatomic) IBOutlet EHITextField *ageTextField;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *ageHeight;

// emerald added
@property (weak, nonatomic) IBOutlet UIView *emeraldAddedContainer;
@property (weak, nonatomic) IBOutlet UILabel *emeraldAddedLabel;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *emeraldAddedHeight;

// authenticated contract toggle
@property (weak, nonatomic) IBOutlet UIView *contractContainer;
@property (weak, nonatomic) IBOutlet UILabel *contractNameLabel;
@property (weak, nonatomic) IBOutlet UISwitch *contractToggle;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *contractHeight;

// code/coupon/account entry
@property (weak, nonatomic) IBOutlet UIView *couponContainer;
@property (weak, nonatomic) IBOutlet UILabel *couponInputLabel;
@property (weak, nonatomic) IBOutlet EHIButton *couponInsertButton;
@property (weak, nonatomic) IBOutlet EHITextField *couponInputField;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *couponTitleHeight;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *couponBottomSpacing;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *couponFieldHeight;

// promotion
@property (weak, nonatomic) IBOutlet UIView *promotionContainer;
@property (weak, nonatomic) IBOutlet UILabel *promotionAppliedLabel;
@property (weak, nonatomic) IBOutlet UILabel *promotionName;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *promotionContainerHeight;

// add emerald club
@property (weak, nonatomic) IBOutlet UIView *emeraldSignInContainer;
@property (weak, nonatomic) IBOutlet EHIButton *emeraldSignInButton;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *emeraldSignInHeight;

@end

@implementation EHIItineraryUserInfoView

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHIItineraryUserInfoViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    // style the age text field
    self.ageTextField.borderColor      = [UIColor ehi_greenColor];
    self.ageTextField.actionButtonType = EHIButtonTypeDownChevron;
    self.ageTextField.hidesCursor      = YES;
    self.ageTextField.usesDoneToolbar  = YES;
    
    UIPickerView *agePicker = [[UIPickerView alloc] init];
    agePicker.dataSource = self;
    agePicker.delegate = self;
    self.ageTextField.inputView = agePicker;
}

- (BOOL)resignFirstResponder
{
    BOOL firstBool = [super resignFirstResponder];
    BOOL secondBool = [self.couponInputField resignFirstResponder];
    BOOL thirdBool = [self.ageTextField resignFirstResponder];
    
    return firstBool || secondBool || thirdBool;
}

# pragma mark - Accessibility

- (void)registerAccessibilityIdentifiers
{
    [super registerAccessibilityIdentifiers];
    
    self.couponInsertButton.accessibilityIdentifier = EHIItineraryAddContractKey;
    self.couponInputField.accessibilityIdentifier   = EHIItineraryContractKey;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIItineraryUserInfoViewModel *)model
{
    [super registerReactions:model];

    [MTRReactor autorun:self action:@selector(updateAgeVisibility:)];
    [MTRReactor autorun:self action:@selector(updateCodeEntryFieldVisibility:)];
    [MTRReactor autorun:self action:@selector(updateEmeraldVisibility:)];
    [MTRReactor autorun:self action:@selector(updateModifyVisibility:)];
    
    model.bind.map(@{
        source(model.driverAgeTitle)           : dest(self, .ageLabel.text),
        
        source(model.emeraldAddedTitle)        : dest(self, .emeraldAddedLabel.text),
        source(model.emeraldSignInTitle)       : dest(self, .emeraldSignInButton.ehi_title),
        
        source(model.contractNameTitle)        : dest(self, .contractNameLabel.attributedText),
        
        source(model.couponButtonTitle)        : dest(self, .couponInsertButton.ehi_title),
        source(model.couponButtonImageName)    : dest(self, .couponInsertButton.ehi_imageName),
        source(model.couponInputTitle)         : dest(self, .couponInputLabel.text),
        source(model.couponInputPlaceholder)   : dest(self, .couponInputField.placeholder),
        
        source(model.discountCode)             : dest(self, .couponInputField.text),
        source(model.promotionTitle)           : dest(self, .promotionName.text),
        
        source(model.shouldUseContractCode)    : dest(self, .contractToggle.on),
        source(model.promotionAppliedTitle)    : dest(self, .promotionAppliedLabel.text),
    });
}

- (void)updateModifyVisibility:(MTRComputation *)computation
{
    BOOL isModify = self.viewModel.isModify;
    
    self.emeraldSignInHeight.isDisabled = isModify;

    self.couponInsertButton.ehi_titleColor = isModify ? [UIColor ehi_silverColor] : [UIColor ehi_greenColor];
    
    self.couponContainer.alpha = isModify ? 0.5f : 1.0f;
    self.couponContainer.userInteractionEnabled = !isModify;
    
    self.contractContainer.alpha = isModify ? 0.5f : 1.0f;
    self.contractContainer.userInteractionEnabled = !isModify;
    
    self.promotionContainer.alpha = isModify ? 0.5f : 1.0f;
    self.promotionContainer.userInteractionEnabled = !isModify;
    
    self.emeraldAddedContainer.alpha = isModify ? 0.5f : 1.0f;
    self.emeraldAddedContainer.userInteractionEnabled = !isModify;
}

- (void)updateAgeVisibility:(MTRComputation *)computation
{
    BOOL hideAgeInput = self.viewModel.isAuthenticated || self.viewModel.ageOptions.count == 0 || self.viewModel.isModify;

    if(!hideAgeInput) {
        [self updateDefaultAgeSelection];
    }
    
    self.ageHeight.isDisabled = hideAgeInput;
    [self setNeedsUpdateConstraints];
    
    UIView.animate(!computation.isFirstRun).duration(0.25).transform(^{
        self.ageContainer.alpha = hideAgeInput ? 0.0 : 1.0;
        [self layoutIfNeeded];
    }).start(nil);
}

- (void)updateDefaultAgeSelection
{
    EHILocationRenterAge *selectedAge = self.viewModel.ageOptions[self.viewModel.selectedAgeIndex];
    self.ageTextField.text = selectedAge.text;
    
    UIPickerView *agePicker = (UIPickerView *)self.ageTextField.inputView;
    [agePicker selectRow:self.viewModel.selectedAgeIndex inComponent:0 animated:NO];
}

- (void)updateCodeEntryFieldVisibility:(MTRComputation *)computation
{
    // check our dependencies
    BOOL shouldShowPromotionContainer = self.viewModel.shouldShowPromotionCode;
    BOOL shouldShowContractToggle     = self.viewModel.shouldShowContractToggle && !shouldShowPromotionContainer;
    BOOL shouldShowCodeInputField     = self.viewModel.shouldShowCouponInputField && !shouldShowPromotionContainer;
    BOOL shouldShowDiscountButton     = self.viewModel.shouldShowCouponInputButton && !shouldShowPromotionContainer;
    BOOL automaticallyShowKeyboard    = self.viewModel.automaticallyShowKeyboard && !shouldShowPromotionContainer;
    
    // update any constraints
    self.promotionContainerHeight.isDisabled = !shouldShowPromotionContainer;
    self.contractHeight.isDisabled = !shouldShowContractToggle;
    self.couponTitleHeight.isDisabled = !shouldShowCodeInputField && !shouldShowDiscountButton;
    self.couponBottomSpacing.isDisabled = !shouldShowCodeInputField && !shouldShowDiscountButton;
    self.couponFieldHeight.isDisabled = !shouldShowCodeInputField;
    
    // and prepare to animate them
    [self setNeedsUpdateConstraints];
    
    UIView.animate(!computation.isFirstRun).duration(0.25).transform(^{
        // update the visibility of the components
        self.promotionContainer.alpha = shouldShowPromotionContainer ? 1.0f : 0.0f;
        self.contractContainer.alpha  = shouldShowContractToggle ? 1.0f : 0.0f;
        self.couponInputLabel.alpha   = shouldShowCodeInputField ? 1.0f : 0.0f;
        self.couponInputField.alpha   = self.couponInputLabel.alpha;
        self.couponInsertButton.alpha = shouldShowDiscountButton ? 1.0f : 0.0f;
        
        // force the contraint changes to animate
        [self layoutIfNeeded];
    }).start(^(BOOL finished) {
        // if the text field is collapsing, dismiss the keyboard
        if(!shouldShowCodeInputField) {
            [self.couponInputField resignFirstResponder];
        }
        // else the text field is expanding, present the keyboard
        else if(automaticallyShowKeyboard) {
            [self.couponInputField becomeFirstResponder];
        }
    });
}

- (void)updateEmeraldVisibility:(MTRComputation *)computation
{
    BOOL authenticated = self.viewModel.isAuthenticated;
    BOOL emeraldUser   = self.viewModel.isEmeraldUser;

    // update constraints
    self.emeraldAddedHeight.isDisabled  = !emeraldUser;
    self.emeraldSignInHeight.isDisabled = authenticated || self.viewModel.isModify;
    
    // prepare to animate
    [self setNeedsUpdateConstraints];
    
    UIView.animate(!computation.isFirstRun).duration(0.25).transform(^{
        // update visibility of components
        self.emeraldAddedContainer.alpha  = !emeraldUser ? 0.0 : 1.0;
        self.emeraldSignInContainer.alpha = !authenticated ? 1.0 : 0.0;
        
        // force constraint animations
        [self layoutIfNeeded];
    }).start(nil);
}

# pragma mark - Text Field Delegate

- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField
{
    return YES;
}

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    if ([textField isEqual:self.couponInputField]) {
        self.viewModel.discountCode = [textField.text stringByReplacingCharactersInRange:range withString:string];
        
        // force reactions to run, this is necessary because iOS's quicktype cycle is faster than reactor's flush
        [[MTRReactor reactor] flush];
    }
    return NO;
}

- (BOOL)textFieldShouldClear:(UITextField *)textField
{
    if ([textField isEqual:self.couponInputField]) {
        self.viewModel.discountCode = nil;
    }
    return NO;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    // dismiss keyboard when return is tapped
    [textField resignFirstResponder];
    
    // notify view model
    if ([textField isEqual:self.couponInputField]) {
        [self.viewModel commitCodeInput];
    }
    
    return YES;
}

# pragma mark - Age Picker Delegate

- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    return self.viewModel.ageOptions.count;
}

- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return  1;
}

- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    // allow reuse cycle to complete so picker view and viewModel.ageOptions are in sync
    if(self.viewModel.ageOptions.count <= row) {
        return nil;
    }
    
    EHILocationRenterAge *age = self.viewModel.ageOptions[row];
    return age.text;
}

- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component {
    EHILocationRenterAge *age = self.viewModel.ageOptions[row];
    self.ageTextField.text = age.text;
    self.viewModel.selectedAgeIndex = row;
}


# pragma mark - View Actions

- (IBAction)didTapActionButton:(UITextField *)textField
{
    textField.isFirstResponder ? [textField resignFirstResponder] : [textField becomeFirstResponder];
}

- (IBAction)didTapCodeAdditionButton:(id)sender
{
    [self.viewModel showCouponInput];
}

- (IBAction)didTapCorporateCodeInclusionSwitch:(UISwitch *)sender
{
    self.viewModel.shouldUseContractCode = sender.on;
}

- (IBAction)didTapRemovePromotion:(id)sender
{
    [self.viewModel removePromotion];
}

- (IBAction)didTapEmeraldSignInButton:(id)sender
{
    [self.viewModel signInEmeraldClub];
}

- (IBAction)didTapRemoveEmeraldButton:(id)sender
{
    [self.viewModel removeEmeraldClub];
}

@end
