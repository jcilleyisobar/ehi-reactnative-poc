//
//  EHIForgotPasswordViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 1/14/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIRequiredInfoViewModel.h"

@interface EHIForgotPasswordViewModel : EHIViewModel <MTRReactive>

@property (copy, nonatomic, readonly) NSString *title;
@property (copy, nonatomic, readonly) NSString *instructionsTitle;
@property (copy, nonatomic, readonly) NSString *firstNameTitle;
@property (copy, nonatomic, readonly) NSString *lastNameTitle;
@property (copy, nonatomic, readonly) NSString *emailAddressTitle;
@property (copy, nonatomic, readonly) NSString *submitTitle;
@property (copy, nonatomic, readonly) NSString *sentEmailMessage;

@property (assign, nonatomic) BOOL hasErrorFirstName;
@property (assign, nonatomic) BOOL hasErrorLastName;
@property (assign, nonatomic) BOOL hasErrorEmail;
@property (assign, nonatomic) BOOL hasErrors;
@property (assign, nonatomic, readonly) BOOL isLoading;

@property (copy, nonatomic) NSString *firstName;
@property (copy, nonatomic) NSString *lastName;
@property (copy, nonatomic) NSString *emailAddress;

@property (strong, nonatomic) EHIRequiredInfoViewModel *requiredInfoModel;

- (void)submit;
- (void)dismiss;
@end
