//
//  EHIReviewPaymentOptionsCell.m
//  Enterprise
//
//  Created by mplace on 6/5/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReviewPaymentOptionsCell.h"
#import "EHIReviewPaymentOptionsViewModel.h"
#import "EHIRestorableConstraint.h"
#import "EHIToggleButton.h"
#import "EHITextField.h"
#import "EHIBarButtonItem.h"

@interface EHIReviewPaymentOptionsCell () <UITextFieldDelegate, UIPickerViewDelegate, UIPickerViewDataSource>
@property (strong, nonatomic) EHIReviewPaymentOptionsViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *contentContainer;
@property (strong, nonatomic) UIPickerView *picker;
@property (strong, nonatomic) UIToolbar *pickerToolbar;

@property (weak  , nonatomic) UILabel *titleLabel;

// billing
@property (weak, nonatomic) IBOutlet UIView *billingContainer;
@property (weak, nonatomic) IBOutlet UILabel *billingTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *billingSubtitleLabel;
@property (weak, nonatomic) IBOutlet EHIToggleButton *billingToggleButton;
@property (weak, nonatomic) IBOutlet EHITextField *billingAccountTextField;
@property (weak, nonatomic) IBOutlet EHITextField *billingEntryTextField;
@property (weak, nonatomic) IBOutlet UILabel *billingAccountTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *billingNumberTitleLabel;

// payment
@property (weak, nonatomic) IBOutlet UIView *paymentContainer;
@property (weak, nonatomic) IBOutlet UILabel *paymentTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *paymentSubtitleLabel;
@property (weak, nonatomic) IBOutlet EHIToggleButton *paymentToggleButton;
@property (weak, nonatomic) IBOutlet EHITextField *paymentAccountTextField;
@property (weak, nonatomic) IBOutlet UILabel *paymentAccountTitleLabel;

// constraints
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *billingNumberHeight;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *billingAccountContainerHeight;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *billingEntryContainerHeight;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *paymentAccountContainerHeight;

@end

@implementation EHIReviewPaymentOptionsCell

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    [self styleTextField:self.paymentAccountTextField];
    [self styleTextField:self.billingAccountTextField];
}

- (void)styleTextField:(EHITextField *)textField
{
    textField.borderType  = EHITextFieldBorderButton | EHITextFieldBorderField;
    textField.borderColor = [UIColor ehi_greenColor];
    textField.actionButtonType = EHIButtonTypeDownChevron;
    textField.actionButton.userInteractionEnabled = NO;
    textField.hidesCursor = YES;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIReviewPaymentOptionsViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(updatePaymentOptionState:)];
    [MTRReactor autorun:self action:@selector(updateBillingNumberTitle:)];
    
    model.bind.map(@{
        source(model.title)                   : dest(self, .titleLabel.text),
        source(model.billingTitle)            : dest(self, .billingTitleLabel.text),
        source(model.paymentTitle)            : dest(self, .paymentTitleLabel.text),
        source(model.paymentSubtitle)         : dest(self, .paymentSubtitleLabel.attributedText),
        source(model.customBillingCode)       : dest(self, .billingEntryTextField.text),
        source(model.billingAccountTitle)     : ^(NSAttributedString *title){
                                                    self.billingAccountTitleLabel.attributedText = title;
                                                    self.billingAccountTextField.attributedText = title;
                                                },
        source(model.paymentAccountTitle)     : ^(NSAttributedString *title){
                                                    self.paymentAccountTitleLabel.attributedText = title;
                                                    self.paymentAccountTextField.attributedText = title;
                                                },
        source(model.billingSubtitle)         : dest(self, .billingSubtitleLabel.attributedText),
        source(model.billingEntryHintTitle)   : dest(self, .billingEntryTextField.placeholder),
        source(model.shouldHideBillingEntry)  : dest(self, .billingEntryContainerHeight.isDisabled),
        source(model.shouldHideBillingPicker) : dest(self, .billingAccountContainerHeight.isDisabled),
        source(model.shouldHidePaymentPicker) : dest(self, .paymentAccountContainerHeight.isDisabled),
    });
}

- (void)updatePaymentOptionState:(MTRComputation *)computation
{
    EHIReviewPaymentOption option = self.viewModel.currentPaymentOption;
    
    self.billingToggleButton.selected = option == EHIReviewPaymentOptionBilling;
    self.paymentToggleButton.selected = option == EHIReviewPaymentOptionPayment;
    
    // animate the changes
    [self setNeedsUpdateConstraints];
    UIView.animate(!computation.isFirstRun).duration(0.3).transform(^{
        self.billingContainer.alpha = option == EHIReviewPaymentOptionBilling ? 1.0f : 0.0f;
        self.paymentContainer.alpha = option == EHIReviewPaymentOptionPayment ? 1.0f : 0.0f;
        [self layoutIfNeeded];
    }).start(nil);
    
    // update containers
    MASLayoutPriority priority = option == EHIReviewPaymentOptionBilling ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    [self.billingContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0f).priority(priority);
    }];

    priority = option == EHIReviewPaymentOptionPayment ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    [self.paymentContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0f).priority(priority);
    }];
    
    [self ehi_performAction:@selector(didResizeReviewPaymentOptionsCell:) withSender:self];
}

- (void)updateBillingNumberTitle:(MTRComputation *)computation
{
    NSString *title = self.viewModel.billingNumberTitle;
    
    self.billingNumberHeight.isDisabled = title.length == 0;
    self.billingNumberTitleLabel.text = title;
}

# pragma mark - Actions

- (IBAction)togglePaymentOption:(EHIToggleButton *)sender
{
    // if the sender is already selected, bail out
    if(sender.selected) {
        return;
    }
    
    // dismiss the picker if its active
    [self.textFieldForCurrentPaymentOption resignFirstResponder];
    
    self.viewModel.currentPaymentOption = sender == self.billingToggleButton
        ? EHIReviewPaymentOptionBilling
        : EHIReviewPaymentOptionPayment;
}

- (void)dismissFilterPicker:(id)sender
{
    [self.textFieldForCurrentPaymentOption resignFirstResponder];
}

# pragma mark - UITextFieldDelegate

- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField
{
    // show the picker for all fields aside from billing code entry
    if(textField != self.billingEntryTextField) {
        // update the view model with the payment option, determined by the selected text field
        self.viewModel.currentPaymentOption = textField == self.billingAccountTextField
            ? EHIReviewPaymentOptionBilling
            : EHIReviewPaymentOptionPayment;
        
        // use the picker as our input view
        textField.inputView = self.picker;
        textField.inputAccessoryView = self.pickerToolbar;
        
        [self.picker reloadAllComponents];
    }
    
    return YES;
}

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    BOOL isBillingEntry = textField == self.billingEntryTextField;
    
    // if this is the billing entry text field, update the custom billing code
    if(isBillingEntry) {
        NSString *result = [textField.text stringByReplacingCharactersInRange:range withString:string];
        [self.viewModel updateCustomBillingCode:result];
    }
    
    // force reactions to run, this is necessary because iOS's quicktype cycle is faster than reactor's flush
    [[MTRReactor reactor] flush];
    
    // hijack the text field update cycle if we are the billing text field
    return !isBillingEntry;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];

    return YES;
}

- (BOOL)textFieldShouldClear:(UITextField *)textField
{
    // if we are the billing text field, clear out the custom billing code
    if(textField == self.billingEntryTextField) {
        [self.viewModel updateCustomBillingCode:nil];
    }
    
    return YES;
}

# pragma mark - UIPickerViewDataSource

- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 1;
}

- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    return [self.viewModel numberOfPaymentMethods];
}

# pragma mark - UIPickerViewDelegate

- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    return [self.viewModel titleForPaymentMethodAtIndex:row];
}

- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component
{
    [self.viewModel selectPaymentMethodAtIndex:row];
    [self ehi_performAction:@selector(didResizeReviewPaymentOptionsCell:) withSender:self];
}

# pragma mark - Computed

- (EHITextField *)textFieldForCurrentPaymentOption
{
    switch (self.viewModel.currentPaymentOption) {
        case EHIReviewPaymentOptionBilling:
            return self.billingAccountTextField;
        case EHIReviewPaymentOptionPayment:
            return self.paymentAccountTextField;
    }
}

# pragma mark - Getter

- (UIPickerView *)picker
{
    if (!_picker) {
        _picker = [UIPickerView new];
        _picker.delegate = self;
        _picker.dataSource = self;
    }
    
    return _picker;
}

- (UIToolbar *)pickerToolbar
{
    if (!_pickerToolbar) {
        UIBarButtonItem *doneButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(dismissFilterPicker:)];
        [doneButton setTintColor:[UIColor ehi_greenColor]];
        
        // create toolbar and add the done button as an item
        _pickerToolbar = [[UIToolbar alloc] initWithFrame:CGRectMake(0, 0, self.picker.bounds.size.width, 44)];
        _pickerToolbar.items = @[[EHIBarButtonItem flexibleSpace], doneButton];
    }
    
    return _pickerToolbar;
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.contentContainer.frame) + EHIMediumPadding
    };
}

@end
