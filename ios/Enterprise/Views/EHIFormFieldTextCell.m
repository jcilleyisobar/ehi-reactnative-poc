//
//  EHIFormFieldTextCell.m
//  Enterprise
//
//  Created by Alex Koller on 5/1/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldTextCell.h"
#import "EHIFormFieldTextViewModel.h"
#import "EHILabel.h"
#import "EHITextField.h"
#import "EHIRestorableConstraint.h"

@interface EHIFormFieldTextCell () <UITextFieldDelegate, UIPickerViewDataSource, UIPickerViewDelegate>
@property (strong, nonatomic) EHIFormFieldTextViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHITextField *textField;
@property (weak  , nonatomic) IBOutlet EHIButton *categoryButton;
@property (weak  , nonatomic) IBOutlet EHITextField *categoryInputField;
@property (weak  , nonatomic) IBOutlet EHIButton *deleteButton;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *deleteButtonWidth;
@property (weak  , nonatomic) IBOutlet NSLayoutConstraint *textFieldToDeleteSpacing;
@property (strong, nonatomic) UIPickerView *picker;
@end

@implementation EHIFormFieldTextCell

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.categoryButton.showsBorder = YES;
    self.categoryButton.borderColor = [UIColor ehi_greenColor];
    self.categoryButton.imageHorizontalAlignment = UIControlContentHorizontalAlignmentRight;
    
    self.categoryInputField.usesDoneToolbar = YES;
    
    self.deleteButton.showsBorder = YES;
    self.deleteButton.borderColor = [UIColor ehi_redColor];
}

- (void)updateConstraints
{
    [super updateConstraints];
    
    // toggle category selection
    BOOL showCategorySelection = self.viewModel.allowsCategorySelection;
    self.categoryButton.hidden = !showCategorySelection;
    
    // optionally allow compression resistance of in between category input to override these views hugging
    self.textFieldToDeleteSpacing.priority = showCategorySelection ? 249 : 999;
    
    self.deleteButtonWidth.isDisabled = self.viewModel.hidesDeleteButton;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIFormFieldTextViewModel *)model
{
    [super registerReactions:model];
 
    [MTRReactor autorun:self action:@selector(invalidateSelectedCategory:)];
    [MTRReactor autorun:self action:@selector(invalidateCategoryName:)];
    [MTRReactor autorun:self action:@selector(invalidateTextFieldInput:)];
    
    model.bind.map(@{
        source(model.placeholder)       : dest(self, .textField.placeholder),
        source(model.sensitive)         : dest(self, .textField.sensitive),
        source(model.keyboardType)      : ^(NSNumber *type) {
            self.textField.keyboardType = [type unsignedIntegerValue];
        },
        source(model.isPhoneField)      : dest(self, .textField.usesDoneToolbar),
        source(model.hidesDeleteButton) : ^(NSNumber *hideDelete) {
            [self setNeedsUpdateConstraints];
        },
        source(model.allowsCategorySelection) : ^(NSNumber *allowsCategory) {
            [self setNeedsUpdateConstraints];
        },
        source(model.captalizationMode) : ^(NSNumber *captalizationMode) {
            self.textField.autocapitalizationType = [captalizationMode integerValue];
        },
        source(model.returnKeyType) : ^(NSNumber *returnKeyType) {
            self.textField.returnKeyType = [returnKeyType integerValue];
        },
    });
}

- (void)invalidateSelectedCategory:(MTRComputation *)computation
{
    NSUInteger selectedRow = self.viewModel.selectedCategory;
    
    // select non-empty new value
    [self.picker ehi_selectRow:selectedRow animated:NO];
}

- (void)invalidateCategoryName:(MTRComputation *)computation
{
    NSString *categoryName = self.viewModel.selectedCategoryName;
    
    UIView.animate(!computation.isFirstRun).duration(0.25f)
        .transform(^{
            [self.categoryButton setTitle:categoryName forState:UIControlStateNormal];
            [self layoutIfNeeded];
        }).start(nil);
}

- (void)invalidateTextFieldInput:(MTRComputation *)computation
{
    EHIFormattedPhone *phoneModel = self.viewModel.phoneModel;
    
    // update our phone model
    if(self.textField.phoneModel != phoneModel) {
        self.textField.phoneModel = phoneModel;
    }
    
    // update input if not done through phone model
    if(!phoneModel) {
        self.textField.text = self.viewModel.inputValue;
    }
}

# pragma mark - UIResponder

- (BOOL)becomeFirstResponder
{
    return [self.textField becomeFirstResponder];
}

# pragma mark - UITextFieldDelegate

- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField
{
    // use the picker as our input view
    if([textField isEqual:self.categoryInputField]) {
        textField.inputView = self.picker;
    }
    
    return YES;
}

- (void)textFieldDidBeginEditing:(UITextField *)textField
{
    if([textField isEqual:self.textField]) {
        [self ehi_performAction:@selector(didBeginEditingPrimaryInputForCell:) withSender:self];
    }
}

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    NSString *text = [textField.text stringByReplacingCharactersInRange:range withString:string];
    self.viewModel.inputValue = text;
    
    // force reactions to run, this is necessary because iOS's quicktype cycle is faster than reactor's flush
    [[MTRReactor reactor] flush];
    
    // update via reactions on view model
    return NO;
}

- (BOOL)textFieldShouldClear:(UITextField *)textField
{
    self.viewModel.inputValue = nil;
    
    return NO;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    // make selection and resign secondary responder if category input
    if([textField isEqual:self.categoryInputField]) {
        self.viewModel.selectedCategory = [self.picker ehi_selectedRow];
        [textField resignFirstResponder];
    }
    
    [self resignFirstResponder];
    
    return NO;
}

# pragma mark - UIPickerViewDataSource

- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 1;
}

- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    return [self.viewModel.categoryOptions count];
}

# pragma mark - UIPickerViewDelegate

- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    return self.viewModel.categoryOptions[row];
}

- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component
{
    self.viewModel.selectedCategory = row;
}

# pragma mark - Getters

- (UIPickerView *)picker
{
    if(!_picker) {
        _picker = [UIPickerView new];
        _picker.dataSource = self;
        _picker.delegate = self;
    }
    
    return _picker;
}

# pragma mark - Actions

- (IBAction)didTapCategoryButton:(id)sender
{
    [self.categoryInputField becomeFirstResponder];
}

- (IBAction)didTapDeleteButton:(id)sender
{
    [self ehi_performAction:@selector(didTapDeleteButtonForCell:) withSender:self];
}

@end
