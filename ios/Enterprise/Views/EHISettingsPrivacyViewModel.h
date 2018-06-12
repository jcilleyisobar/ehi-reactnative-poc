//
//  EHISettingsPrivacyViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 6/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHISettingsControlViewModel.h"

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSUInteger, EHISettingsPrivacyRow) {
    EHISettingsPrivacyRowAutoSave,
    EHISettingsPrivacyRowDataCollection,
    EHISettingsPrivacyRowSaveHistory,
    EHISettingsPrivacyRowUsePreferredCard,
    EHISettingsPrivacyRowClearData,
    EHISettingsPrivacyRowClearAnalyticsData
};

@interface EHISettingsPrivacyViewModel : EHISettingsControlViewModel <MTRReactive>

@property (assign, nonatomic) EHISettingsPrivacyRow row;

@end

NS_ASSUME_NONNULL_END
