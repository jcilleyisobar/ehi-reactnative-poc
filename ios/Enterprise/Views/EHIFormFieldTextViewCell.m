//
//  EHIFormFieldTextViewCell.m
//  Enterprise
//
//  Created by Alex Koller on 6/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldTextViewCell.h"
#import "EHIFormFieldTextViewViewModel.h"

@interface EHIFormFieldTextViewCell () <UITextViewDelegate>
@property (strong, nonatomic) EHIFormFieldTextViewViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UITextView *textView;
@property (weak  , nonatomic) IBOutlet UITextView *placeholderTextView;
@end

@implementation EHIFormFieldTextViewCell

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.textView.layer.borderWidth = 1.0f;
    self.textView.layer.borderColor = [UIColor ehi_grayColor2].CGColor;
    
    UIEdgeInsets textInsets = UIEdgeInsetsMake(15.0, 10.0, 15.0, 10.0);
    self.textView.textContainerInset = textInsets;
    self.placeholderTextView.textContainerInset = textInsets;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIFormFieldTextViewViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:^(MTRComputation *computation) {
        NSString *input = self.viewModel.inputValue;
        
        self.textView.text = input;
        self.placeholderTextView.text = input == nil ? self.viewModel.placeholder : nil;
    }];
    
    model.bind.map(@{
        source(model.keyboardType) : ^(NSNumber *type) {
            self.textView.keyboardType = [type unsignedIntegerValue];
        },
        source(model.returnKeyType) : ^(NSNumber *returnKeyType) {
            self.textView.returnKeyType = [returnKeyType integerValue];
        },
    });
}

# pragma mark - UIResponder

- (BOOL)becomeFirstResponder
{
    return [self.textView becomeFirstResponder];
}

# pragma mark - UITextViewDelegate

- (void)textViewDidBeginEditing:(UITextView *)textView
{
    [self ehi_performAction:@selector(didBeginEditingPrimaryInputForCell:) withSender:self];
}

- (void)textViewDidChange:(UITextView *)textView
{
    self.viewModel.inputValue = textView.text;
}

@end
