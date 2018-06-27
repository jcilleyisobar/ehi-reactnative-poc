//
//  EHISettingsAboutViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 6/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHISettingsAboutViewModel.h"

NS_ASSUME_NONNULL_BEGIN

@implementation EHISettingsAboutViewModel

# pragma mark - Computed

- (BOOL)showsArrow
{
    return self.detailTitle == nil;
}

# pragma mark - Generator

+ (NSArray *)viewModels
{
    EHISettingsAboutViewModel *version = [EHISettingsAboutViewModel new];
    version.row         = EHISettingsAboutRowVersion;
    version.title       = EHILocalizedString(@"settings_about_row_version_title", @"Version", @"");
    version.detailTitle = [NSBundle versionShort];
    
    EHISettingsAboutViewModel *privacyPolicy = [EHISettingsAboutViewModel new];
    privacyPolicy.row   = EHISettingsAboutRowPrivacyPolicy;
    privacyPolicy.title = EHILocalizedString(@"settings_about_row_privacy_policy_title", @"Privacy Policy", @"");
    
    EHISettingsAboutViewModel *termsOfUse = [EHISettingsAboutViewModel new];
    termsOfUse.row   = EHISettingsAboutRowTermsOfUse;
    termsOfUse.title = EHILocalizedString(@"settings_about_row_terms_title", @"Terms of Use", @"");

    EHISettingsAboutViewModel *termsAndConditions = [EHISettingsAboutViewModel new];
    termsAndConditions.row   = EHISettingsAboutRowTermsAndConditions;
    termsAndConditions.title = EHILocalizedString(@"settings_about_row_terms_and_conditions_title", @"Terms & Conditions", @"");
    
    EHISettingsAboutViewModel *licenses = [EHISettingsAboutViewModel new];
    licenses.row   = EHISettingsAboutRowLicenses;
    licenses.title = EHILocalizedString(@"settings_about_row_licenses_title", @"3rd Party Licenses", @"");
    
    return @[version, privacyPolicy, termsOfUse, termsAndConditions, licenses];
}

@end

NS_ASSUME_NONNULL_END