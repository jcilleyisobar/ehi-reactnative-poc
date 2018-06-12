//
//  EHISecurityManager.m
//  Enterprise
//
//  Created by Alex Koller on 1/20/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import <LocalAuthentication/LocalAuthentication.h>
#import "EHISecurityManager.h"

@interface EHISecurityManager ()
@property (strong, nonatomic) LAContext *context;
@property (strong, nonatomic) UIImageView *securityOverlay;
@end

@implementation EHISecurityManager

+ (instancetype)sharedInstance
{
    static EHISecurityManager *manager;
    static dispatch_once_t onceToken;
    
    dispatch_once(&onceToken, ^{
        manager = [EHISecurityManager new];
    });
    
    return manager;
}

- (instancetype)init
{
    if(self = [super init]) {
        // common context for checking touch id availability
        _context = [LAContext new];
        
        // register for app state notifications for adding security overlay
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didBecomeActive:) name:UIApplicationDidBecomeActiveNotification object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(willResignActive:) name:UIApplicationWillResignActiveNotification object:nil];
    }
    
    return self;
}

# pragma mark - Notifications

- (void)didBecomeActive:(NSNotification *)notification
{
    if(self.shouldHideContent) {
        [self.securityOverlay setHidden:YES];
    }
}

- (void)willResignActive:(NSNotification *)notification
{
    if(self.shouldHideContent) {
        // make sure we cover everything
        [self.securityOverlay.superview bringSubviewToFront:self.securityOverlay];
        [self.securityOverlay setHidden:NO];
    }
}

# pragma mark - Lazy

- (UIImageView *)securityOverlay
{
    if(_securityOverlay) {
        return _securityOverlay;
    }
    
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    UIImage *securityImage = [UIImage launchImage];
    
    _securityOverlay = [[UIImageView alloc] initWithImage:securityImage];
    
    // insert fullscreen over all views
    [window addSubview:_securityOverlay];
    [_securityOverlay mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(window);
    }];
    
    return _securityOverlay;
}

# pragma mark - Touch ID

- (void)evaluateBiometricsWithReason:(NSString *)reason handler:(EHIBiometricHandler)handler
{
    [[LAContext new] evaluatePolicy:self.evaluationPolicy localizedReason:reason reply:^(BOOL success, NSError * _Nullable error) {
        // handle possible error
        dispatch_main_async(^{
            [self renderError:error];
        });
        
        // invoke success callback
        ehi_call(handler)(success);
    }];
}

- (BOOL)canUseBiometrics
{
    NSError *error;
    BOOL result = [self.context canEvaluatePolicy:self.availabilityPolicy error:&error];

    // device is capable as long as passcode is set and Touch ID is detected
    return result || error.code == LAErrorTouchIDNotEnrolled;
}

- (BOOL)canEvaluateBiometrics
{
    NSError *error;
    BOOL result = [self.context canEvaluatePolicy:self.availabilityPolicy error:&error];

    // show possible availability errors
    [self renderError:error];
    
    return result;
}

# pragma mark - Accessors

- (LAPolicy)availabilityPolicy
{
    // require biometrics to be considered available
    return LAPolicyDeviceOwnerAuthenticationWithBiometrics;
}

- (LAPolicy)evaluationPolicy
{
    return LAPolicyDeviceOwnerAuthentication;
}

# pragma mark - Errors

- (void)renderError:(NSError *)error
{
    // all supported errors
    switch(error.code) {
        // availability errors
        case LAErrorPasscodeNotSet:
        case LAErrorTouchIDNotAvailable:
            break;
        case LAErrorTouchIDNotEnrolled:
            [self showNotEnrolledAlert]; break;
        // evaluation errors
        case LAErrorAuthenticationFailed:
        case LAErrorTouchIDLockout:
            [self showAuthenticationFailedAlert]; break;
        case LAErrorUserCancel:
        case LAErrorUserFallback:
        case LAErrorSystemCancel:
        case LAErrorAppCancel:
        case LAErrorInvalidContext:
            break;
    }
}

- (void)showNotEnrolledAlert
{
    NSString *title = EHILocalizedString(@"touch_id_not_enrolled_title", @"Touch ID Unavailable", @"");
    NSString *message = EHILocalizedString(@"touch_id_not_enrolled_message", @"You have not fingers enrolled in Touch ID.", @"");
    
    [self showAlertWithTitle:title message:message];
}

- (void)showAuthenticationFailedAlert
{
    NSString *title = EHILocalizedString(@"touch_id_authentication_failed_title", @"Authentication Failed", @"");
    NSString *message = EHILocalizedString(@"touch_id_authentication_failed_message", @"Unable to verify your credentials.", @"");

    [self showAlertWithTitle:title message:message];
}

//
// Helpers
//

- (void)showAlertWithTitle:(NSString *)title message:(NSString *)message
{
    EHIAlertViewBuilder.new
        .title(title)
        .message(message)
        .cancelButton(nil)
        .show(nil);
}

@end
