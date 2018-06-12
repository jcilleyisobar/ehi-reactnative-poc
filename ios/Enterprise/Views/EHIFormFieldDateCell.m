//
//  EHIFormFieldDateCell.m
//  Enterprise
//
//  Created by Alex Koller on 5/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldDateCell.h"
#import "EHIFormFieldDateViewModel.h"
#import "EHITextField.h"
#import "EHIButton.h"
#import "EHIBarButtonItem.h"

@interface EHIFormFieldDateCell () <UITextFieldDelegate>
@property (strong, nonatomic) EHIFormFieldDateViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHITextField *textField;
@property (weak  , nonatomic) IBOutlet EHIButton *dropdownButton;
@property (strong, nonatomic) UIDatePicker *picker;
@end

@implementation EHIFormFieldDateCell

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.textField.inputView = self.picker;
    self.textField.usesDoneToolbar = YES;
    self.textField.tintColor = [UIColor clearColor];
    
    self.dropdownButton.showsBorder = YES;
    self.dropdownButton.borderColor = [UIColor ehi_greenColor];
    
    // pass touches on entire view to text field
    UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self.textField action:@selector(becomeFirstResponder)];
    [self.dropdownButton addGestureRecognizer:tapGesture];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIFormFieldDateViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.dateString)        : dest(self, .textField.text),
        source(model.placeholder)       : dest(self, .textField.placeholder),
        source(model.pickerMode)        : dest(self, .picker.datePickerMode),
        source(model.minimumDate)       : dest(self, .picker.minimumDate),
        source(model.maximumDate)       : dest(self, .picker.maximumDate),
        source(model.inputValue)        : ^(id date) {
            if(date && [date isKindOfClass:NSDate.class]) {
                [self.picker setDate:date animated:NO];
            }
        }
    });
}

# pragma mark - UITextFieldDelegate

- (void)textFieldDidBeginEditing:(UITextField *)textField
{
    [self ehi_performAction:@selector(didBeginEditingPrimaryInputForCell:) withSender:self];
}

- (BOOL)textFieldShouldClear:(UITextField *)textField
{
    self.viewModel.inputValue = nil;
    return YES;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    self.viewModel.inputValue = self.picker.date;
    
    [self resignFirstResponder];
    
    return NO;
}

# pragma mark - UIResponder

- (BOOL)becomeFirstResponder
{
    return YES;
}

# pragma mark - Actions

- (void)didChangeDatePickerValue:(UIDatePicker *)sender
{
    self.viewModel.inputValue = sender.date;
}

# pragma mark - Getters

- (UIDatePicker *)picker
{
    if(!_picker) {
        _picker = [UIDatePicker new];
        [_picker addTarget:self action:@selector(didChangeDatePickerValue:) forControlEvents:UIControlEventValueChanged];
    }
    
    return _picker;
}

@end
