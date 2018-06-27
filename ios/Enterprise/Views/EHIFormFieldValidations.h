//
//  EHIFormFieldValidations.h
//  Enterprise
//
//  Created by Alex Koller on 5/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef BOOL(^EHIFormFieldValidation)(id input);

# pragma mark - Text Validations

/** Validates the existence of some input text */
extern EHIFormFieldValidation EHIFormFieldValidationNotEmpty;
extern EHIFormFieldValidation EHIFormFieldValidationNotEmptyOrSpaces;
/** Validates that the input text contains the '@' symbol */
extern EHIFormFieldValidation EHIFormFieldValidationAtSymbol;

# pragma mark - Date Validations

/** Validates that the input date is before now */
extern EHIFormFieldValidation EHIFormFieldValidationIsPast;
/** Validates that the input date is after now */
extern EHIFormFieldValidation EHIFormFieldValidationIsFuture;
