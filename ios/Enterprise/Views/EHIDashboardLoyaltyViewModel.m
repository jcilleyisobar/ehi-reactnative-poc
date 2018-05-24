//
//  EHIDashboardLoyaltyViewModel.m
//  Enterprise
//
//  Created by mplace on 5/18/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDashboardLoyaltyViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIUserManager.h"
#import "EHIDataStore.h"

@interface EHIDashboardLoyaltyViewModel () <EHIUserListener>

@end

@implementation EHIDashboardLoyaltyViewModel

- (instancetype)initWithModel:(id)model
{
    if (self = [super initWithModel:model]) {
        // unauthenticated
        _title = EHILocalizedString(@"dashboard_loyalty_cell_title", @"Sign in to earn points & make booking even faster.", @"title for the loyalty cell on the dashboard");
        _signInButtonTitle = EHILocalizedString(@"dashboard_loyalty_cell_sign_in_title", @"SIGN IN", @"title for the loyalty cell sign in button on the dashboard");
        
        // authenticated
        _greetingTitle = EHILocalizedString(@"dashboard_loyalty_cell_authenticated_greeting_title", @"WELCOME,", @"greeting title for the authenticated loyalty cell");
        _pointsTitle   = EHILocalizedString(@"dashboard_loyalty_cell_authenticated_points_title", @"POINTS BALANCE", @"points balance title for the authenticated loyalty cell");

        // add ourselves as a listener for further authentication state changes
        [[EHIUserManager sharedInstance] addListener:self];
    }

    return self;
}

# pragma mark - EHIUserListener

- (void)manager:(EHIUserManager *)manager didChangeAuthenticationForUser:(EHIUser *)user
{
    self.isAuthenticated = user != nil && !self.isEmeraldUser;

    [self invalidateLoyaltyForUser:user];
}

- (void)manager:(EHIUserManager *)manager didRefreshUser:(EHIUser *)user
{
    [self invalidateLoyaltyForUser:user];
}

//
// Helpers
//

- (BOOL)isEmeraldUser
{
    return [[EHIUserManager sharedInstance] isEmeraldUser];
}

- (void)invalidateLoyaltyForUser:(EHIUser *)user
{
    self.pointsSubtitle   = user.loyaltyPoints ?: @"";
    self.greetingSubtitle = user.firstName;
}

# pragma mark - Actions

- (void)presentSignIn
{
    [EHIAnalytics trackAction:EHIAnalyticsDashActionJoin handler:nil];
    
    // present the sign in screen
    self.router.transition
        .present(EHIScreenMainSignin).start(nil);
}

- (void)pushProfile
{
    self.router.transition
        .push(EHIScreenProfile).start(nil);
}

- (void)pushRewards
{
    self.router.transition
        .push(EHIScreenRewardsBenefitsAuth).start(nil);
}

@end
