//
//  EHIEmeraldClubSignInViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 6/2/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHISigninViewModel.h"

@interface EHIEmeraldClubSignInViewModel : EHIViewModel <MTRReactive>
@property (assign, nonatomic) EHISigninLayout layout;
@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) NSString *headerInfoText;
@property (copy  , nonatomic, readonly) NSString *subheaderInfoText;
@property (copy  , nonatomic, readonly) NSString *addAccountTitle;
@property (copy  , nonatomic, readonly) NSString *staySignedInTitle;
@property (copy  , nonatomic, readonly) NSString *forgotPasswordTitle;

@property (copy  , nonatomic, readonly) NSString *identification;
@property (copy  , nonatomic, readonly) NSString *password;
@property (copy  , nonatomic, readonly) NSArray  *fieldModels;

@property (copy  , nonatomic) NSString *username;

@property (assign, nonatomic, readonly) BOOL remembersCredentials;
@property (assign, nonatomic, readonly) BOOL isLoading;
@property (assign, nonatomic, readonly) BOOL hasValidCredentials;

@property (copy  , nonatomic) void (^signinHandler)(BOOL didSignIn);

- (void)setValue:(NSString *)value forFieldWithType:(EHISigninFieldType)type;

- (void)toggleRemembersCredentials;
- (void)initiateSignin;
- (void)cancelSignIn;
- (void)didTapForgotPassword;

@end
