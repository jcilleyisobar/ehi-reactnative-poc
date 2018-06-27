//
//  EHIFormFieldViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 5/1/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIFormFieldValidations.h"

typedef NS_ENUM(NSUInteger, EHIFormFieldType) {
    EHIFormFieldTypeUnknown,
    EHIFormFieldTypeLabel,
    EHIFormFieldTypeButton,
    EHIFormFieldTypeActionButton,
    EHIFormFieldTypeBasicProfile,
    EHIFormFieldTypeText,
    EHIFormFieldTypeTextToggle,
    EHIFormFieldTypeTextView,
    EHIFormFieldTypeToggle,
    EHIFormFieldTypeDropdown,
    EHIFormFieldTypeDate,
    EHIFormFieldTypeDateMonthYear,
    EHIFormFieldTypePhones,
};

@protocol EHIFormFieldDelegate;

@interface EHIFormFieldViewModel : EHIViewModel <MTRReactive>

/** Delegate for reporting state changes */
@property (weak  , nonatomic) id<EHIFormFieldDelegate> delegate;
/** Id used to identify this form field. Optional. */
@property (copy  , nonatomic) id uid;
/** The input value obtained from the user */
@property (strong, nonatomic) id inputValue;
/** The title for this input field. View is hidden if this and @c attributedTitle are @c nil. */
@property (copy  , nonatomic) NSString *title;
/** The attributed title for this input field. Only used if @c title is @c nil. View is hidden if this and @c title are @c nil. */
@property (copy  , nonatomic) NSAttributedString *attributedTitle;
/** The subtitle for this input field. View is hidden if this is @c nil. */
@property (copy  , nonatomic) NSString *subtitle;
/** If field is last within a grouping of related fields (e.i. address fields) */
@property (assign, nonatomic) BOOL isLastInGroup;
/** If the field's main control should be inactive to the user. Defaults to @c NO. */
@property (assign, nonatomic) BOOL isUneditable;
/** If @YES will append "*" to the title. */
@property (assign, nonatomic) BOOL isRequired;
/** Controls autocapitalization behavior for the text widget. */
@property (assign, nonatomic) UITextAutocapitalizationType captalizationMode;
@property (assign, nonatomic) UIReturnKeyType returnKeyType;
@property (assign, nonatomic) CGFloat extraPadding;

/** The type of input field to render */
@property (assign, nonatomic, readonly) EHIFormFieldType type;
/** If the title above the input field should be hidden. */
@property (assign, nonatomic, readonly) BOOL hidesTitle;
/** If the subtitle above the input field should be hidden. */
@property (assign, nonatomic, readonly) BOOL hidesSubtitle;
/** If this field is currently showing an error */
@property (assign, nonatomic, readonly) BOOL showsError;

/** Pass through to @c -validate:(BOOL)showErrors with @c showErrors set to @c NO */
- (BOOL)validate;
/** Result of running @c inputValue through all given validation blocks. If @c showErrors is @c YES, field will show an error state if needed */
- (BOOL)validate:(BOOL)showErrors;
/** Adds a validation to perform when inquiring whether @c inputValue is valid via @c isValid */
- (void)validates:(EHIFormFieldValidation)validation;
/** Clears any existing validations created via @c -validates:(EHIFormFieldValidation)validation */
- (void)clearValidations;

@end

@protocol EHIFormFieldDelegate <NSObject>
/** Called whenever @c inputValue is changed */
- (void)formFieldViewModelDidChangeValue:(EHIFormFieldViewModel *)viewModel;
@optional
- (void)formFieldViewModelButtonTapped:(EHIFormFieldViewModel *)viewModel;
@end
