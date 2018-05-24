//
//  EHISettingsViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 6/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHISettingsViewModel.h"
#import "EHISettingsNotificationViewModel.h"
#import "EHISettingsSecurityViewModel.h"
#import "EHISettingsPrivacyViewModel.h"
#import "EHISettingsAboutViewModel.h"
#import "EHIInfoModalViewModel.h"
#import "EHINotificationSettingsViewModel.h"
#import "EHIWebViewModel.h"
#import "EHIDataStore.h"
#import "EHILocation.h"
#import "EHIDriverInfo.h"
#import "EHIHistoryManager.h"
#import "EHIUserManager+Analytics.h"
#import "EHISecurityManager.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHISettingsViewModel ()
@property (strong, nonatomic) NSDictionary *sectionHeaders;
@end

@implementation EHISettingsViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title = EHILocalizedString(@"settings_navigation_title", @"Settings", @"navigation bar title for car class selection screen");
    }
    
    return self;
}

- (void)didBecomeActive
{
    [super didBecomeActive];
    
    // only show notification and security settings when logged in
    if([EHIUser currentUser] != nil) {
        self.notificationModels = [EHISettingsNotificationViewModel viewModels];
        
        // filter security models by hardware support
        self.securityModels = [EHISettingsSecurityViewModel viewModels].select(^(EHISettingsSecurityViewModel *viewModel) {
            return [self shouldShowSecurityRow:viewModel.row];
        });
    }
    
    self.privacyModels  = [EHISettingsPrivacyViewModel viewModels].select(^(EHISettingsPrivacyViewModel *viewModel){
        return [self shouldShowPrivacyRow:viewModel.row];
    });
    
    self.aboutModels    = [EHISettingsAboutViewModel viewModels];
}

//
// Helpers
//

- (BOOL)shouldShowSecurityRow:(EHISettingsSecurityRow)row
{
    switch(row) {
        case EHISettingsSecurityRowTouchId:
            return [EHISecurityManager sharedInstance].canUseBiometrics && [EHIUserManager sharedInstance].credentials.remembersCredentials;
    }
}

- (BOOL)shouldShowPrivacyRow:(EHISettingsPrivacyRow)row
{
    switch(row) {
        case EHISettingsPrivacyRowUsePreferredCard:
            return self.isLogged;
        default:
            return YES;
    }
}

- (BOOL)isLogged
{
    EHIUserManager *instance = [EHIUserManager sharedInstance];
    
    return instance.currentUser != nil && !instance.isEmeraldUser;
}

# pragma mark - Actions

- (void)selectItemAtIndexPath:(NSIndexPath *)indexPath
{
    // show options for the selected notification setting
    if(indexPath.section == EHISettingsSectionNotification && indexPath.row != EHISettingsNotificationRowRentalAssistant) {
        [self showNotificationOptionsForRow:indexPath.row];
    }
    // privacy section's
    else if(indexPath.section == EHISettingsSectionPrivacy) {
        EHISettingsPrivacyViewModel *privacyViewModel = [self.privacyModels ehi_safelyAccess:indexPath.row];
        if(privacyViewModel.row == EHISettingsPrivacyRowClearData || (privacyViewModel.row == EHISettingsPrivacyRowUsePreferredCard && !self.isLogged)) {
            [self showClearUserDataModal];
        } else if(privacyViewModel.row == EHISettingsPrivacyRowClearAnalyticsData) {
            [self clearAnalyticsData];
        }
    }
    // show web view with selected policy
    else if(indexPath.section == EHISettingsSectionAbout) {
        [self showAboutPolicyForRow:indexPath.row];
    }
}

//
// Helpers
//

- (void)showNotificationOptionsForRow:(EHISettingsNotificationRow)row
{
    EHINotificationSettingsType type = [self notificationSettingsTypeForSettingsNotificationRow:row];
    EHINotificationSettingsViewModel *viewModel = [[EHINotificationSettingsViewModel alloc] initWithType:type];
    
    [EHIAnalytics trackAction:type == EHINotificationSettingsTypePickup ? EHIAnalyticsActionNotificationRemindPickup : EHIAnalyticsActionNotificationRemindDropOff handler:nil];

    self.router.transition
        .push(EHIScreenSettingsNotifications).object(viewModel).start(nil);
}

- (EHINotificationSettingsType)notificationSettingsTypeForSettingsNotificationRow:(EHISettingsNotificationRow)row
{
    switch(row) {
        case EHISettingsNotificationRowPickup:
            return EHINotificationSettingsTypePickup;
        case EHISettingsNotificationRowReturn:
            return EHINotificationSettingsTypeReturn;
        default:
            return 0;
    }
}

- (void)showClearUserDataModal
{
    [EHIAnalytics trackAction:EHIAnalyticsSettingsActionCleanPersonalData handler:nil];
    
    EHIInfoModalViewModel *viewModel = [EHIInfoModalViewModel new];
    viewModel.title = EHILocalizedString(@"settings_clear_data_confirmation_title", @"Clear Personal Data?", @"");
    viewModel.details = EHILocalizedString(@"settings_clear_data_confirmation_details", @"Are you sure you want to clear personal data, such as your email address, first and last name, and mobile number, from the app?", @"");
    
    viewModel.hidesCloseButton = YES;
    viewModel.firstButtonTitle = EHILocalizedString(@"settings_clear_button", @"CLEAR", @"");
    viewModel.secondButtonTitle = EHILocalizedString(@"settings_cancel_button", @"CANCEL", @"");

    [viewModel present:^(NSInteger index, BOOL canceled) {
        // clear data
        if(index == 0) {
            [self clearUserData];
        }
        
        return YES;
    }];
}

- (void)clearAnalyticsData
{
    EHIInfoModalViewModel *viewModel = [EHIInfoModalViewModel new];
    viewModel.title   = EHILocalizedString(@"right_to_be_forgotten_modal_title", @"Are You Sure You Want To Clear Your National Mobile Analytics & App Usage Historical Data?", @"");
    viewModel.details = EHILocalizedString(@"right_to_be_forgotten_modal_summary", @"By selecting \"Yes\" all your historical data will be cleared and no longer tracked.", @"");
    
    viewModel.hidesCloseButton = YES;
    viewModel.firstButtonTitle  = EHILocalizedString(@"right_to_be_forgotten_modal_confirm", @"Yes", @"");
    viewModel.secondButtonTitle = EHILocalizedString(@"right_to_be_forgotten_modal_cancel", @"No", @"");
    
    EHISettingsPrivacyViewModel *dataCollectionViewModel = (self.privacyModels ?: @[]).find(^(EHISettingsPrivacyViewModel *viewModel){
        return viewModel.row == EHISettingsPrivacyRowDataCollection;
    });
    
    [viewModel present:^(NSInteger index, BOOL canceled) {
        // clear data
        if(index == 0) {
            [EHIAnalytics forgetMe];
            [self clearUserData];
            [dataCollectionViewModel enableToggle:NO];
        }
        
        return YES;
    }];
}

- (void)clearUserData
{
    // clear recent locations
    [EHIDataStore purge:[EHILocation class] handler:nil];
    // clear driver info
    [EHIDataStore purge:[EHIDriverInfo class] handler:nil];
    // clear past and abandoned rentals
    [[EHIHistoryManager sharedInstance] clearHistory];
    // clear applicable (EC) personal authentication data
    [[EHIUserManager sharedInstance] clearData];
}

- (void)showAboutPolicyForRow:(EHISettingsAboutRow)row
{
    // no policy for version row
    if(row == EHISettingsAboutRowVersion) {
        return;
    }
    
    EHIWebContentType type = [self webViewModelTypeForSettingsAboutRow:row];
    [[[EHIWebViewModel alloc] initWithType:type] push];
}

- (EHIWebContentType)webViewModelTypeForSettingsAboutRow:(EHISettingsAboutRow)row
{
    switch(row) {
        case EHISettingsAboutRowPrivacyPolicy:
            return EHIWebContentTypePrivacy;
        case EHISettingsAboutRowTermsOfUse:
            return EHIWebContentTypeTermsOfUse;
        case EHISettingsAboutRowTermsAndConditions:
            return EHIWebContentTypeTermsAndConditions;
        case EHISettingsAboutRowLicenses:
            return EHIWebContentTypeLicenses;
        default: return EHIWebContentTypeNone;
    }
}

# pragma mark - Accessors

- (EHISectionHeaderModel *)headerForSection:(EHISettingsSection)section
{
    return self.sectionHeaders[@(section)];
}

- (NSDictionary *)sectionHeaders
{
    if(_sectionHeaders) {
        return _sectionHeaders;
    }
    
    _sectionHeaders = [EHISectionHeaderModel modelsWithTitles:@[
        EHILocalizedString(@"settings_notifications_section_title", @"NOTIFICATIONS", @""),
        EHILocalizedString(@"settings_security_section_title", @"SECURITY", @""),
        EHILocalizedString(@"settings_privacy_section_title", @"PRIVACY", @""),
        EHILocalizedString(@"settings_about_section_title", @"ABOUT THIS APP", @""),
    ]];
    
    return _sectionHeaders;
}

# pragma mark - Analytics

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    [super updateAnalyticsContext:context];
    // encode the "sign-in" dictionary"
    [[EHIUserManager sharedInstance] updateAnalyticsContext:context];    
}

@end

NS_ASSUME_NONNULL_END
