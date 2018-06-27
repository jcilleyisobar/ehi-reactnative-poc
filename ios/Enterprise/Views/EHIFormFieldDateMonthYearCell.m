//
//  EHIFormFieldDateMonthYearCell.m
//  Enterprise
//
//  Created by Alex Koller on 5/7/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldDateMonthYearCell.h"
#import "EHIFormFieldDateMonthYearViewModel.h"
#import "EHITextField.h"
#import "EHIButton.h"
#import "EHIBarButtonItem.h"



@interface EHIFormFieldDateMonthYearCell () <UITextFieldDelegate, UIPickerViewDataSource, UIPickerViewDelegate>
@property (strong, nonatomic) EHIFormFieldDateMonthYearViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHITextField *monthField;
@property (weak  , nonatomic) IBOutlet EHITextField *yearField;
@property (weak  , nonatomic) IBOutlet UIView *touchView;

@property (strong, nonatomic) IBOutletCollection(EHIButton) NSArray *dropdownButtons;

@property (strong, nonatomic) UIPickerView *picker;
@property (strong, nonatomic) UIToolbar *pickerToolbar;

@end

@implementation EHIFormFieldDateMonthYearCell

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.monthField.inputView = self.picker;
    self.monthField.inputAccessoryView = self.pickerToolbar;
    self.monthField.tintColor = [UIColor clearColor];
    
    // format dropdown buttons
    for(EHIButton *button in self.dropdownButtons) {
        button.showsBorder = YES;
        button.borderColor = [UIColor ehi_greenColor];
    }
    
    // pass touches on entire view to text field
    UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self.monthField action:@selector(becomeFirstResponder)];
    [self.touchView addGestureRecognizer:tapGesture];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIFormFieldDateMonthYearViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.monthText)         : dest(self, .monthField.text),
        source(model.monthPlaceholder)  : dest(self, .monthField.placeholder),
        source(model.yearText)          : dest(self, .yearField.text),
        source(model.yearPlaceholder)   : dest(self, .yearField.placeholder)
    });
}

# pragma mark - UITextFieldDelegate

- (void)textFieldDidBeginEditing:(UITextField *)textField
{
    if([textField isEqual:self.monthField]) {
        [self ehi_performAction:@selector(didBeginEditingPrimaryInputForCell:) withSender:self];
    }
}

# pragma mark - UIResponder

- (BOOL)becomeFirstResponder
{
    return [self.monthField becomeFirstResponder] || [self.yearField becomeFirstResponder] || [self.picker becomeFirstResponder];
}

# pragma mark - UIPickerViewDataSource

- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 2;
}

- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    return [self.viewModel numberOfRowsInComponent:component];
}

# pragma mark - UIPickerViewDelegate

- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    return [self.viewModel titleForRow:row inComponent:component];
}

- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component
{
    [self.viewModel didSelectRow:row inComponent:component];
}

# pragma mark - Actions

- (void)dismissPicker:(id)sender
{
    NSInteger monthRow = [self.picker selectedRowInComponent:EHIFormFieldDateMonthYearPickerComponentMonth];
    [self.viewModel didSelectRow:monthRow inComponent:EHIFormFieldDateMonthYearPickerComponentMonth];
    NSInteger yearRow = [self.picker selectedRowInComponent:EHIFormFieldDateMonthYearPickerComponentYear];
    [self.viewModel didSelectRow:yearRow inComponent:EHIFormFieldDateMonthYearPickerComponentYear];
    
    [self resignFirstResponder];
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

- (UIToolbar *)pickerToolbar
{
    if(!_pickerToolbar) {
        EHIBarButtonItem *doneButton = [EHIBarButtonItem buttonWithType:EHIButtonTypeDone target:self action:@selector(dismissPicker:)];
        [doneButton setTintColor:[UIColor ehi_greenColor]];
        
        // create toolbar and add the done button as an item
        _pickerToolbar = [[UIToolbar alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(self.picker.bounds), 44.0f)];
        _pickerToolbar.items = @[[EHIBarButtonItem flexibleSpace], doneButton];
    }
    
    return _pickerToolbar;
}

@end
