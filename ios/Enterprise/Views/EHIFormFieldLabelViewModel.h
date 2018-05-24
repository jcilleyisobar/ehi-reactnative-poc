//
//  EHIFormFieldLabelViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 6/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldViewModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIFormFieldLabelViewModel : EHIFormFieldViewModel

/** Creates a standard label field with the given @c title */
+ (instancetype)viewModelWithTitle:(NSString *)title;

@end

NS_ASSUME_NONNULL_END