//
//  EHISigninViewModel.h
//  Enterprise
//
//  Created by Ty Cobb on 4/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHISigninFieldModel.h"
#import "EHISigninRecoveryType.h"

typedef NS_ENUM(NSInteger, EHISigninFieldType) {
    EHISigninFieldTypeEmail,
    EHISigninFieldTypePassword,
};

@interface EHISigninViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic) NSString *headerInfoText;

@property (assign, nonatomic) EHISigninLayout layout;
@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) NSString *actionTitle;
@property (copy  , nonatomic, readonly) NSString *staySignedInTitle;
@property (copy  , nonatomic, readonly) NSString *forgotUsernameTitle;
@property (copy  , nonatomic, readonly) NSString *forgotPasswordTitle;
@property (copy  , nonatomic, readonly) NSString *partialEnrollmentTitle;
@property (copy  , nonatomic, readonly) NSString *joinNowTitle;
@property (copy  , nonatomic) NSString *emeraldClubMembersTitle;

@property (copy  , nonatomic, readonly) NSString *identification;
@property (copy  , nonatomic, readonly) NSString *password;
@property (copy  , nonatomic, readonly) NSArray  *fieldModels;

@property (copy  , nonatomic) NSString *username;

@property (assign, nonatomic, readonly) BOOL remembersCredentials;
@property (assign, nonatomic, readonly) BOOL isLoading;
@property (assign, nonatomic) BOOL hideArrowImage;
@property (assign, nonatomic, readonly) BOOL hasValidCredentials;
@property (assign, nonatomic) BOOL isReservationFlow;

@property (copy  , nonatomic) void (^signinHandler)();

- (void)setValue:(NSString *)value forFieldWithType:(EHISigninFieldType)type;

- (void)toggleRemembersCredentials;
- (void)initiateSignin;
- (void)didTapEmeraldClubMembers;
- (void)showForgotPasswordScreen;
- (void)showJoinNowScreen;
- (void)closeSignin;
- (void)dismiss;

- (void)showRecoveryModalOrWebBrowserWithType:(EHISigninRecoveryType)type;

@end
