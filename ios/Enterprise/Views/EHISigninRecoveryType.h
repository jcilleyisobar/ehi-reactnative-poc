//
//  EHISigninRecoveryType.h
//  Enterprise
//
//  Created by Michael Place on 5/7/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

typedef NS_ENUM(NSUInteger, EHISigninRecoveryType) {
    EHISigninRecoveryTypeUsername,
    EHISigninRecoveryTypePartialEnrollment,
    EHISignInRecoveryTypeJoinNow,
    EHISigninRecoveryTypeForgotConfirmation
};

typedef NS_ENUM(NSInteger, EHISigninLayout) {
    EHISigninLayoutDefault,
    EHISigninLayoutEnrollment,
};