//
//  EHIFormFieldLabelViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 6/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldLabelViewModel.h"

NS_ASSUME_NONNULL_BEGIN

@implementation EHIFormFieldLabelViewModel

+ (instancetype)viewModelWithTitle:(NSString *)title
{
    EHIFormFieldLabelViewModel *viewModel = [EHIFormFieldLabelViewModel new];
    viewModel.isLastInGroup = YES;
    
    // format title
    viewModel.attributedTitle = EHIAttributedStringBuilder.new.size(18.0).lineSpacing(4.0)
        .text(title).string;
    
    return viewModel;
}

# pragma mark - Accessors

- (EHIFormFieldType)type
{
    return EHIFormFieldTypeLabel;
}

- (nullable id)inputValue
{
    return nil;
}

- (BOOL)isUneditable
{
    return YES;
}

# pragma mark - Validation

- (BOOL)validate:(BOOL)showErrors
{
    return YES;
}

@end

NS_ASSUME_NONNULL_END
