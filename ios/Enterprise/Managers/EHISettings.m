//
//  EHISettings.m
//  Enterprise
//
//  Created by Ty Cobb on 1/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHISettings.h"
#import "EHIDriverInfo.h"
#import "EHIUser.h"
#import "EHIDataStore.h"
#import "EHIAnalytics.h"
#import "EHINotificationManager.h"
#import "EHIUserLoyalty.h"

#define EHIFirstRunKey                                  @"EHIFirstRunKey"
#define EHILastRunKey                                   @"EHILastRunKey"
#define EHIAutoSaveUserInfoKey                          @"EHIAutoSaveUserInfoKey"
#define EHIAllowDataCollectionKey                       @"EHIAllowDataCollectionKey"
#define EHISaveSearchHistoryKey                         @"EHISaveSearchHistoryKey"
#define EHIRedemptionHidePointsKey                      @"EHIRedemptionHidePointsKey"
#define EHIUseRentalAssistantKey                        @"EHIUseRentalAssistantKey"
#define EHIUseTouchIdKey                                @"EHIUseTouchIdKey"
#define EHIDidPromptDashboardNewFeatureKey              @"EHIDidPromptDashboardNewFeatureKey"
#define EHIDidPromptDashboardNotificationsUnuath        @"EHIDidPromptDashboardNotificationsUnuath"
#define EHICurrentRentalReminderTimeKey                 @"EHICurrentRentalReminderTimeKey"
#define EHIUpcomingRentalReminderTimeKey                @"EHIUpcomingRentalReminderTimeKey"
#define EHIPromotionCodeKey                             @"EHIPromotionCodeKey"
#define EHIAnalyticsReminderLastDateKey                 @"EHIAnalyticsReminderLastDateKey"
#define EHIConfirmationScreenViewCounterKey             @"EHIConfirmationScreenViewCounter"
#define EHIAppStoreRateViewPresentedKey                 @"EHIAppStoreRateViewPresentedKey"
#define EHIAppStoreRateViewPresentedDayKey              @"EHIAppStoreRateViewPresentedDayKey"
#define EHIInAppReviewDebugKey                          @"EHIInAppReviewDebugKey"
#define EHIPrepayNAPaymentBannerKey                     @"EHIPrepayNAPaymentBannerKey"
#define EHISelectPreferredPaymentMethodAutomaticallyKey @"EHISelectPreferredPaymentMethodAutomaicallyKey"
#define EHISurveySkipPoolingCheckKey                    @"EHISurveySkipPoolingCheckKey"
#define EHIForceWrongApiKey                             @"EHIForceWrongApiKey"
#define EHIShowLocationsMapFilterTipKey                 @"EHIShowLocationsMapFilterTipKey"
#define EHIIssuingAuthorityRequiredKey                  @"EHIIssuingAuthorityRequiredKey"
#define EHIConfirmationShowJoinModalKey                 @"EHIConfirmationShowJoinModalKey"
#define EHIGDPRShowModalKey                             @"EHIGDPRShowModalKey"

#define EHIConfirmationViewsCountToShowRate 2
#define EHIAnalyticsReminderIntervalInMonths 12
#define EHIRentalReminderTimeTransformerName @"EHIRentalReminderTimeTransformer"

@interface EHISettings ()
@property (strong, nonatomic, readonly) id firstRunKey;
@property (strong, nonatomic, readonly) NSString *lastRunVersion;
@end

@implementation EHISettings

+ (instancetype)sharedInstance
{
    static EHISettings *sharedInstance;
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [self new];
    });
    
    return sharedInstance;
}

- (instancetype)init
{
    if(self = [super init]) {
        // unarchive the current environment
        _environment    = [EHISettingsEnvironment unarchive];
        _firstRunKey    = [self loadFirstRunKey];
        _lastRunVersion = [self loadLastRunVersion];
    }
    
    return self;
}

# pragma mark - Launch

+ (void)prepareToLaunch
{
    EHISettings *settings = [EHISettings sharedInstance];
    
    [settings registerDefaults];
    [settings promptDataTrackingIfNeeded];
}

- (void)registerDefaults
{
    EHISettings *settings = [EHISettings sharedInstance];
    
    [[NSUserDefaults standardUserDefaults] registerDefaults:@{
        EHIAutoSaveUserInfoKey                              : @([NSLocale ehi_shouldCacheDriverInfo]),
        EHIAllowDataCollectionKey                           : @(YES),
        EHISaveSearchHistoryKey                             : @(YES),
        EHIRedemptionHidePointsKey                          : @(NO),
        EHIUseRentalAssistantKey                            : @(NO),
        EHIUseTouchIdKey                                    : @(NO),
        EHIConfirmationScreenViewCounterKey                 : @(NO),
        EHIAppStoreRateViewPresentedKey                     : @(NO),
        EHIInAppReviewDebugKey                              : @(NO),
        EHIPrepayNAPaymentBannerKey                         : @(YES),
        EHISelectPreferredPaymentMethodAutomaticallyKey     : @(NO),
        EHISurveySkipPoolingCheckKey                        : @(NO),
        EHIForceWrongApiKey                                 : @(NO),
        EHIShowLocationsMapFilterTipKey                     : @(YES),
        EHIConfirmationShowJoinModalKey                     : @(YES),
        EHIGDPRShowModalKey                                 : @(YES),
        EHICurrentRentalReminderTimeKey                     : [settings.rentalReminderTimeTransformer transformedValue:@(EHIRentalReminderTimeNone)],
        EHIUpcomingRentalReminderTimeKey                    : [settings.rentalReminderTimeTransformer transformedValue:@(EHIRentalReminderTimeNone)]
    }];
}

//
// Helpers
//
     
- (void)promptDataTrackingIfNeeded
{
    // only prompt on first run
    if(self.isFirstRun && [NSLocale ehi_shouldPromptDataTrackingOnFirstRun]) {
        EHIAlertViewBuilder.new
            .message(EHILocalizedString(@"dashboard_data_tracking_notification", @"Data tracking is on, if you'd like to adjust this please visit your phone settings", @"data tracking alert shown to first time Germany users"))
            .button(EHILocalizedString(@"standard_ok_text", @"OK", @""))
            .show(nil);
    }
}

# pragma mark - First Run

- (BOOL)isFirstRun
{
    return !self.firstRunKey;
}

- (id)loadFirstRunKey
{
    // pull whatever we have out of user defaults; should be nil if first run
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    id key = [defaults objectForKey:EHIFirstRunKey];
   
    // if this is our first run, store a random uuid as the key
    if(!key) {
        [defaults setObject:[[NSUUID UUID] UUIDString] forKey:EHIFirstRunKey];
    }

    return key;
}

# pragma mark - Upgrade

- (BOOL)didUpgrade
{
    return ![self.lastRunVersion isEqualToString:[NSBundle versionShort]];
}

- (NSString *)loadLastRunVersion
{
    // retrieve last run version from user defaults
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *lastRunVersion = [defaults objectForKey:EHILastRunKey];
    
    // overwrite defaults with current run version
    [defaults setObject:[NSBundle versionShort] forKey:EHILastRunKey];
    
    return lastRunVersion;
}

# pragma mark - Setters

- (void)setAutoSaveUserInfo:(BOOL)autoSaveUserInfo
{
    [[NSUserDefaults standardUserDefaults] setBool:autoSaveUserInfo forKey:EHIAutoSaveUserInfoKey];

    // purge any stored information
    if(!autoSaveUserInfo) {
        [EHIDataStore purge:[EHIDriverInfo class] handler:nil];
    }
}

- (void)setAllowDataCollection:(BOOL)allowDataCollection
{
    [[NSUserDefaults standardUserDefaults] setBool:allowDataCollection forKey:EHIAllowDataCollectionKey];
    
    // notify analytics manager of change
    [EHIAnalytics enableDataCollection:allowDataCollection];
}

- (void)setSaveSearchHistory:(BOOL)saveSearchHistory
{
    [[NSUserDefaults standardUserDefaults] setBool:saveSearchHistory forKey:EHISaveSearchHistoryKey];
}

- (void)setRedemptionHidePoints:(BOOL)redemptionHidePoints
{
    [[NSUserDefaults standardUserDefaults] setBool:redemptionHidePoints forKey:EHIRedemptionHidePointsKey];
}

- (void)setUseRentalAssistant:(BOOL)useRentalAssistant
{
    [[NSUserDefaults standardUserDefaults] setBool:useRentalAssistant forKey:EHIUseRentalAssistantKey];
}

- (void)setUseTouchId:(BOOL)useTouchId
{
    [[NSUserDefaults standardUserDefaults] setBool:useTouchId forKey:EHIUseTouchIdKey];
    // Force synchronization
    [self synchronize];
}

- (void)setDidPromptDashboardNewFeature:(BOOL)didPromptDashboardNewFeature
{
    [[NSUserDefaults standardUserDefaults] setBool:didPromptDashboardNewFeature forKey:EHIDidPromptDashboardNewFeatureKey];
}

- (void)setDidPromptDashboardNotificationsUnuath:(BOOL)didPromptDashboardNewFeature
{
    [[NSUserDefaults standardUserDefaults] setBool:didPromptDashboardNewFeature forKey:EHIDidPromptDashboardNotificationsUnuath];
}

- (void)setSelectPreferredPaymentMethodAutomatically:(BOOL)selectPreferredPaymentMethodAutomaically
{
    [[NSUserDefaults standardUserDefaults] setBool:selectPreferredPaymentMethodAutomaically forKey:EHISelectPreferredPaymentMethodAutomaticallyKey];
}

- (void)setCurrentRentalReminderTime:(EHIRentalReminderTime)time
{
    NSString *typeString = [self.rentalReminderTimeTransformer transformedValue:@(time)];
    [[NSUserDefaults standardUserDefaults] setObject:typeString forKey:EHICurrentRentalReminderTimeKey];

    // reschedule notifications
    [[EHINotificationManager sharedInstance] scheduleRentalNotificationsForUser:[EHIUser currentUser]];
}

- (void)setUpcomingRentalReminderTime:(EHIRentalReminderTime)time
{
    NSString *typeString = [self.rentalReminderTimeTransformer transformedValue:@(time)];
    [[NSUserDefaults standardUserDefaults] setObject:typeString forKey:EHIUpcomingRentalReminderTimeKey];

    // reschedule notifications
    [[EHINotificationManager sharedInstance] scheduleRentalNotificationsForUser:[EHIUser currentUser]];
}

# pragma mark - Accessors

+ (EHISettingsEnvironment *)environment
{
    return [(EHISettings *)[self sharedInstance] environment];
}

- (BOOL)autoSaveUserInfo
{
    return [[NSUserDefaults standardUserDefaults] boolForKey:EHIAutoSaveUserInfoKey];
}

- (BOOL)allowDataCollection
{
    return [[NSUserDefaults standardUserDefaults] boolForKey:EHIAllowDataCollectionKey];
}

- (BOOL)saveSearchHistory
{
    return [[NSUserDefaults standardUserDefaults] boolForKey:EHISaveSearchHistoryKey];
}

- (BOOL)redemptionHidePoints
{
    return [[NSUserDefaults standardUserDefaults] boolForKey:EHIRedemptionHidePointsKey];
}

- (BOOL)useRentalAssistant
{
    return [[NSUserDefaults standardUserDefaults] boolForKey:EHIUseRentalAssistantKey];
}

- (BOOL)useTouchId
{
    return [[NSUserDefaults standardUserDefaults] boolForKey:EHIUseTouchIdKey];
}

- (BOOL)didPromptDashboardNewFeature
{
    return [[NSUserDefaults standardUserDefaults] boolForKey:EHIDidPromptDashboardNewFeatureKey];
}

- (BOOL)didPromptDashboardNotificationsUnuath
{
    return [[NSUserDefaults standardUserDefaults] boolForKey:EHIDidPromptDashboardNotificationsUnuath];
}

- (BOOL)selectPreferredPaymentMethodAutomatically
{
    return [[NSUserDefaults standardUserDefaults] boolForKey:EHISelectPreferredPaymentMethodAutomaticallyKey];
}

- (EHIRentalReminderTime)currentRentalReminderTime
{
    NSString *type = [[NSUserDefaults standardUserDefaults] objectForKey:EHICurrentRentalReminderTimeKey];
    
    return [[self.rentalReminderTimeTransformer reverseTransformedValue:type] unsignedIntegerValue];
}

- (EHIRentalReminderTime)upcomingRentalReminderTime
{
    NSString *type = [[NSUserDefaults standardUserDefaults] objectForKey:EHIUpcomingRentalReminderTimeKey];
    
    return [[self.rentalReminderTimeTransformer reverseTransformedValue:type] unsignedIntegerValue];
}

+ (BOOL)shouldRemindAnalytics
{
    if(![NSLocale ehi_shouldShowDataCollectionReminder]) {
        return NO;
    }
    
    NSDate *dateToRemindAnalytics = self.dateToRemindAnalytics;
    // if there's no date, then we show it
    if(!dateToRemindAnalytics) {
        return YES;
    }
    
    return [dateToRemindAnalytics ehi_isBefore:[NSDate ehi_today]];
}

+ (void)presentedAnalyticsReminder
{
    [[NSUserDefaults standardUserDefaults] setObject:[NSDate ehi_today] forKey:EHIAnalyticsReminderLastDateKey];
    [self synchronize];
}

# pragma mark - Transformers

- (NSValueTransformer *)rentalReminderTimeTransformer
{
    NSValueTransformer *transformer = [NSValueTransformer valueTransformerForName:EHIRentalReminderTimeTransformerName];
    
    // create the transformer and store it for later if it doesn't exist
    if(!transformer) {
        transformer = [[EHIMapTransformer alloc] initWithMap:@{
            @(EHIRentalReminderTimeNone)          : @"NONE",
            @(EHIRentalReminderTimeThirtyMinutes) : @"THIRTY_MINUTES",
            @(EHIRentalReminderTimeTwoHours)      : @"TWO_HOURS",
            @(EHIRentalReminderTimeOneDay)        : @"ONE_DAY",
        }];
        
        [NSValueTransformer setValueTransformer:transformer forName:EHIRentalReminderTimeTransformerName];
    }
    
    return transformer;
}

# pragma mark - Settings Display

+ (NSString *)stringForRentalReminderTime:(EHIRentalReminderTime)type
{
    switch(type) {
        case EHIRentalReminderTimeNone:
            return EHILocalizedString(@"notification_setting_option_do_not_notify", @"Do Not Notify", @"");
        case EHIRentalReminderTimeThirtyMinutes:
            return EHILocalizedString(@"notification_setting_option_thirty_minutes_before", @"30 Minutes Before", @"");
        case EHIRentalReminderTimeTwoHours:
            return EHILocalizedString(@"notification_setting_option_two_hours_before", @"2 Hours Before", @"");
        case EHIRentalReminderTimeOneDay:
            return EHILocalizedString(@"notification_setting_option_one_day_before", @"1 Day Before", @"");
        default:
            return nil;
    }
}

# pragma mark - Promotion

+ (BOOL)shouldPresentPromotion:(NSString *)promoCode
{
    return ![[[NSUserDefaults standardUserDefaults] objectForKey:EHIPromotionCodeKey] isEqualToString:promoCode];
}

+ (void)presentedPromotionWithCode:(NSString *)promoCode
{
    [[NSUserDefaults standardUserDefaults] setObject:promoCode forKey:EHIPromotionCodeKey];
}

# pragma mark - Promotion (Debug)

+ (NSString *)savedPromotionCode
{
    return [[NSUserDefaults standardUserDefaults] objectForKey:EHIPromotionCodeKey];
}

+ (void)clearPromotionCode
{
    [[NSUserDefaults standardUserDefaults] setObject:nil forKey:EHIPromotionCodeKey];
}

+ (void)clearFirstRunFlag
{
    [[NSUserDefaults standardUserDefaults] setObject:nil forKey:EHIFirstRunKey];
}

# pragma mark - Issuing Authority Required (Debug)

+ (void)setForceIssuingAuthorityRequired:(BOOL)isIssuingAuthorityRequired
{
    [[NSUserDefaults standardUserDefaults] setBool:isIssuingAuthorityRequired forKey:EHIIssuingAuthorityRequiredKey];
}

+ (BOOL)shouldForceIssuingAuthorityRequired
{
    return [[NSUserDefaults standardUserDefaults] boolForKey:EHIIssuingAuthorityRequiredKey];
}

# pragma mark - Rate

+ (BOOL)shouldPresentAppStoreRateView
{
    BOOL presented  = [[NSUserDefaults standardUserDefaults] boolForKey:EHIAppStoreRateViewPresentedKey];
    NSInteger count = [[NSUserDefaults standardUserDefaults] integerForKey:EHIConfirmationScreenViewCounterKey];
    
    return !presented && count >= EHIConfirmationViewsCountToShowRate;
}

+ (void)presentedAppStoreRate
{
    [[NSUserDefaults standardUserDefaults] setBool:YES forKey:EHIAppStoreRateViewPresentedKey];
}

+ (void)incrementConfirmationViewCount
{
    NSInteger currentCount = [[NSUserDefaults standardUserDefaults] integerForKey:EHIConfirmationScreenViewCounterKey];
    currentCount = MIN(currentCount + 1, EHIConfirmationViewsCountToShowRate);
    [[NSUserDefaults standardUserDefaults] setInteger:currentCount forKey:EHIConfirmationScreenViewCounterKey];
}

+ (void)appStoreRatePopupRequested
{
    [[NSUserDefaults standardUserDefaults] setObject:[NSDate ehi_today] forKey:EHIAppStoreRateViewPresentedDayKey];
}

+ (NSDate*)lastDayAppStoreRatePopupRequested
{
    return [[NSUserDefaults standardUserDefaults] objectForKey:EHIAppStoreRateViewPresentedDayKey];
}

# pragma mark - Prepay NA Banner

+ (BOOL)shouldShowPrepayBanner
{
    return [[NSUserDefaults standardUserDefaults] boolForKey:EHIPrepayNAPaymentBannerKey];
}

+ (void)didShowPrepayBanner
{
    [[NSUserDefaults standardUserDefaults] setBool:NO forKey:EHIPrepayNAPaymentBannerKey];
}

# pragma mark - Locations Map Filter Tip

+ (BOOL)shouldShowLocationsMapFilterTip
{
    return [[NSUserDefaults standardUserDefaults] boolForKey:EHIShowLocationsMapFilterTipKey];
}

+ (void)didShowLocationsMapFilterTip
{
    [[NSUserDefaults standardUserDefaults] setBool:NO forKey:EHIShowLocationsMapFilterTipKey];
}

# pragma mark - Confirmation Join Modal

+ (BOOL)shouldShowJoinModal
{
    return [[NSUserDefaults standardUserDefaults] boolForKey:EHIConfirmationShowJoinModalKey];
}

+ (void)didShowJoinModal
{
    [[NSUserDefaults standardUserDefaults] setBool:NO forKey:EHIConfirmationShowJoinModalKey];
}

# pragma mark - Confirmation Join Modal (Debug)

+ (void)resetShowJoinModal
{
    [[NSUserDefaults standardUserDefaults] setBool:YES forKey:EHIConfirmationShowJoinModalKey];
}

# pragma mark - Reset

+ (void)resetUserDefaults
{
    [[NSUserDefaults standardUserDefaults] removePersistentDomainForName:[[NSBundle mainBundle] bundleIdentifier]];
    [[NSUserDefaults standardUserDefaults] synchronize];
    
    [[self sharedInstance] registerDefaults];
}

# pragma mark - GDPR

+ (BOOL)shouldShowGDPRModal
{
    BOOL showModal = [[NSUserDefaults standardUserDefaults] boolForKey:EHIGDPRShowModalKey];
    BOOL isGDPR    = [NSLocale ehi_isGDPR];
    
    return showModal && isGDPR;
}

+ (void)didShowGDPRModal
{
    [[NSUserDefaults standardUserDefaults] setBool:NO forKey:EHIGDPRShowModalKey];
}

# pragma mark - Locations Map Filter Tip (Debug)

+ (void)resetLocationsMapFilterTip
{
    [[NSUserDefaults standardUserDefaults] setBool:YES forKey:EHIShowLocationsMapFilterTipKey];
}

# pragma mark - Rewards and Benefits Banner

+ (NSInteger)tierLevelForKey:(NSString *)uid
{
    return [[NSUserDefaults standardUserDefaults] integerForKey:uid];
}

+ (void)setTierLevel:(NSInteger)tier forKey:(NSString *)uid
{
    [[NSUserDefaults standardUserDefaults] setInteger:tier forKey:uid];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

# pragma mark - Prepay NA Banner (Debug)

+ (void)resetPrepayBanner
{
    [[NSUserDefaults standardUserDefaults] setBool:YES forKey:EHIPrepayNAPaymentBannerKey];
}

# pragma mark - Rewards and Benefits (Debug)

+ (void)resetTierWithLoyalty:(EHIUserLoyalty *)loyalty
{
    [self saveTier:EHIUserLoyaltyTierUnknown withUserId:loyalty.number];
}

# pragma mark - Rewards and Benefits Banner

+ (NSInteger)tierForLoyalty:(EHIUserLoyalty *)loyalty
{
    if(!loyalty || !loyalty.number) {
        return EHIUserLoyaltyTierUnknown;
    }

    NSString *savedTier = [[NSUserDefaults standardUserDefaults] objectForKey:loyalty.number];
    NSNumber *tier = [EHILoyaltyTierTypeTransform() transformedValue:savedTier];
    
    return [tier unsignedIntegerValue];
}

+ (void)saveTierOfLoyalty:(EHIUserLoyalty *)loyalty {
    if(!loyalty || !loyalty.number) {
        return;
    }

    [self saveTier:loyalty.tier withUserId:loyalty.number];
}

+ (void)saveTier:(NSInteger)tier withUserId:(NSString *)userId
{
    NSString *tierName = [EHILoyaltyTierTypeTransform() reverseTransformedValue:@(tier)];
    [[NSUserDefaults standardUserDefaults] setObject:tierName forKey:userId];
    [self synchronize];
}

# pragma mark - Survey (Debug)

- (void)setSkipSurveyPoolingCheck:(BOOL)skipSurveyPoolingCheck
{
    [[NSUserDefaults standardUserDefaults] setBool:skipSurveyPoolingCheck forKey:EHISurveySkipPoolingCheckKey];
}

- (BOOL)skipSurveyPoolingCheck
{
    return [[NSUserDefaults standardUserDefaults] boolForKey:EHISurveySkipPoolingCheckKey];
}

# pragma mark - API Key

- (void)setForceWrongApiKey:(BOOL)forceWrongApiKey
{
    [[NSUserDefaults standardUserDefaults] setBool:forceWrongApiKey forKey:EHIForceWrongApiKey];
}

- (BOOL)forceWrongApiKey
{
    return [[NSUserDefaults standardUserDefaults] boolForKey:EHIForceWrongApiKey];
}

# pragma mark - Rate (Debug)

+ (NSInteger)confirmationScreenCount
{
    return [[NSUserDefaults standardUserDefaults] integerForKey:EHIConfirmationScreenViewCounterKey];
}

+ (void)resetConfirmationViewCount
{
    [[NSUserDefaults standardUserDefaults] setInteger:0 forKey:EHIConfirmationScreenViewCounterKey];
}

+ (void)resetPresentedAppStoreRate
{
    [[NSUserDefaults standardUserDefaults] setBool:NO forKey:EHIAppStoreRateViewPresentedKey];
}

+ (BOOL)didPresentedAppStoreRate
{
    return [[NSUserDefaults standardUserDefaults] boolForKey:EHIAppStoreRateViewPresentedKey];
}

+ (void)toggleDebugingInAppReview
{
    BOOL isDebuging = [self isDebugingInAppReview];
    [[NSUserDefaults standardUserDefaults] setBool:!isDebuging forKey:EHIInAppReviewDebugKey];
}

+ (BOOL)isDebugingInAppReview
{
    return [[NSUserDefaults standardUserDefaults] boolForKey:EHIInAppReviewDebugKey];
}

# pragma mark - Analytics (Debug)

+ (NSDate *)dateToRemindAnalytics
{
    NSDate *lastRemindDate = [[NSUserDefaults standardUserDefaults] objectForKey:EHIAnalyticsReminderLastDateKey];
    return [lastRemindDate ehi_addMonths:EHIAnalyticsReminderIntervalInMonths];
}

+ (void)makeAnalyticsBePresented
{
    NSDate *todayFromNextDate  = [[NSDate ehi_today] ehi_addMonths:-EHIAnalyticsReminderIntervalInMonths];
    NSDate *yesterday = [todayFromNextDate ehi_addDays:-1];
    [[NSUserDefaults standardUserDefaults] setObject:yesterday forKey:EHIAnalyticsReminderLastDateKey];
}

# pragma mark - EHIAnalyticsEncodable

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    context[EHIAnalyticsUserSettingsNotificationPickupKey]  = [self analyticsStringForRentalReminderTime:self.upcomingRentalReminderTime];
    context[EHIAnalyticsUserSettingsNotificationDropoffKey] = [self analyticsStringForRentalReminderTime:self.currentRentalReminderTime];
}

# pragma mark - Analytics Helpers

- (NSString *)analyticsStringForRentalReminderTime:(EHIRentalReminderTime)type
{
    switch(type) {
        case EHIRentalReminderTimeNone:
            return nil;
        case EHIRentalReminderTimeThirtyMinutes:
            return @"30minutes";
        case EHIRentalReminderTimeTwoHours:
            return @"2hours";
        case EHIRentalReminderTimeOneDay:
            return @"1day";
        default:
            return nil;
    }
}

//
// Helpers
//

- (void)synchronize
{
    [self.class synchronize];
}

+ (void)synchronize
{
    [[NSUserDefaults standardUserDefaults] synchronize];
}

@end
