//
//  EHIDashboardLoyaltyViewModel.h
//  Enterprise
//
//  Created by mplace on 5/18/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIDashboardLoyaltyViewModel : EHIViewModel <MTRReactive>
/** Determines whether the authenticated or unauthenticated view is visible */
@property (assign, nonatomic) BOOL isAuthenticated;

// Unauthenticated
@property (copy, nonatomic) NSString *title;
@property (copy, nonatomic) NSString *signInButtonTitle;

// Authenticated
@property (copy, nonatomic) NSString *greetingTitle;
@property (copy, nonatomic) NSString *greetingSubtitle;
@property (copy, nonatomic) NSString *pointsTitle;
@property (copy, nonatomic) NSString *pointsSubtitle;

/** Displays the sign in screen modally */
- (void)presentSignIn;
- (void)pushProfile;
- (void)pushRewards;

@end
