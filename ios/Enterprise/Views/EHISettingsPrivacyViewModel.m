//
//  EHISettingsPrivacyViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 6/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHISettingsPrivacyViewModel.h"

NS_ASSUME_NONNULL_BEGIN

@implementation EHISettingsPrivacyViewModel

+ (NSArray *)viewModels
{
    EHISettingsPrivacyViewModel *autoSave = [EHISettingsPrivacyViewModel new];
    autoSave.row              = EHISettingsPrivacyRowAutoSave;
    autoSave.title            = EHILocalizedString(@"settings_privacy_row_auto_save_title", @"Auto-Save", @"");
    autoSave.details          = [self detailsForRow:EHISettingsPrivacyRowAutoSave];
    autoSave.hidesDetailIcon  = YES;
    autoSave.settingsKey      = NSStringFromProperty(autoSaveUserInfo);

    EHISettingsPrivacyViewModel *dataCollection = [EHISettingsPrivacyViewModel new];
    dataCollection.row         = EHISettingsPrivacyRowDataCollection;
    dataCollection.title       = EHILocalizedString(@"settings_privacy_row_data_collection_title", @"Data Collection", @"");
    dataCollection.details     = [self detailsForRow:EHISettingsPrivacyRowDataCollection];
    dataCollection.settingsKey = NSStringFromProperty(allowDataCollection);
    
    EHISettingsPrivacyViewModel *saveHistory = [EHISettingsPrivacyViewModel new];
    saveHistory.row         = EHISettingsPrivacyRowSaveHistory;
    saveHistory.title       = EHILocalizedString(@"settings_privacy_row_save_history_title", @"Save Search History", @"");
    saveHistory.details     = [self detailsForRow:EHISettingsPrivacyRowSaveHistory];
    saveHistory.settingsKey = NSStringFromProperty(saveSearchHistory);
    
    EHISettingsPrivacyViewModel *clearData = [EHISettingsPrivacyViewModel new];
    clearData.row      = EHISettingsPrivacyRowClearData;
    clearData.title    = EHILocalizedString(@"settings_privacy_row_clear_data_title", @"Clear Personal Data", @"");
    clearData.details  = [self detailsForRow:EHISettingsPrivacyRowClearData];
    clearData.isAction = YES;
    
    EHISettingsPrivacyViewModel *usePreferred = [EHISettingsPrivacyViewModel new];
    usePreferred.row          = EHISettingsPrivacyRowUsePreferredCard;
    usePreferred.title        = EHILocalizedString(@"settings_privacy_use_preferred_card_title", @"Use Preferred Credit Card", @"");
    usePreferred.details      = [self detailsForRow:EHISettingsPrivacyRowUsePreferredCard];
    usePreferred.detailsTitle = EHILocalizedString(@"settings_privacy_use_preferred_card_dialog_title", @"About the setting", @"");
    usePreferred.settingsKey  = NSStringFromProperty(selectPreferredPaymentMethodAutomatically);
    
    EHISettingsPrivacyViewModel *clearAnalyticsData = [EHISettingsPrivacyViewModel new];
    clearAnalyticsData.row          = EHISettingsPrivacyRowClearAnalyticsData;
    clearAnalyticsData.title        = EHILocalizedString(@"right_to_be_forgotten_title", @"Store Mobile Analytics & App Usage Historical Data", @"");
    clearAnalyticsData.detailsTitle = EHILocalizedString(@"right_to_be_forgotten_info_title", @"About Storing Mobile Analytics & App Usage Historical Data", @"");
    clearAnalyticsData.details      = [self detailsForRow:EHISettingsPrivacyRowClearAnalyticsData];
    clearAnalyticsData.isAction     = YES;
    
    return @[autoSave, dataCollection, saveHistory, usePreferred, clearData, clearAnalyticsData];
}

//
// Helpers
//

- (NSString *)detailsTitle
{
    switch(self.row) {
        case EHISettingsPrivacyRowDataCollection:
            return EHILocalizedString(@"settings_privacy_data_collection_modal_title", @"", @"");
        default:
            return self.title;
    }
}

+ (NSString *)detailsForRow:(EHISettingsPrivacyRow)row
{
    switch(row) {
        case EHISettingsPrivacyRowAutoSave:
            return EHILocalizedString(@"settings_privacy_row_auto_save_detail_title", @"Enabling this option will give the app permission to save your email address, first & last name, and your phone number in order to expidite vehicle reservations.", @"");
        case EHISettingsPrivacyRowDataCollection:
            return EHILocalizedString(@"settings_privacy_data_collection_modal_content", @"Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt.", @"");
        case EHISettingsPrivacyRowSaveHistory:
            return EHILocalizedString(@"settings_privacy_save_history_modal_content", @"Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt.", @"");
        case EHISettingsPrivacyRowClearData:
            return EHILocalizedString(@"settings_privacy_clear_data_modal_content", @"Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt.", @"");
        case EHISettingsPrivacyRowUsePreferredCard:
            return EHILocalizedString(@"settings_privacy_use_preferred_card_dialog_message", @"Description of the setting", @"");
        case EHISettingsPrivacyRowClearAnalyticsData:
            return EHILocalizedString(@"right_to_be_forgotten_info_summary", @"By turning this option on, all the historical data that has been collected and stored will be cleared and no longer tracked. You can choose to opt back in at any time.", @"");
    }
}

@end

NS_ASSUME_NONNULL_END
