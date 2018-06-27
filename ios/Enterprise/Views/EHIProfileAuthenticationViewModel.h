//
//  EHIProfileAuthenticateViewModel.h
//  Enterprise
//
//  Created by fhu on 5/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHISigninFieldModel.h"

@interface EHIProfileAuthenticationViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic) NSString *password;
@property (assign, nonatomic) BOOL isLoading;
@property (strong, nonatomic, readonly) EHISigninFieldModel *passwordFieldModel;
@property (assign, nonatomic, readonly) BOOL hasValidCredentials;
@property (nonatomic, readonly) NSArray *fieldModels;

@property (copy  , nonatomic, readonly) NSString *titleText;
@property (copy  , nonatomic, readonly) NSString *subtitleText;
@property (copy  , nonatomic, readonly) NSString *forgotPasswordText;
@property (copy  , nonatomic, readonly) NSString *continueButtonText;
@property (copy  , nonatomic, readonly) NSString *cancelButtonText;

- (void)forgotPassword;
- (void)authenticateWithHandler:(void(^)(NSError *error))handler;
- (void)cancel;

@end
