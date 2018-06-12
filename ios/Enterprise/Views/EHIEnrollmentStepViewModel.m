//
//  EHIEnrollmentStepViewModel.m
//  Enterprise
//
//  Created by Rafael Machado on 8/11/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIEnrollmentStepViewModel.h"
#import "EHIRequiredInfoViewModel.h"
#import "EHIServices+User.h"

@implementation EHIEnrollmentStepViewModel

- (NSString *)title
{
    return EHILocalizedString(@"enroll_window_title", @"Join Enterprise Plus", @"");
}

- (void)setStep:(EHIEnrollmentStep)step
{
    _step = step;
    
    self.headerModel = [[EHIEnrollmentStepHeaderViewModel alloc] initWithStep:step];
}

- (void)persistUser:(EHIUser *)user
{
    // avoid overwriting existing data
    NSString *password = self.enrollmentProfile.password;
    BOOL readTerms     = self.enrollmentProfile.terms.acceptDecline;
    [self persistUser:user password:password readTerms:readTerms];
}

- (void)reset
{
    EHIUserManager *instance = [EHIUserManager sharedInstance];
    
    instance.enrollmentProfile = nil;
    instance.profileMatch = EHIEnrollmentProfileMatchNone;
}

# pragma mark - Accessors

- (void)persistUser:(EHIUser *)user password:(NSString *)password readTerms:(BOOL)terms
{
    EHIEnrollProfile *enroll = [EHIEnrollProfile modelForUser:user password:password acceptedTerms:terms];
    
    [EHIUserManager sharedInstance].enrollmentProfile = enroll;
}

- (EHIEnrollProfile *)enrollmentProfile
{
    return [EHIUserManager sharedInstance].enrollmentProfile;
}

- (void)setProfileMatch:(EHIEnrollmentProfileMatch)profileMatch
{
    [EHIUserManager sharedInstance].profileMatch = profileMatch;
}

- (EHIEnrollmentProfileMatch)profileMatch
{
    return [EHIUserManager sharedInstance].profileMatch;
}

- (BOOL)didMatchProfile
{
    return self.profileMatch != EHIEnrollmentProfileMatchNone
        && self.profileMatch != EHIEnrollmentProfileMatchNoMatch;
}

- (EHIRequiredInfoViewModel *)requiredInfoModel
{
    if(!_requiredInfoModel) {
        _requiredInfoModel = [EHIRequiredInfoViewModel modelForInfoType:EHIRequiredInfoTypeEnroll];
    }
    
    return _requiredInfoModel;
}

- (void)cloneCreateProfile:(EHIEnrollProfile *)profile handler:(void (^)(EHIUser *user, EHIServicesError *error))handler
{
    if(self.didMatchProfile) {
        [[EHIServices sharedInstance] cloneEnrollProfile:profile handler:handler];
    } else {
        [[EHIServices sharedInstance] createEnrollProfile:profile handler:handler];
    }
}

# pragma mark - Analytics

- (NSString *)currentState
{
    switch (self.profileMatch) {
        case EHIEnrollmentProfileMatchNone:
            return EHIAnalyticsEnrollmentNone;
        case EHIEnrollmentProfileMatchNoMatch:
            return EHIAnalyticsEnrollmentProfileNoMatch;
        case EHIEnrollmentProfileMatchNonLoyalty:
            return EHIAnalyticsEnrollmentProfileNonLoyalty;
        case EHIEnrollmentProfileMatchEmeraldClub:
            return EHIAnalyticsEnrollmentProfileEmerald;
        case EHIEnrollmentProfileMatchEnterprisePlus:
            return EHIAnalyticsEnrollmentProfileEPlus;
    }
}

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    [super updateAnalyticsContext:context];
    
    context.state = self.currentState;
    context[EHIAnalyticsEnrollmentProfileMatchKey] = self.currentState ?: @"";
    
    NSString *countryCode = self.enrollmentProfile.license.countryCode;
    if(countryCode) {
        context[EHIAnalyticsEnrollmentProfileCountryCodeKey] = countryCode;
    }
}

@end
