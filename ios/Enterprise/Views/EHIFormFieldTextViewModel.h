//
//  EHIFormFieldTextViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 5/1/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldViewModel.h"
#import "EHIContractDetails.h"
#import "EHIFormattedPhone.h"
#import "EHIPhone.h"

@interface EHIFormFieldTextViewModel : EHIFormFieldViewModel <MTRReactive>

/** The value held by this input field */
@property (strong, nonatomic) NSString *inputValue;
/** Model to be passed into the text field in the case of a formatted phone number */
@property (strong, nonatomic) EHIFormattedPhone *phoneModel;
/** Placeholder text for embedded text field when @c inputValue is @c nil */
@property (copy  , nonatomic) NSString *placeholder;
/** Type of keyboard to display when user is entering text */
@property (assign, nonatomic) UIKeyboardType keyboardType;
/** @c YES if the field holds sensitive data */
@property (assign, nonatomic) BOOL sensitive;
/** If this field is used as input for phone numbers */
@property (assign, nonatomic) BOOL isPhoneField;
/** If this field is used as input for email addresses */
@property (assign, nonatomic) BOOL isEmailField;
/** If a dropdown selection should be shown on the left to change category of input */
@property (nonatomic, readonly) BOOL allowsCategorySelection;
/** The selectable categories shown in the dropdown */
@property (copy  , nonatomic) NSArray *categoryOptions;
/** The text field placeholder to use when a particular category is selected. Will use @c placeholder if nil. */
@property (copy  , nonatomic) NSArray *categoryOptionPlaceholders;
/** The selected category corresponding to @c categoryOptions */
@property (assign, nonatomic) NSUInteger selectedCategory;
/** If delete button should be shown on the right */
@property (assign, nonatomic) BOOL hidesDeleteButton;
/** character limit for the input value, default is no limit **/
@property (assign, nonatomic) NSInteger limit;

// computed
@property (nonatomic, readonly) NSString *selectedCategoryName;

@end

@interface EHIFormFieldTextViewModel (Generators)

+ (instancetype)accountFieldForCorporateAccount:(EHIContractDetails *)corporateAccount;
+ (instancetype)phoneFieldForPhone:(EHIPhone *)phone withTitle:(NSString *)title;

@end
