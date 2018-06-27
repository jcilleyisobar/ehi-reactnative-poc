//
//  EHISettingsViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 6/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHISectionHeaderModel.h"

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSUInteger, EHISettingsSection) {
    EHISettingsSectionNotification,
    EHISettingsSectionSecurity,
    EHISettingsSectionPrivacy,
    EHISettingsSectionAbout,
};

@interface EHISettingsViewModel : EHIViewModel <MTRReactive>

@property (copy, nonatomic) NSString *title;
@property (copy, nonatomic) NSArray *notificationModels;
@property (copy, nonatomic) NSArray *securityModels;
@property (copy, nonatomic) NSArray *privacyModels;
@property (copy, nonatomic) NSArray *aboutModels;

- (void)selectItemAtIndexPath:(NSIndexPath *)indexPath;
- (EHISectionHeaderModel *)headerForSection:(EHISettingsSection)section;

@end

NS_ASSUME_NONNULL_END