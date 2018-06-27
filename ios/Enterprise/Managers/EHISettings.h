//
//  EHISettings.h
//  Enterprise
//
//  Created by Ty Cobb on 1/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHISettingsEnvironment.h"
#import "EHIServices+Config.h"
#import "EHIAnalyticsUpdater.h"

typedef NS_ENUM(NSUInteger, EHIRentalReminderTime) {
    EHIRentalReminderTimeNone,
    EHIRentalReminderTimeThirtyMinutes,
    EHIRentalReminderTimeTwoHours,
    EHIRentalReminderTimeOneDay,
    EHIRentalReminderTimeCount,
};

typedef NS_ENUM(NSUInteger, EHICookieRegionBypass) {
    EHICookieRegionBypassClear,
    EHICookieRegionBypassEast,
    EHICookieRegionBypassWest
};


@class EHIUserLoyalty;
@interface EHISettings : NSObject <EHIAnalyticsUpdater>

/** The current environment. Contains the services base path as well as other related info. */
@property (nonatomic, readonly) EHISettingsEnvironment *environment;
/** @c YES if it's first run of the app */
@property (assign, nonatomic, readonly) BOOL isFirstRun;
/** @c YES if the user just upgraded the app */
@property (assign, nonatomic, readonly) BOOL didUpgrade;
/** Whether data pertaining to the user should be saved */
@property (assign, nonatomic) BOOL autoSaveUserInfo;
/** If analytics information should be collected */
@property (assign, nonatomic) BOOL allowDataCollection;
/** If user search and reservation history should be saved */
@property (assign, nonatomic) BOOL saveSearchHistory;
/** @c YES if points should default to hidden during an applicable res flow */
@property (assign, nonatomic) BOOL redemptionHidePoints;
/** @c YES if Enterprise Rental Assistant using geofencing is active */
@property (assign, nonatomic) BOOL useRentalAssistant;
/** @c YES if the user opts in to using touch id for various secure features */
@property (assign, nonatomic) BOOL useTouchId;
/** @c YES if the user wants to use the preferred payment method on the profile, as a default payment method */
@property (assign, nonatomic) BOOL selectPreferredPaymentMethodAutomatically;
/** @c YES if should skip survey pooling check (for debug) */
@property (assign, nonatomic) BOOL skipSurveyPoolingCheck;
/** @c YES if we should force wrong api key */
@property (assign, nonatomic) BOOL forceWrongApiKey;
/** Notification type before end of current rental to notify **/
@property (assign, nonatomic) EHIRentalReminderTime currentRentalReminderTime;
/** Notification type before start of upcoming rental to notify **/
@property (assign, nonatomic) EHIRentalReminderTime upcomingRentalReminderTime;

/** Singleton-accessor */
+ (instancetype)sharedInstance;
/** Perform any initial setup (like initializing defaults) */
+ (void)prepareToLaunch;
/** Convenience accessor for the environment on the shared instance */
+ (EHISettingsEnvironment *)environment;

/** Friendly string for the given notification type */
+ (NSString *)stringForRentalReminderTime:(EHIRentalReminderTime)time;
/** @c YES if should present educational modal for a promotion */
+ (BOOL)shouldPresentPromotion:(NSString *)promoCode;
+ (void)presentedPromotionWithCode:(NSString *)promoCode;
/** @c YES if should present analytics reminder modal to the user **/
+ (BOOL)shouldRemindAnalytics;
/** Flag that the analytics reminder modal was presented **/
+ (void)presentedAnalyticsReminder;

+ (BOOL)shouldPresentAppStoreRateView;
+ (void)presentedAppStoreRate;
+ (void)incrementConfirmationViewCount;

+ (BOOL)shouldShowPrepayBanner;
+ (void)didShowPrepayBanner;

+ (BOOL)shouldShowLocationsMapFilterTip;
+ (void)didShowLocationsMapFilterTip;

+ (BOOL)shouldShowJoinModal;
+ (void)didShowJoinModal;

+ (BOOL)shouldShowGDPRModal;
+ (void)didShowGDPRModal;

+ (BOOL)shouldPromptForNotifications;
+ (void)didPromptForNotifications;

+ (BOOL)shouldPromptForLocationInUpcomingRental;
+ (void)didPromptForLocationInUpcomingRental;

/** Returns loyalty tier level for user */
+ (NSInteger)tierForLoyalty:(EHIUserLoyalty *)loyalty;
/** Save the tier level of a user loyalty profile */
+ (void)saveTierOfLoyalty:(EHIUserLoyalty *)loyalty;

/** For debug view */
+ (NSString *)savedPromotionCode;
+ (void)clearPromotionCode;
+ (void)clearFirstRunFlag;
+ (NSDate *)dateToRemindAnalytics;
+ (void)makeAnalyticsBePresented;
+ (NSInteger)confirmationScreenCount;
+ (void)resetConfirmationViewCount;
+ (void)resetPresentedAppStoreRate;
+ (BOOL)didPresentedAppStoreRate;
+ (void)appStoreRatePopupRequested;
+ (NSDate*)lastDayAppStoreRatePopupRequested;
+ (void)toggleDebugingInAppReview;
+ (BOOL)isDebugingInAppReview;
+ (void)resetPrepayBanner;
+ (void)resetTierWithLoyalty:(EHIUserLoyalty *)loyalty;
+ (void)resetLocationsMapFilterTip;
+ (void)setForceIssuingAuthorityRequired:(BOOL)isIssuingAuthorityRequired;
+ (BOOL)shouldForceIssuingAuthorityRequired;
+ (void)resetShowJoinModal;

+ (EHICookieRegionBypass)currentCookieBypass;
+ (void)setCurrentCookieBypass:(EHICookieRegionBypass)currentCookieBypass;

+ (void)resetUserDefaults;

@end

NS_INLINE NSValueTransformer * EHICookieRegionBypassTransformer()
{
    EHIMapTransformer *transformer = [[EHIMapTransformer alloc] initWithMap:@{
        @"east"  : @(EHICookieRegionBypassEast),
        @"west"  : @(EHICookieRegionBypassWest)
    }];
    
    // default to unknown
    transformer.defaultValue = @(EHICookieRegionBypassClear);
    
    return transformer;
}
