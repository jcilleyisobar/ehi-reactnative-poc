//
//  EHIMenuItem.m
//  Enterprise
//
//  Created by Ty Cobb on 3/30/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIMenuItem.h"
#import "EHIUserManager.h"
#import "EHIAlertViewBuilder.h"
#import "EHIAnalyticsKeys.h"
#import "EHIConfiguration.h"
#import "EHIUserManager.h"

@interface EHIMenuItem ()
@property (copy  , nonatomic) EHIMenuTransition transition;
@end

@implementation EHIMenuItem

- (void(^)(NAVTransitionBuilder *))transition
{
    EHIMenuTransition result = _transition;
   
    // if we don't have a custom transition and do have a destination, auto-generate
    // a root-switch transition
    if(!result && self.root) {
        return ^(NAVTransitionBuilder *transition) {
            transition.root(self.root).animated(NO);
        };
    }

    return result;
}

# pragma mark - Generation

+ (NSArray *)items
{
    EHIMenuItem *model;
    
    return [EHIMenuItem modelsWithDictionaries:@[@{
         @key(model.type)     : @(EHIMenuItemTypePromotion),
         @key(model.row)      : @(EHIMenuItemRowPromotion),
        //TODO: Check analytics key
        //@key(model.analyticsAction) : EHIAnalyticsMenuActionDashboard,
    }, @{
        @key(model.type)     : @(EHIMenuItemTypeScreen),
        @key(model.row)      : @(EHIMenuItemRowHome),
        @key(model.header)   : @(EHIMenuItemHeaderNone),
        @key(model.title)    : EHILocalizedString(@"menu_home", @"Home", @"title for menu: Home"),
        @key(model.iconName) : @"icon_home_01",
        @key(model.root)     : EHIScreenDashboard,
        @key(model.analyticsAction) : EHIAnalyticsMenuActionDashboard
    }, @{
        @key(model.type)     : @(EHIMenuItemTypeScreen),
        @key(model.row)      : @(EHIMenuItemRowRentals),
        @key(model.header)   : @(EHIMenuItemHeaderEnterprisePlus),
        @key(model.title)    : EHILocalizedString(@"menu_my_rentals", @"My Rentals", @"title for Menu: My Rentals"),
        @key(model.iconName) : @"icon_rentals_01",
        @key(model.root)     : EHIScreenRentals,
        @key(model.analyticsAction) : EHIAnalyticsMenuActionRentals
    }, @{
        @key(model.type)     : @(EHIMenuItemTypeScreen),
        @key(model.row)      : @(EHIMenuItemRowRewards),
        @key(model.header)   : @(EHIMenuItemHeaderEnterprisePlus),
        @key(model.title)    : self.isNotEmeraldUser
                                    ? EHILocalizedString(@"menu_enterprise_plus_rewards", @"My Rewards & Benefits", @"")
                                    : EHILocalizedString(@"menu_learn_about_rewards", @"Learn about Rewards", @""),
        @key(model.iconName) : @"icon_rewards_01",
        @key(model.root)     : self.isNotEmeraldUser ? EHIScreenRewardsBenefitsAuth : EHIScreenRewardsLearnMore,
        @key(model.analyticsAction) : EHIAnalyticsMenuActionRewards,
        @key(model.analyticsAction) : EHIAnalyticsMenuActionRewards
    }, @{
        @key(model.type)     : @(EHIMenuItemTypeScreen),
        @key(model.row)      : @(EHIMenuItemRowSignIn),
        @key(model.iconName) : @"icon_signin_bold",
        @key(model.root)     : EHIScreenDashboard,
        @key(model.analyticsAction) : EHIAnalyticsMenuActionSignIn,
        @key(model.action) : ^(void(^completion)(BOOL)){
            [self transitionToSignInWithCompletion:completion];
        },
        @key(model.header)   : @(EHIMenuItemHeaderEnterprisePlus)
    }, @{
        @key(model.type)     : @(EHIMenuItemTypeScreen),
        @key(model.row)      : @(EHIMenuItemRowProfile),
        @key(model.header)   : @(EHIMenuItemHeaderEnterprisePlus),
        @key(model.title)    : EHILocalizedString(@"menu_profile", @"Profile", @"title for Menu: Profile"),
        @key(model.iconName) : @"icon_profile_01",
        @key(model.root)     : EHIScreenProfile,
        @key(model.analyticsAction) : EHIAnalyticsMenuActionProfile
    }, @{
        @key(model.type)     : @(EHIMenuItemTypeScreen),
        @key(model.row)      : @(EHIMenuItemRowRentalLookUp),
        @key(model.header)   : @(EHIMenuItemHeaderReservation),
        @key(model.title)    : EHILocalizedString(@"menu_look_up_rentals", @"Look Up Rentals", @""),
        @key(model.iconName) : @"icon_rentals_01",
        @key(model.root)     : EHIScreenRentals,
        @key(model.analyticsAction) : EHIAnalyticsMenuActionRentals
    }, @{
        @key(model.type)     : @(EHIMenuItemTypeScreen),
        @key(model.row)      : @(EHIMenuItemRowNewRental),
        @key(model.header)   : @(EHIMenuItemHeaderReservation),
        @key(model.title)    : EHILocalizedString(@"menu_start_a_reservation", @"Start a Reservation", @"title for Menu: start new rental button"),
        @key(model.iconName) : @"icon_reservation",
        @key(model.root)     : EHIScreenLocations,
        @key(model.analyticsAction) : EHIAnalyticsMenuActionStartRental,
        @key(model.action)   : ^(void(^completion)(BOOL)){
            [self transitionToLocationSearchWithCompletion:completion];
        }
    }, @{
        @key(model.type)     : @(EHIMenuItemTypeScreen),
        @key(model.row)      : @(EHIMenuItemRowLocations),
        @key(model.header)   : @(EHIMenuItemHeaderReservation),
        @key(model.title)    : EHILocalizedString(@"menu_look_up_a_location", @"Look Up a Location", @"title for Menu: Locations"),
        @key(model.iconName) : @"icon_locations_01",
        @key(model.root)     : EHIScreenLocations,
        @key(model.analyticsAction) : EHIAnalyticsMenuActionLocations,
        @key(model.action)   : ^(void(^completion)(BOOL)){
            [self transitionToLocationSearchWithCompletion:completion];
        }
    }, @{
        @key(model.type)       : @(EHIMenuItemTypeSecondary),
        @key(model.row)        : @(EHIMenuItemRowFeedback),
        @key(model.header)     : @(EHIMenuItemHeaderSupport),
        @key(model.title)      : EHILocalizedString(@"menu_send_feedback", @"Send Feedback", @""),
        @key(model.root)       : EHIScreenDashboard,
        @key(model.action)     : ^(void(^completion)(BOOL)){
            [self transitionToSupportWithCompletion:completion];
        }
    }, @{
        @key(model.type)    : @(EHIMenuItemTypeSecondary),
        @key(model.row)     : @(EHIMenuItemRowSupport),
        @key(model.header)  : @(EHIMenuItemHeaderSupport),
        @key(model.title)   : EHILocalizedString(@"menu_customer_support", @"Customer Support", @"title for Menu: customer support"),
        @key(model.root)    : EHIScreenCustomerSupport,
        @key(model.analyticsAction) : EHIAnalyticsMenuActionSupport
    }, @{
        @key(model.type)    : @(EHIMenuItemTypeSecondary),
        @key(model.row)     : @(EHIMenuItemRowSettings),
        @key(model.header)  : @(EHIMenuItemHeaderSupport),
        @key(model.title)   : EHILocalizedString(@"menu_settings", @"Settings", @"title for Menu: settings"),
        @key(model.root)    : EHIScreenSettings,
        @key(model.analyticsAction) : EHIAnalyticsMenuActionSettings
    },
        [self signOutMenuItem],
    @{
        @key(model.type)   : @(EHIMenuItemTypeSecondary),
        @key(model.row)    : @(EHIMenuItemRowDebug),
        @key(model.header) : @(EHIMenuItemHeaderSupport),
        @key(model.title)  : @"Debug Options",
        @key(model.root)   : EHIScreenDebug
    }]];
}

+ (NSDictionary *)signOutMenuItem
{
    EHIMenuItem *model;
    BOOL isEmeraldClub = [[EHIUserManager sharedInstance] isEmeraldUser];
    
    NSMutableDictionary *signOut = [@{
        @key(model.type)    : @(EHIMenuItemTypeSecondary),
        @key(model.row)     : @(EHIMenuItemRowSignout),
        @key(model.header)  : @(EHIMenuItemHeaderEnterprisePlus),
        @key(model.root)    : EHIScreenDashboard,
        @key(model.iconName) : @"arrow_smwhite",
        @key(model.analyticsAction) : EHIAnalyticsMenuActionSignOut
    } mutableCopy];
    
    id completionHandler;
    
    if(isEmeraldClub) {
        completionHandler = [^(void(^completion)(BOOL)) {
            [[EHIUserManager sharedInstance] promptLogoutWithHandler:completion];
            [[EHIUserManager sharedInstance] clearData];
        } copy];
    } else {
        completionHandler = [^(void(^completion)(BOOL)) {
            [[EHIUserManager sharedInstance] promptLogoutWithHandler:completion];
        } copy];
    }
    
    [signOut setObject:completionHandler forKey:@key(model.action)];
    
    return signOut;
}

+ (BOOL)isNotEmeraldUser
{
    return [[EHIUserManager sharedInstance] currentUser] != nil && ![[EHIUserManager sharedInstance] isEmeraldUser];
}

# pragma mark - Accessors

- (NSString *)headerTitle
{
    switch (self.header) {
        case EHIMenuItemHeaderNone:
            return nil;
        case EHIMenuItemHeaderEnterprisePlus:
            return self.isLogged
            ? EHILocalizedString(@"menu_section_my_enterprise_plus_auth", @"MY ENTERPRISE PLUS", @"")
            : EHILocalizedString(@"menu_section_my_enterprise_plus_unauth", @"ENTERPRISE PLUS", @"");
        case EHIMenuItemHeaderReservation:
            return EHILocalizedString(@"menu_section_reservations_location_search", @"RESERVATIONS & LOCATIONS", @"");
        case EHIMenuItemHeaderSupport:
            return EHILocalizedString(@"menu_section_support_tools", @"SUPPORT & TOOLS", @"");
    }
}

- (NSAttributedString *)attributedTitle
{
    switch (self.row) {
        case EHIMenuItemRowSignIn: {
            NSString *title = EHILocalizedString(@"login_title", @"Sign In", @"title for Menu: E+ sign in");
            return EHIAttributedStringBuilder.new.appendText(title).fontStyle(EHIFontStyleBold, 20.0f).string;
        }
        case EHIMenuItemRowSignout:
            return EHIAttributedStringBuilder.new.appendText(self.signOutTitle).fontStyle(EHIFontStyleBold, 16.0f).string;
        default:
            return nil;
    }
}

- (NSString *)signOutTitle
{
    return [[EHIUserManager sharedInstance] isEmeraldUser]
    ? EHILocalizedString(@"menu_emerald_club_sign_out", @"Remove National Emerald Club Account", @"title for Menu Sign Out")
    : EHILocalizedString(@"menu_sign_out", @"Sign Out", @"title for Menu Sign Out");
}

- (BOOL)isLogged
{
    return [[EHIUserManager sharedInstance] currentUser] != nil && ![[EHIUserManager sharedInstance] isEmeraldUser];
}

# pragma mark - Actions

+ (void)transitionToLocationSearchWithCompletion:(void(^)(BOOL))completion
{
    EHIMainRouter.router.transition
        .root(EHIScreenDashboard)
        .push(EHIScreenLocations).animated(NO).start(nil);
    
    ehi_call(completion)(YES);
}

+ (void)transitionToSignInWithCompletion:(void(^)(BOOL))completion
{
    EHIMainRouter.router.transition
        .root(EHIScreenDashboard)
        .present(EHIScreenMainSignin).animated(YES).start(nil);

    
    ehi_call(completion)(YES);
}

+ (void)transitionToSupportWithCompletion:(void(^)(BOOL))completion
{
    EHIMainRouter.router.transition
        .root(EHIScreenDashboard)
        .present(EHIScreenWebBrowser).animated(YES)
        .object([NSURL URLWithString:[EHIConfiguration configuration].feedbackUrl])
        .start(nil);
    
    ehi_call(completion)(YES);
}

@end
