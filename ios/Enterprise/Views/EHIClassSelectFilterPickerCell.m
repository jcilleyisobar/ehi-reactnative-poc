//
//  EHIClassSelectFilterPickerCell.m
//  Enterprise
//
//  Created by mplace on 4/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIClassSelectFilterPickerCell.h"
#import "EHIClassSelectFilterPickerCellViewModel.h"
#import "EHITextField.h"
#import "EHILabel.h"

@interface EHIClassSelectFilterPickerCell () <UITextFieldDelegate>
@property (strong, nonatomic) EHIClassSelectFilterPickerCellViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet EHITextField *filterPickerTextField;
@property (strong, nonatomic) UIPickerView *picker;
@end

@implementation EHIClassSelectFilterPickerCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIClassSelectFilterPickerCellViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    // style the picker text field
    self.filterPickerTextField.borderColor = [UIColor ehi_greenColor];
    self.filterPickerTextField.actionButtonType = EHIButtonTypeDownChevron;
    self.filterPickerTextField.actionButton.userInteractionEnabled = NO;
    self.filterPickerTextField.usesDoneToolbar = YES;
    
    // hide the text field cursor as we will be using a picker to populate
    self.filterPickerTextField.tintColor = [UIColor clearColor];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIClassSelectFilterPickerCellViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title)            : dest(self, .titleLabel.text),
        source(model.filterValueTitle) : dest(self, .filterPickerTextField.text),
    });
}

# pragma mark - UITextFieldDelegate

- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField
{
    // use the picker as our input view
    textField.inputView = self.picker;
    
    return YES;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    
    NSInteger row = [self.picker selectedRowInComponent:0];
    [self.viewModel selectFilterValueAtIndex:row];
    [self ehi_performAction:@selector(didDismissPickerForCell:) withSender:self];
    
    return NO;
}

# pragma mark - Getter

- (UIPickerView *)picker
{
    if (!_picker) {
        _picker = [UIPickerView new];
        _picker.delegate = self.viewModel;
        _picker.dataSource = self.viewModel;
    }
    
    return _picker;
}

# pragma mark - Layout

+ (EHILayoutMetrics *)metrics
{
    EHILayoutMetrics *metrics = [self.defaultMetrics copy];
    metrics.fixedSize = (CGSize){.height = 60, .width = EHILayoutValueNil};
    return metrics;
}

@end
