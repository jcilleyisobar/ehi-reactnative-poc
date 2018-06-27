//
//  EHIUserManager.m
//  Enterprise
//
//  Created by mplace on 2/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIUserManager.h"
#import "EHIRouter.h"
#import "EHIConfiguration.h"
#import "EHIDataStore.h"
#import "EHIInfoModalViewModel.h"
#import "EHITermsViewModel.h"
#import "EHIServices+User.h"
#import "EHIServices+Rentals.h"
#import "EHIUserManager+DNR.h"
#import "EHINotificationManager.h"
#import "EHIWatchConnectivityManager.h"
#import "EHIGeofenceManager.h"
#import "EHISettings.h"
#import "EHIWebBrowserViewModel.h"
#import "EHITransitionManager.h"

#define EHIUnrememberedUserExpiryTime EHISecondsPerDay

NSString *const EHIEnrollmentUserProfilesUID = @"EHIEnrollmentUserProfilesUID";
NSString *const EHIEnrollmentAddressUID      = @"EHIEnrollmentAddressUID";

@interface EHIUserManager ()
@property (strong, nonatomic) NSHashTable *listeners;
@property (assign, nonatomic) BOOL isRefreshingRentals;
@property (assign, nonatomic) BOOL isRefreshingCredentials;
@property (assign, nonatomic) BOOL isAutoAuthenticating;
@property (strong, nonatomic) NSTimer *expiryTimer;
@property (strong, nonatomic) NSMutableArray *pastHandlers;
@property (strong, nonatomic) NSMutableArray *handlers;
@property (strong, nonatomic) NSMutableArray *credentialHandlers;
@property (strong, nonatomic) EHIUserCredentials *credentials;
@property (strong, nonatomic) id<EHINetworkCancelable> pastRentalsRequest;
@property (strong, nonatomic) id<EHINetworkCancelable> rentalsRequest;
@end

@implementation EHIUserManager

+ (instancetype)sharedInstance
{
    static EHIUserManager *manager;
    static dispatch_once_t once;
    
    dispatch_once(&once, ^{
        manager = [EHIUserManager new];
    });
    
    return manager;
}

- (instancetype)init
{
    if(self = [super init]) {
        _handlers  = [NSMutableArray new];
        _pastHandlers = [NSMutableArray new];
        _credentialHandlers = [NSMutableArray new];
        _listeners = [NSHashTable weakObjectsHashTable];
    }
    
    return self;
}

# pragma mark - Bootstrapping

+ (void)prepareToLaunch
{
    // auto authenticate EP users
    [EHIDataStore first:[EHIUserCredentials class] handler:^(EHIUserCredentials *credentials) {
        if(credentials) {
            [[self sharedInstance] prepareToAutoAuthenticate];
        }
    }];
    
    [[NSNotificationCenter defaultCenter] addObserver:[self sharedInstance] selector:@selector(didEnterForeground:) name:UIApplicationWillEnterForegroundNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:[self sharedInstance] selector:@selector(didEnterBackground:) name:UIApplicationDidEnterBackgroundNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:[self sharedInstance] selector:@selector(willTerminate:) name:UIApplicationWillTerminateNotification object:nil];
}

- (void)prepareToAutoAuthenticate
{
    __weak typeof(self) welf = self;
    [EHIDataStore first:[EHIUser class] handler:^(EHIUser *user) {
        welf.currentUser = user;
    }];
    
    // only refresh if we have stored creds
    self.isRefreshingRentals = [EHIDataStore any:[EHIUserCredentials class]];
    
    // attempt authentication after the config comes back
    EHIConfigurationHandler *handler = [[EHIConfiguration configuration] onReady:^(BOOL isReady) {
        [self attemptAutoAuthenticationWithHandler:nil];
    }];
   
    // we only want to know when we're actually ready
    handler.waitsUntilReady = YES;
}

- (void)attemptAutoAuthenticationWithHandler:(EHIUserHandler)handler
{
    // pull out the credentials and authenticate if possible
    [EHIDataStore first:[EHIUserCredentials class] handler:^(EHIUserCredentials *credentials) {
        // we're already persisted so don't modify the cache
        [self setCredentials:credentials modifyCache:NO];
        
        // throw away these credentials if they're expired
        if(credentials && [[NSDate ehi_today] ehi_isAfter:credentials.expiryDate]) {
            credentials = nil;
            
            [self setCredentials:nil modifyCache:YES];
        }
      
        // if we don't have valid credentials, terminate now
        if(!credentials.isValid) {
            [self didFinishRefreshingRentalsForUser:nil error:nil];
            ehi_call(handler)(nil, nil);
        } else {
            [self setIsAutoAuthenticating:YES];
            [self authenticateUserWithCredentials:credentials handler:handler];
        }
    }];
}

- (void)attemptEmeraldAutoAuthentcateWithHandler:(EHIUserHandler)handler
{
    // don't do anything if we're currently authenticating or authenticated
    if(self.isRefreshingRentals || self.currentUser != nil) {
        ehi_call(handler)(nil, [EHIServicesError servicesErrorFailure]);
    }
    
    // try authenticating with whatever we got
    else {
        [self attemptAutoAuthenticationWithHandler:handler];
    }
}

- (void)didFinishRefreshingRentalsForUser:(EHIUser *)user error:(EHIServicesError *)error
{
    self.isRefreshingRentals = NO;
    
    // callback all the handlers, and then destroy them
    for(EHIUserHandler handler in self.handlers) {
        ehi_call(handler)(user, error);
    }
    
    [self.handlers removeAllObjects];
    
    // schedule trip notifications
     [[EHINotificationManager sharedInstance] scheduleRentalNotificationsForUser:user];
	
    // schedule geofence notifications
    [[EHIGeofenceManager sharedInstance] monitorRentalGeofencesForUser:user];

    // update our application context for apple watch
    [[EHIWatchConnectivityManager sharedInstance] updateContextWithUser:user];
    
    // add shortcut item for soonest reservation
    if(user.upcomingRentals.firstRental) {
        [UIApplication addRentalShortcut:user.upcomingRentals.firstRental];
    }
    // wipe existing if no user (failed auth) or rental (prevent showing stale data)
    else {
        [UIApplication removeShortcutsWithType:EHIShortcutTypeRentalDetailsKey];
    }
}

# pragma mark - Sign Up

- (void)promptSignUpWithHandler:(void (^)(BOOL didSignup))handler
{  
    EHIRouter.currentRouter.transition
        .push(EHIScreenEnrollmentStepOne).start(nil);
}

# pragma mark - Authentication

- (void)refreshCredentialsWithHandler:(EHIUserHandler)handler
{
    // user did not select remember me
    if(!self.credentials.encrypedCredentials) {
        [self logoutCurrentUser];
        ehi_call(handler)(nil, [EHIServicesError servicesErrorFailure]);
        return;
    }
    
    // batch all handlers for single request
    [self.credentialHandlers addObject:handler];
    
    // only have 1 active request at a time
    if(!self.isRefreshingCredentials) {
        self.isRefreshingCredentials = YES;

        __weak typeof(self) welf = self;
        void (^handlerBlock)(EHIUser *, EHIServicesError *) = ^(EHIUser *user, EHIServicesError *error) {
            BOOL success = !error.hasFailed;
            
            // refresh credentials
            if(success) {
                // prevent user changed notifications
                _currentUser = user;
                
                // instead notify of user refresh
                [self notifyListenersForSelector:@selector(manager:didRefreshUser:)];
                
                // update credentials
                welf.credentials.encrypedCredentials = user.encryptedCredentials;
                welf.credentials.expiryDate = [[NSDate ehi_today] ehi_addDays:EHIDaysPerYear];
                
                // save credentials
                [EHIDataStore save:welf.credentials handler:nil];
            }
            // only wipeout user if error wasn't a result of bad network connectivity
            else if(error.code != EHINetworkStatusCodeServicesUnavailable){
                [welf logoutCurrentUser];
            }
            
            // call handlers
            for(EHIUserHandler handler in welf.credentialHandlers) {
                ehi_call(handler)(user, error);
            }
            
            [welf.credentialHandlers removeAllObjects];
            [welf setIsRefreshingCredentials:NO];
        };
        
        BOOL isEmerald = self.credentials.isEmeraldCredentials;
        if(isEmerald) {
            [[EHIServices sharedInstance] authenticateEmeraldClubUserWithCredentials:self.credentials handler:handlerBlock];
        } else {
            [[EHIServices sharedInstance] authenticateUserWithCredentials:self.credentials handler:handlerBlock];
        }
    }
}

- (void)authenticateUserWithCredentials:(EHIUserCredentials *)credentials handler:(EHIUserHandler)handler
{
    if(!credentials.isEmeraldCredentials) {
        [self authenticateEnterprisePlusUserWithCredentials:credentials hander:handler];
    } else {
        [self authenticateEmeraldClubUserWithCredentials:credentials handler:handler];
    }
}

- (void)authenticateEnterprisePlusUserWithCredentials:(EHIUserCredentials *)credentials hander:(EHIUserHandler)handler
{
    self.isRefreshingRentals = YES;
    
    [[EHIServices sharedInstance] authenticateUserWithCredentials:credentials handler:^(EHIUser *user, EHIServicesError *error) {
         // if this was a terms and conditions error, run the custom error handling
        if([error hasErrorCode:EHIServicesErrorCodeTermsAndConditions] || self.signInOption == EHIUserSignInOptionForceUpdatedTerms) {
            [self handleTermsAndConditionsError:error credentials:credentials handler:handler];
        }
        else if ([error hasErrorCode:EHIServicesErrorCodePasswordIsOutdated]) {
            [self handleOutdatedPasswordError:error credentials:credentials handler:handler];
        }
        else if ([error hasErrorCode:EHIServicesErrorCodeLoginSystemError]) {
            [self handleLoginError:error credentials:credentials handler:handler];
        }
        else {
            // update state based on the (un)successful authentication
            [self didFinishAuthenticationWithUser:user credentials:credentials error:error];
            // and show the dnr modal if necessary before continuing
            [EHIUserManager attemptToShowContinueDnrModalWithHandler:^(BOOL shouldContinue) {
                ehi_call(handler)(user,error);
            }];
        }
        
        // reset debug option
        self.signInOption = EHIUserSignInOptionDefault;
    }];
}

- (void)authenticateEmeraldClubUserWithCredentials:(EHIUserCredentials *)credentials handler:(EHIUserHandler)handler
{
    [[EHIServices sharedInstance] authenticateEmeraldClubUserWithCredentials:credentials handler:^(EHIUser *user, EHIServicesError *error) {
        // if this was a terms and conditions error, run the custom error handling
        if([error hasErrorCode:EHIServicesErrorCodeTermsAndConditions] || self.signInOption == EHIUserSignInOptionForceUpdatedTerms) {
            [self handleTermsAndConditionsError:error credentials:credentials handler:handler];
        } else if ([error hasErrorCode:EHIServicesErrorCodePasswordIsOutdated]) {
            [self handleOutdatedPasswordError:error credentials:credentials handler:handler];
        } else if ([error hasErrorCode:EHIServicesErrorCodeLoginSystemError]) {
            [self handleLoginError:error credentials:credentials handler:handler];
        }
        else {
            [self didFinishAuthenticationWithUser:user credentials:credentials error:error];
            ehi_call(handler)(self.currentUser, error);
        }
    }];
}

- (void)changePassword:(NSString *)password confirmation:(NSString *)confirmation handler:(void (^)(EHIServicesError *error))handler
{
    [[EHIServices sharedInstance] changePassword:password confirmation:confirmation hander:^(EHIUser *user, EHIServicesError *error) {
        if(!error.hasFailed) {
            self.credentials.encrypedCredentials = user.encryptedCredentials;
        }
        
        ehi_call(handler)(error);
    }];
}

- (void)resetPassword:(EHIUser *)user handler:(void (^)(EHIServicesError *error))handler
{
    [[EHIServices sharedInstance] resetPassword:user handler:^(EHIServicesError *error) {
        ehi_call(handler)(error);
    }];
}

- (void)didFinishAuthenticationWithUser:(EHIUser *)user credentials:(EHIUserCredentials *)credentials error:(EHIServicesError *)error
{
    // if we errored, we can't fetch any rentals
    if(error.hasFailed) {
        [self didFinishRefreshingRentalsForUser:nil error:error];
        [self purgeCredentials];
    } else {
        [self didAuthenticateUser:user withCredentials:credentials];
        [self refreshCurrentAndUpcomingRentalsWithHandler:nil];
    }
    
    // reset flag
    self.isAutoAuthenticating = NO;
}

- (void)refreshUserWithHandler:(EHIUserHandler)handler;
{
    if (self.currentUser && !self.credentials.isEmeraldCredentials ){
        [[EHIServices sharedInstance] refreshUser:self.currentUser handler:^(EHIUser *user, EHIServicesError *error) {
            // notify listeners on successful refresh
            if(!error) {
                [self notifyListenersForSelector:@selector(manager:didRefreshUser:)];
            }
        
            ehi_call(handler)(self.currentUser, error);
        }];
    }
}

- (void)promptLogoutWithHandler:(void (^)(BOOL didLogout))handler
{
    NSString *message;
    if(self.isEmeraldUser) {
        message = EHILocalizedString(@"signout_emerald_club_confirmation_text", @"Are you sure you want to sign out of your Emerald Club account?", @"");
    } else {
        message = EHILocalizedString(@"signout_confirmation_text", @"Do you want to sign out?", @"");
    }
    EHIAlertViewBuilder *alertView = EHIAlertViewBuilder.new
        .message(message)
        .button(EHILocalizedString(@"signout_confirmation_signout_button", @"Sign Out", @"Signout button title"))
        .cancelButton(EHILocalizedString(@"signout_confirmation_cancel_button", @"Cancel", @"Standard cancel button title"));
    
    alertView.show(^(NSInteger index, BOOL canceled) {
        if(!canceled) {
            [[EHIUserManager sharedInstance] logoutCurrentUser];
        }
        
        ehi_call(handler)(!canceled);
    });
}

- (void)logoutCurrentUser
{
    // clear out our current credentials / user but persist Emerald credentials
    [self setCredentials:nil modifyCache:YES];
    [self setCurrentUser:nil];
	
	// clear our notifications
    [[EHINotificationManager sharedInstance] clearRentalNotifications];
    [[EHIGeofenceManager sharedInstance] clearRentalGeofences];

    // clear our application context for apple watch
    [[EHIWatchConnectivityManager sharedInstance] updateContextWithUser:nil];
    
    [UIApplication removeShortcutsWithType:EHIShortcutTypeRentalDetailsKey];
}

- (void)promptEmeraldClubWarningIfNecessaryWithHandler:(void (^)(BOOL shouldSignIn))handler
{
    if(!self.credentials.isEmeraldCredentials) {
        ehi_call(handler)(YES);
    } else {
        EHIInfoModalViewModel *model = [EHIInfoModalViewModel new];
        model.title             = EHILocalizedString(@"login_emerald_club_logged_in_title", @"SIGN IN TO ENTERPRISE PLUS?", @"");
        model.details           = EHILocalizedString(@"login_emerald_club_logged_in_text", @"You are currently signed into an Emerald Club account. By continuing, you will be signed out of this account so that you can sign in to Enterprise Plus.", @"");
        model.firstButtonTitle  = EHILocalizedString(@"login_emerald_club_logged_in_yes_button", @"YES, CONTINUE", @"");
        model.secondButtonTitle = EHILocalizedString(@"login_emerald_club_logged_in_no_button", @"NO, KEEP EMERALD CLUB ACCOUNT", @"");
        
        __weak typeof(model) wodel = model;
        [model present:^BOOL(NSInteger index, BOOL canceled) {
            BOOL shouldSignIn = index == 0;
            
            // call handler after dismissal
            [wodel dismissWithCompletion:^{
                ehi_call(handler)(shouldSignIn);
            }];
            
            return NO;
        }];
    }
}

//
// Helpers
//

- (void)didAuthenticateUser:(EHIUser *)user withCredentials:(EHIUserCredentials *)credentials
{
    // clear out any sensitive information
    credentials.identification = nil;
    credentials.password       = nil;
    
    // mark the auth time
    credentials.authenticationDate = [NSDate date];

    // store the encrpyted credentials, if any
    if(credentials.remembersCredentials) {
        credentials.encrypedCredentials = user.encryptedCredentials;
    }
    // otherwise, throw away these credentials in no less than 24 hours
    else {
        [self kickoffExpiryTimerForCredentials];
    }
    
    // store the credentials in memory
    [self setCredentials:credentials modifyCache:YES];
    
    // save user and notify listeners
    self.currentUser = user;
}

- (void)handleTermsAndConditionsError:(EHIServicesError *)error credentials:(EHIUserCredentials *)credentials handler:(EHIUserHandler)handler
{
    // prevent the default error handling
    [error consume];
   
    // show the terms vc
    EHITermsViewModel *termsModel = [EHITermsViewModel new];

    [EHITransitionManager transitionToScreen:EHIScreenTerms object:termsModel asModal:YES];

    // handle the result of the user interaction
    termsModel.handler = ^(NSString *acceptedTermsVersion, BOOL accepted) {
        // update creds if the user accepted
        credentials.acceptedTermsVersion = acceptedTermsVersion;
        
        // if the user didn't accept, terminate here
        if(!acceptedTermsVersion) {
            [self didFinishAuthenticationWithUser:nil credentials:credentials error:error];
            ehi_call(handler)(nil, error);
        }
        // otherwise, attempt to sign-in again
        else {
            [self authenticateUserWithCredentials:credentials handler:handler];
        }
    };
}

- (void)handleOutdatedPasswordError:(EHIServicesError *)error credentials:(EHIUserCredentials *)credentials handler:(EHIUserHandler)handler
{
    // prevent the default error handling
    [error consume];

    EHIInfoModalViewModel *model = [EHIInfoModalViewModel new];
    model.title             = EHILocalizedString(@"cp_your_password_is_outdated", @"Your Password is Outdated", @"");
    model.details           = EHILocalizedString(@"cp_our_requirements_have_been_changed", @"Our password requirements have changed. You will be prompted to update your password.", @"");
    model.firstButtonTitle  = EHILocalizedString(@"standard_ok_text", @"OK", @"");
    model.secondButtonTitle = EHILocalizedString(@"standard_button_cancel", @"CANCEL", @"");
    
    __weak typeof(model) wodel = model;
    [model present:^BOOL(NSInteger index, BOOL canceled) {
        BOOL update = index == 0;

        [wodel dismissWithCompletion:^{
            [self didFinishAuthenticationWithUser:nil credentials:credentials error:[EHIServicesError servicesErrorFailure]];
            ehi_call(handler)(nil, [EHIServicesError servicesErrorFailure]);
            
            if(update) {
                [EHIMainRouter currentRouter].transition.dismiss.push(EHIScreenProfilePassword).object(credentials).start(nil);
            }
        }];
        
        return NO;
    }];
}

- (void)handleLoginError:(EHIServicesError *)error credentials:(EHIUserCredentials *)credentials handler:(EHIUserHandler)handler
{
    [error consume];
    [self logoutCurrentUser];

    [self didFinishAuthenticationWithUser:nil credentials:credentials error:[EHIServicesError servicesErrorFailure]];
    ehi_call(handler)(nil, [EHIServicesError servicesErrorFailure]);
}

# pragma mark - Rentals

- (void)refreshPastRentalsWithHandler:(EHIUserHandler)handler
{
    EHIUser *user = self.currentUser;
    
    // fire request if needed
    if(!self.pastRentalsRequest && user) {
        user.pastRentals = nil;
        
        __weak typeof(self) welf = self;
        self.pastRentalsRequest = [[EHIServices sharedInstance] fetchPastRentalsWithHandler:^(EHIUserRentals *rentals, EHIServicesError *error) {
            // update cached rentals
            if(!error.hasFailed) {
                user.pastRentals = rentals;
            }
            
            // call handlers with user, if it's still valid
            for(EHIUserHandler handler in welf.pastHandlers) {
                ehi_call(handler)([welf.currentUser isEqual:user] ? user : nil, error);
            }
            
            [welf.pastHandlers removeAllObjects];
            
            welf.pastRentalsRequest = nil;
        }];
    }
    
    // batch handlers
    [self.pastHandlers ehi_safelyAppend:handler];
}

- (void)currentAndUpcomingRentalsWithHandler:(EHIUserHandler)handler
{
    // if we-re currently refreshing our feeds, batch the handler
    if(self.isRefreshingRentals) {
        [self.handlers ehi_safelyAppend:handler];
    }
    // otherwise, call back the handler with what we have
    else {
        ehi_call(handler)(self.currentUser, nil);
    }
}

- (void)refreshCurrentAndUpcomingRentalsWithHandler:(EHIUserHandler)handler
{
    EHIUser *user = self.currentUser;
   
    // if we don't have a request out
    if(!self.rentalsRequest && user) {
        user.currentRentals  = nil;
        user.upcomingRentals = nil;
        
        // make the service call
        self.rentalsRequest = [self fetchRentalsForUser:user handler:^(EHIUser *user, EHIServicesError *error) {
            [self didFinishRefreshingRentalsForUser:user error:error];
            [self setRentalsRequest:nil];
        }];
    }
    
    // batch the handler for the current request
    [self currentAndUpcomingRentalsWithHandler:handler];
}

- (id<EHINetworkCancelable>)fetchRentalsForUser:(EHIUser *)user handler:(EHIUserHandler)handler
{
    if(user.loyaltyNumber.length == 0) {
        return nil;
    }
    // batch all the rentals calls up together
    dispatch_group_t group = dispatch_group_create();
   
    // helper that wraps the handler in a block that automatically leaves the dispatch group
    // and suppresses errors
    EHIUserRentalsHandler(^wrap)(EHIUserRentalsHandler handler) = ^(EHIUserRentalsHandler handler) {
        return ^(EHIUserRentals *rentals, EHIServicesError *error) {
            dispatch_group_leave(group);
            if(!error.hasFailed) {
                ehi_call(handler)(rentals, error);
            }
        };
    };
   
    // create a group for canceling the requests as a unit
    EHINetworkCancelableGroup *cancelable = [EHINetworkCancelableGroup new];
    
    // add each request to the group and wrap each handler
    dispatch_group_enter(group);
    [cancelable addCancelable:[[EHIServices sharedInstance] fetchCurrentRentalsWithHandler:wrap(^(EHIUserRentals *rentals, EHIServicesError *error) {
        user.currentRentals = rentals;
    })]];
    
    dispatch_group_enter(group);
    [cancelable addCancelable:[[EHIServices sharedInstance] fetchUpcomingRentalsWithHandler:wrap(^(EHIUserRentals *rentals, EHIServicesError *error) {
        user.upcomingRentals = rentals;
    })]];
   
    // call the handler with this user, if it's still valid
    dispatch_group_notify(group, dispatch_get_main_queue(), ^{
        ehi_call(handler)([self.currentUser isEqual:user] ? user : nil, nil);
    });
    
    return cancelable;
}

- (void)setRentalsRequest:(id<EHINetworkCancelable>)rentalsRequest
{
    // cancel the old request if it happens to still be running
    [_rentalsRequest cancel];
    
    _rentalsRequest = rentalsRequest;
}

- (BOOL)isRefreshingRentals
{
    // if the flag is explicitly set, use that
    return _isRefreshingRentals || self.rentalsRequest;
}

- (void)clearRentalsForUser:(EHIUser *)user
{
    // destroy the users current rentals
    user.pastRentals     = nil;
    user.currentRentals  = nil;
    user.upcomingRentals = nil;
}

# pragma mark - Credentials

- (void)setCredentials:(EHIUserCredentials *)credentials modifyCache:(BOOL)cache
{
    // delete the old credentials from the data store, if possible
    if(cache && _credentials) {
        [EHIDataStore remove:_credentials handler:nil];
    }
    
    _credentials = credentials;
    
    // save the new credentials if necessary
    if(cache && credentials.remembersCredentials) {
         credentials.expiryDate = [[NSDate ehi_today] ehi_addDays:EHIDaysPerYear];
        [EHIDataStore save:credentials handler:nil];
    }
}

- (BOOL)isEmeraldUser
{
    return self.currentUser != nil && self.credentials.isEmeraldCredentials;
}

//
// Helpers
//

- (void)kickoffExpiryTimerForCredentials
{
    // expire the old timer
    [self.expiryTimer invalidate];
    
    // create a timer for 24 hours from now
    NSTimer *timer = [NSTimer timerWithTimeInterval:EHIUnrememberedUserExpiryTime target:self selector:@selector(credentialsDidExpireWithTimer:) userInfo:nil repeats:NO];
   
    // schedule it in the run loop and store it
    [[NSRunLoop mainRunLoop] addTimer:timer forMode:NSRunLoopCommonModes];
    self.expiryTimer = timer;
}

- (void)credentialsDidExpireWithTimer:(NSTimer *)timer
{
    // logout if this is our current expiry timer
    if(self.expiryTimer == timer) {
        [self logoutCurrentUser];
        [self setExpiryTimer:nil];
    }
}

# pragma mark - Privacy

- (void)clearData
{
    // clear ec credentials if we have them
    [EHIDataStore first:[EHIUserCredentials class] handler:^(EHIUserCredentials *credentials) {
        if(credentials.isEmeraldCredentials) {
            [EHIDataStore purge:[EHIUserCredentials class] handler:nil];
        }
    }];
}

# pragma mark - Listeners

- (void)addListener:(id<EHIUserListener>)listener
{
    [self.listeners addObject:listener];
   
    // if the user listens for auth change, update them with the default state
    [self notifyListener:listener forSelector:@selector(manager:didChangeAuthenticationForUser:)];
}

- (void)removeListener:(id<EHIUserListener>)listener
{
    [self.listeners removeObject:listener];
}

- (void)notifyListenersForSelector:(SEL)selector
{
    for(id<EHIUserListener> listener in self.listeners) {
        [self notifyListener:listener forSelector:selector];
    }
}

- (BOOL)notifyListener:(id<EHIUserListener>)listener forSelector:(SEL)selector
{
    BOOL doesRespond = [listener respondsToSelector:selector];
    
    if(doesRespond) {
        IGNORE_PERFORM_SELECTOR_WARNING(
            [listener performSelector:selector withObject:self withObject:self.currentUser];
        );
    }

    return doesRespond;
}

- (void)purgeCredentials
{
    [EHIDataStore purge:[EHIUserCredentials class] handler:nil];
}

# pragma mark - Current User

- (void)setCurrentUser:(EHIUser *)currentUser
{
    if(_currentUser == currentUser) {
        return;
    }
    
    if(currentUser) {
        [EHIDataStore save:currentUser handler:nil];
    } else {
        [EHIDataStore purge:[EHIUser class] handler:nil];
    }

    // cancel any active request if the user changes
    self.rentalsRequest = nil;
  
    // notify listeners of the sign out if we have a current user
    if(_currentUser) {
        [self notifyListenersForSelector:@selector(manager:didSignOutUser:)];
    }
    
    _currentUser = currentUser;

    // notify listeners if a user signs in
    if(_currentUser) {
        [self notifyListenersForSelector:@selector(manager:didSignInUser:)];
    }
   
    // always notify listeners when auth changes
    [self notifyListenersForSelector:@selector(manager:didChangeAuthenticationForUser:)];
}

# pragma mark - Notifications

- (void)didEnterForeground:(NSNotification *)notification
{
    // re-fetch the rentals when entering foreground
    [self refreshCurrentAndUpcomingRentalsWithHandler:^(EHIUser *user, EHIServicesError *error) {
        // prevent the default error handling
        [error consume];
    }];
}

- (void)didEnterBackground:(NSNotification *)notification
{
    // throw away rentals on entering background
    [self clearRentalsForUser:[EHIUser currentUser]];
}

- (void)willTerminate:(NSNotification *)notification
{
    // clean credentials if user is EC and don't want to remember credentials
    BOOL isEmerald = self.isEmeraldUser;
    BOOL rememberingCredentials = self.credentials.remembersCredentials;
    if(isEmerald && !rememberingCredentials) {
        [self logoutCurrentUser];
        [self clearData];
    }
}

- (void)resetEnrollment
{
    self.enrollmentProfile = nil;
    self.profileMatch = EHIEnrollmentProfileMatchNone;
}

# pragma mark - Debug

- (void)setSignInOption:(EHIUserSignInOption)signInOption
{
    _signInOption = signInOption;
    
    [[NSNotificationCenter defaultCenter] postNotificationName:EHIUserSignInOptionChangedNotification object:nil];
}

@end
