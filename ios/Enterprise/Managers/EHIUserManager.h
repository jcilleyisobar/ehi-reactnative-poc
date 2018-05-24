//
//  EHIUserManager.h
//  Enterprise
//
//  Created by mplace on 2/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIUser.h"
#import "EHIUserCredentials.h"
#import "EHIServicesError.h"
#import "EHIEnrollProfile.h"

#define EHIUserSignInOptionChangedNotification @"EHIUserSignInOptionChangedNotification"

typedef void(^EHIUserHandler)(EHIUser *, EHIServicesError *);

typedef NS_ENUM(NSUInteger, EHIUserSignInOption) {
    EHIUserSignInOptionDefault,
    EHIUserSignInOptionForceUpdatedTerms,
};

@protocol EHIUserListener;

@interface EHIUserManager : NSObject

/** The presently authenticated user; may be @c nil */
@property (strong, nonatomic, readonly) EHIUser *currentUser;
/** The stored credentials for the presently authenticated user */
@property (strong, nonatomic, readonly) EHIUserCredentials *credentials;
/** @c YES if the user is an emerald account */
@property (assign, nonatomic, readonly) BOOL isEmeraldUser;

/** Debug variable to test different sign in scenarios */
@property (assign, nonatomic) EHIUserSignInOption signInOption;

/** Enrollment data*/
@property (strong, nonatomic) EHIEnrollProfile *enrollmentProfile;
@property (assign, nonatomic) EHIEnrollmentProfileMatch profileMatch;

- (void)resetEnrollment;

/** Singleton accessor for the user manager */
+ (instancetype)sharedInstance;
/** Bootstraps the user manager, allowing it to perform startup tasks */
+ (void)prepareToLaunch;

/** Refreshes current credentials. Batches all handlers and logs out user on server failure. */
- (void)refreshCredentialsWithHandler:(EHIUserHandler)handler;
/** Calls the appropriate authentication service method and properly synchronizes the current user */
- (void)authenticateUserWithCredentials:(EHIUserCredentials *)credentials handler:(EHIUserHandler)handler;
/** Change password for current user */
- (void)changePassword:(NSString *)password confirmation:(NSString *)confirmation handler:(void (^)(EHIServicesError *error))handler;
/** Reset password for current user */
- (void)resetPassword:(EHIUser *)user handler:(void (^)(EHIServicesError *error))handler;
/** Refreshes the current user's profiles */
- (void)refreshUserWithHandler:(EHIUserHandler)handler;
/** Try authenticating as an Emerald Club user with whatever credentials we got */
- (void)attemptEmeraldAutoAuthentcateWithHandler:(EHIUserHandler)handler;

/** Refreshes the user's past rentals and calls the handler when ready */
- (void)refreshPastRentalsWithHandler:(EHIUserHandler)handler;
/** Calls the handler when the user's rentals are ready */
- (void)currentAndUpcomingRentalsWithHandler:(EHIUserHandler)handler;
/** Refreshes the user's rentals and calls the handler when ready */
- (void)refreshCurrentAndUpcomingRentalsWithHandler:(EHIUserHandler)handler;

/** Prompt user to enroll in Enterprise Plus program */
- (void)promptSignUpWithHandler:(void (^)(BOOL didSignup))handler;
/** Prompt user with alert to optionally logout */
- (void)promptLogoutWithHandler:(void (^)(BOOL didLogout))handler;
/** Prompt EC user with alert when trying sign into EP */
- (void)promptEmeraldClubWarningIfNecessaryWithHandler:(void (^)(BOOL shouldSignIn))handler;
/** Destroys and logs out the current user */
- (void)logoutCurrentUser;
/** Clears aplicable user data through settings */
- (void)clearData;

/** Starts listening for authentication events; the manager does @em not retain the @c listener */
- (void)addListener:(id<EHIUserListener>)listener;
/** Stops the @c listener from receiving authentication events */
- (void)removeListener:(id<EHIUserListener>)listener;

@end

@protocol EHIUserListener <NSObject> @optional
/** Called on listeners when a new user signs in */
- (void)manager:(EHIUserManager *)manager didSignInUser:(EHIUser *)user;
/** Called on listeners when a user signs out */
- (void)manager:(EHIUserManager *)manager didSignOutUser:(EHIUser *)user;
/** Called whenever a user signs in/out. @c user will be @c nil if the the user did sign out */
- (void)manager:(EHIUserManager *)manager didChangeAuthenticationForUser:(EHIUser *)user;
/** Called whenever user data is updated via @c refreshUserWithHandler: */
- (void)manager:(EHIUserManager *)manager didRefreshUser:(EHIUser *)user;
@end
