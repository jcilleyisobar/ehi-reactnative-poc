//
//  EHISettingsAboutViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 6/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSUInteger, EHISettingsAboutRow) {
    EHISettingsAboutRowVersion,
    EHISettingsAboutRowPrivacyPolicy,
    EHISettingsAboutRowTermsOfUse,
    EHISettingsAboutRowTermsAndConditions,
    EHISettingsAboutRowLicenses
};

@interface EHISettingsAboutViewModel : EHIViewModel <MTRReactive>

@property (assign, nonatomic) EHISettingsAboutRow row;
@property (copy  , nonatomic) NSString *title;
@property (copy  , nonatomic) NSString *detailTitle;

// computed
@property (assign, nonatomic, readonly) BOOL showsArrow;

+ (NSArray *)viewModels;

@end

NS_ASSUME_NONNULL_END