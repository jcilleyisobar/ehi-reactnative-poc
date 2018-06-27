//
//  EHIRentalsLookupViewModel.h
//  Enterprise
//
//  Created by fhu on 5/7/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIRequiredInfoViewModel.h"

@interface EHIRentalsLookupViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) NSString *firstNameTitle;
@property (copy  , nonatomic, readonly) NSString *lastNameTitle;
@property (copy  , nonatomic, readonly) NSString *actionTitle;
@property (copy  , nonatomic, readonly) NSString *callButtonTitle;
@property (copy  , nonatomic, readonly) NSAttributedString *confirmationTitle;

@property (copy  , nonatomic, readonly) NSError *error;
@property (assign, nonatomic, readonly) BOOL isLoading;
@property (assign, nonatomic, readonly) BOOL isValid;

@property (copy  , nonatomic) NSString *confirmation;
@property (copy  , nonatomic) NSString *firstName;
@property (copy  , nonatomic) NSString *lastName;

@property (strong, nonatomic) EHIRequiredInfoViewModel *requiredFieldsModel;

- (void)findRental;
- (void)closeRental;
- (void)callContactNumber;

@end
