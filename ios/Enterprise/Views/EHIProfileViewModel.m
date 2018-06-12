//
//  EHIProfileViewModel.m
//  Enterprise
//
//  Created by fhu on 5/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIProfileViewModel.h"
#import "EHIProfileItem.h"
#import "EHIUserManager+DNR.h"
#import "EHIUserManager+Analytics.h"
#import "EHISecurityManager.h"
#import "EHISettings.h"
#import "EHIPaymentViewModel.h"

@interface EHIProfileViewModel()
@property (copy, nonatomic) NSDictionary *sectionHeaders;
@end

@implementation EHIProfileViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        // generate static content
        _title          = EHILocalizedString(@"profile_navigation_title", @"Profile", @"navigation bar title for Profilepage");
        _signoutButton  = [EHIBarButtonItem buttonWithType:EHIButtonTypeSignout target:self action:@selector(didSelectSignout:)];
        
        // check authenticated status
        _authenticated = [self determineAuthenticationStatus];
       
        // update profile information
        [self invalidateSections];
    }
    
    return self;
}

- (void)didBecomeActive
{
    [super didBecomeActive];
    
    // try and authenticate with touch id if opted-in and remember me was selected
    if(!self.authenticated && [EHIUserManager sharedInstance].credentials.remembersCredentials && [EHISettings sharedInstance].useTouchId) {
        dispatch_after_seconds(1.0, ^{
            [self authenticateWithBiometrics];
        });
    }
    
    [self invalidateSections];
}

//
// Helpers
//

- (BOOL)determineAuthenticationStatus
{
    // the timeout is 5 minutes after last authentication
    NSDate *timeout = [[EHIUserManager sharedInstance].credentials.authenticationDate ehi_addMinutes:5];
    
    return [timeout ehi_isAfter:[NSDate date]];
}

- (void)invalidateSections
{
    [self invalidateBasicInformation];
    [self invalidateLicenseInformation];
    [self invalidatePaymentInformation];
    [self invalidateAddCard];
}

- (void)invalidateBasicInformation
{
    self.basicInformation = [EHIProfileItem memberInfoItems].select(^(EHIProfileItem *item) {
        return item.data != nil;
    });

}

- (void)invalidateLicenseInformation
{
    self.licenseInformation = [EHIProfileItem driverLicenseItems].select(^(EHIProfileItem *item) {
        return item.data != nil;
    });
}

- (void)invalidatePaymentInformation
{
    EHIUserPaymentProfile *paymentProfile = [EHIUser currentUser].payment;
    self.paymentInformation = [EHIProfilePaymentViewModel new];
    [self.paymentInformation updateWithModel:paymentProfile];
}

- (void)invalidateAddCard
{
    self.addCardModel = self.canAddNewCreditCard && [NSLocale ehi_shouldAllowProfilePaymentEdit] ? [EHIProfilePaymentAddViewModel new] : nil;
    
    if(self.addCardModel) {
        self.addCardModel.hideDivider = YES;
    }
}

- (BOOL)canAddNewCreditCard
{
    NSInteger cardTotal = self.paymentMethods.select(^(EHIUserPaymentMethod *paymentMethod){
        return paymentMethod.paymentType == EHIUserPaymentTypeCard;
    }).count;
    return cardTotal < EHIUserPaymentMethodMaxNumberOfCreditCardsAllowed;
}

- (NSArray *)paymentMethods
{
    return [EHIUser currentUser].payment.paymentMethods ?: @[];
}

# pragma mark - Authentication

- (void)authenticateWithBiometrics;
{
    NSString *reason = EHILocalizedString(@"profile_fingerprint_unlock_prompt", @"Authenticate to edit your profile.", @"");
    
    void (^deferBlock)() = ^{
        self.isLoading = NO;
    };

    self.isLoading = YES;
    [[EHISecurityManager sharedInstance] evaluateBiometricsWithReason:reason handler:^(BOOL success) {
        // attempt to refresh the use with existing encrypted credentials
        if(success) {
            [[EHIUserManager sharedInstance] refreshCredentialsWithHandler:^(EHIUser *user, EHIServicesError *error) {
                self.authenticated = !error.hasFailed;
                ehi_call(deferBlock)();

                // sometimes above code runs faster than reactions
                [[MTRReactor reactor] flush];
            }];
        } else {
            ehi_call(deferBlock)();
        }
    }];
}

# pragma mark - Actions

- (void)didSelectSignout:(id)sender
{
    [[EHIUserManager sharedInstance] promptLogoutWithHandler:^(BOOL didLogout) {
        if(didLogout) {
            EHIMainRouter.router.transition
                .root(EHIScreenDashboard).start(nil);
        }
    }];
}

- (void)didTapAddCreditCard
{
    [EHIAnalytics trackAction:EHIAnalyticsProfileActionAddCreditCard handler:nil];
    
    __weak __typeof(self) welf = self;
    self.router.transition.push(EHIScreenPayment).object(@(EHIPaymentViewStyleProfile)).handler(^{
        [welf invalidatePaymentInformation];
        [welf invalidateAddCard];
    }).start(nil);
}

- (void)didTapActionForHeaderForSection:(NSUInteger)section
{
    __weak typeof(self) welf = self;
    switch(section) {
        case EHIProfileSectionBasic: {
            [self attemptToTransitionToScreen:EHIScreenMemberInfoEdit handler:^{
                [welf invalidateBasicInformation];
            }];
            break;
        }
        case EHIProfileSectionDriver: {
            [self attemptToTransitionToScreen:EHIScreenLicenseEdit handler:^{
                [welf invalidateLicenseInformation];
            }];
            break;
        }
        case EHIProfileSectionPayment: {
            [EHIAnalytics trackAction:EHIAnalyticsProfileActionEditPaymentOptions handler:nil];
            
            [self attemptToTransitionToScreen:EHIScreenProfileEditPaymentMethods handler:^{
                [welf invalidatePaymentInformation];
            }];
            break;
        }
        default:
            break;
    }
}

- (void)attemptToTransitionToScreen:(NSString *)view handler:(id)handler
{
    __weak typeof(self) welf = self;
    [EHIUserManager attemptToShowReturnDnrModalWithHandler:^(BOOL shouldContinue) {
        if (shouldContinue) {
            welf.router.transition
            .push(view).handler(handler).start(nil);
        }
    }];
}

# pragma mark - Accessors

- (EHISectionHeaderModel *)headerForSection:(EHIProfileSection)section
{
    return self.sectionHeaders[@(section)];
}

- (NSDictionary *)sectionHeaders
{
    _sectionHeaders = [EHISectionHeaderModel modelsWithTitles:@[
        EHILocalizedString(@"profile_member_header", @"Member Information", @"Title for member informationlicense section"),
        EHILocalizedString(@"profile_drivers_license_header", @"DRIVER INFORMATION", @"Title for profile driver license section"),
        EHILocalizedString(@"profile_payment_info_header", @"PAYMENT INFORMATION", @"Title for profile payment info section"),
    ]].each(^(NSNumber *index, EHISectionHeaderModel *model) {
        
        void (^actionSetupBlock)() = ^{
            model.style |= EHISectionHeaderStyleAction;
            model.actionButtonTitle = EHILocalizedString(@"profile_edit_action_title", @"EDIT", @"title for edit button on profile");
        };
        
        NSUInteger section = index.unsignedIntegerValue;

        model.style = EHISectionHeaderStyleWrapText;
        model.dividerStyle = section == EHIProfileSectionBasic
            ? EHISectionHeaderDividerStyleDefault
            : EHISectionHeaderDividerStyleFancy;
        
        BOOL isPaymentSection = section == EHIProfileSectionPayment;
        BOOL canEditPayment   = [self canEditPayment];
        if(isPaymentSection) {
            if (canEditPayment){
                ehi_call(actionSetupBlock)();
            }
        } else {
            ehi_call(actionSetupBlock)();
        }
    });
    
    return _sectionHeaders;
}

- (BOOL)canEditPayment
{
    BOOL allowEditing = [NSLocale ehi_shouldAllowProfilePaymentEdit];
    BOOL hasPayment   = self.paymentMethods.count > 0;
    
    return allowEditing && hasPayment;
}

# pragma mark - Analytics

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    [super updateAnalyticsContext:context];
    // encode the "sign-in" dictionary
    [[EHIUserManager sharedInstance] updateAnalyticsContext:context];
}

@end
