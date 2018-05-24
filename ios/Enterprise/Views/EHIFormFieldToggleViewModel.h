//
//  EHIFormFieldCheckboxViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 6/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldViewModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIFormFieldToggleViewModel : EHIFormFieldViewModel <MTRReactive>

/** The @c BOOL input value held by this field */
@property (assign, nonatomic) BOOL toggleValue;

/** Creates a standard toggle field with the given @c title and @c input value */
+ (instancetype)toggleFieldWithTitle:(NSString *)title input:(BOOL)input;

@end

NS_ASSUME_NONNULL_END