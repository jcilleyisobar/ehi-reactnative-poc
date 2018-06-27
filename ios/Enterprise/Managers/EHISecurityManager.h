//
//  EHISecurityManager.h
//  Enterprise
//
//  Created by Alex Koller on 1/20/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

typedef void (^EHIBiometricHandler)(BOOL success);

@interface EHISecurityManager : NSObject

/** @c YES if an overlay should be placed over the screen when backgrounded */
@property (assign, nonatomic) BOOL shouldHideContent;

/** Singleton accessor for the user manager */
+ (instancetype)sharedInstance;

/**
 @brief Prompts touch id for the user
 
 It is expected you call @c canUseBiometrics before calling this method. If
 not, this method may fail silently and immediately callback with @c NO. Failure
 to authenticate the user through touch id automatically shows an alert.
 */

- (void)evaluateBiometricsWithReason:(NSString *)reason handler:(EHIBiometricHandler)handler;

/**
 @brief Whether Touch ID is available on this device
 
 Determines if the users device is capable if authenticating using Touch ID. This may return
 @c YES while @c -canEvaluateBiometrics returns @c NO if the user has not enrolled fingers
 in Touch ID.
 */

- (BOOL)canUseBiometrics;

/**
 @brief Whether Touch ID can currently be evaluated
 
 Determines if the device is completely setup to authenticate a user using Touch ID. This includes
 having a passcode set, having Touch ID capabilities, and having fingers enrolled in Touch ID. If
 the check fails, this method implicitly alerts the user of the error.
 */

- (BOOL)canEvaluateBiometrics;

@end
