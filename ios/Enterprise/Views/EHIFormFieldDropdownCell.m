//
//  EHIFormFieldDropdownCell.m
//  Enterprise
//
//  Created by Alex Koller on 5/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldDropdownCell.h"
#import "EHIFormFieldDropdownViewModel.h"
#import "EHITextField.h"

@interface EHIFormFieldDropdownCell () <UITextFieldDelegate, UIPickerViewDataSource, UIPickerViewDelegate>
@property (strong, nonatomic) EHIFormFieldDropdownViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIButton *dropdownButton;
@property (weak  , nonatomic) IBOutlet EHITextField *textField;
@property (weak  , nonatomic) IBOutlet UIView *touchView;
@property (strong, nonatomic) UIPickerView *picker;
@property (assign, nonatomic) BOOL shouldReloadPicker;
@end

@implementation EHIFormFieldDropdownCell

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.dropdownButton.showsBorder = YES;
    self.dropdownButton.borderColor = [UIColor ehi_greenColor];

    self.textField.inputView = self.picker;
    self.textField.usesDoneToolbar = YES;
    self.textField.tintColor = [UIColor clearColor];
    
    // pass touches on entire view to text field
    UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self.textField action:@selector(becomeFirstResponder)];
    [self.touchView addGestureRecognizer:tapGesture];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIFormFieldDropdownViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateSelectedOption:)];
    
    model.bind.map(@{
        source(model.inputValue)   : dest(self, .textField.text),
        source(model.placeholder)  : dest(self, .textField.placeholder),
        source(model.options)      : ^(NSArray *options) {
            [self.picker reloadAllComponents];
        }
    });
}

- (void)invalidateSelectedOption:(MTRComputation *)computation
{
    NSUInteger selectedRow = self.viewModel.selectedOption;

    // perform selection if a value was selected
    if(selectedRow != EHIFormFieldDropdownValueNone) {
        // if we are not selecting the row from `viewDidLoad` the selectedRow will be different from what we want.
        // reloading it does the work, but there may be another cleaner solution
        [self.picker reloadAllComponents];
        [self.picker ehi_selectRow:selectedRow animated:NO];
    }
}

# pragma mark - UIResponder

- (BOOL)becomeFirstResponder
{
    return [self.textField becomeFirstResponder] || [self.picker becomeFirstResponder];
}

# pragma mark - UITextFieldDelegate

- (void)textFieldDidBeginEditing:(UITextField *)textField
{
    [self ehi_performAction:@selector(didBeginEditingPrimaryInputForCell:) withSender:self];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [self resignFirstResponder];
    
    self.viewModel.selectedOption = [self.picker ehi_selectedRow];
    
    return NO;
}

# pragma mark - UIPickerViewDataSource

- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 1;
}

- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    return [self.viewModel.options count];
}

# pragma mark - UIPickerViewDelegate

- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    // allow reuse cycle to complete so picker view and viewModel.options are in sync
    if (self.viewModel.options.count <= row) {
        return nil;
    }
    
    return self.viewModel.options[row];
}

# pragma mark - Getters

- (UIPickerView *)picker
{
    if(!_picker) {
        _picker = [UIPickerView new];
        _picker.delegate = self;
        _picker.dataSource = self;
    }
    
    return _picker;
}

@end
