//
//  EHIDebugOptionViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 11/24/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIDebugOptionViewModel.h"
#import "EHILocalization.h"
#import "EHISettings.h"
#import "EHISettingsEnvironment.h"
#import "EHIUser.h"
#import "EHIUserCredentials.h"
#import "EHISearchEnvironment.h"
#import "EHINotificationManager+Private.h"

@interface EHIDebugOptionViewModel ()
@property (assign, nonatomic) EHIDebugOptionType type;
@property (copy  , nonatomic) NSString *title;
@property (copy  , nonatomic) NSString *subtitle;
@property (copy  , nonatomic) NSArray *options;
@end

@implementation EHIDebugOptionViewModel

# pragma mark - Generator

+ (NSArray *)viewModels
{
    EHIDebugOptionViewModel *stringBehavior = [EHIDebugOptionViewModel new];
    stringBehavior.type     = EHIDebugOptionTypeStringBehavior;
    stringBehavior.title    = @"String Behavior";
    stringBehavior.subtitle = [EHILocalization nameForStringBehavior:[EHILocalization stringBehavior]];
    
    EHIDebugOptionViewModel *environment = [EHIDebugOptionViewModel new];
    environment.type     = EHIDebugOptionTypeEnvironment;
    environment.title    = @"Environment";
    environment.subtitle = [EHISettings environment].displayName;

    EHIDebugOptionViewModel *searchEnvironment = [EHIDebugOptionViewModel new];
    searchEnvironment.type     = EHIDebugOptionTypeSearchEnvironment;
    searchEnvironment.title    = @"Search Environment";
    searchEnvironment.subtitle = [EHISearchEnvironment unarchive].displayName;

    EHIDebugOptionViewModel *invalidateAuthToken = [EHIDebugOptionViewModel new];
    invalidateAuthToken.type     = EHIDebugOptionTypeInvalidateAuthToken;
    invalidateAuthToken.title    = @"Invalidate Authentication Token";
    invalidateAuthToken.subtitle = [self constructAuthTokenSubtitle];
    
    EHIDebugOptionViewModel *wrongApiKey = [EHIDebugOptionViewModel new];
    wrongApiKey.type     = EHIDebugOptionTypeWrongApiKey;
    wrongApiKey.title    = @"Force wrong API key";
    wrongApiKey.subtitle = [self constructWrongApiKeySubtitle];
    
    EHIDebugOptionViewModel *map = [EHIDebugOptionViewModel new];
    map.type     = EHIDebugOptionTypeMap;
    map.title    = @"Geofence Map";
    map.subtitle = @"All currently observed geofences";
    
    EHIDebugOptionViewModel *officeGeofence = [EHIDebugOptionViewModel new];
    officeGeofence.type     = EHIDebugOptionTypeOfficeGeofence;
    officeGeofence.title    = @"Office Geofence";
    officeGeofence.subtitle = [self constructOfficeGeofenceSubtitle];
    
    EHIDebugOptionViewModel *notifications = [EHIDebugOptionViewModel new];
    notifications.type     = EHIDebugOptionTypeNotifications;
    notifications.title    = @"Notifications";
    notifications.subtitle = @"Fire a notification with user's current or upcoming rental";
    
    EHIDebugOptionViewModel *weekendSpecial = [EHIDebugOptionViewModel new];
    weekendSpecial.type     = EHIDebugOptionTypeWeekendSpecial;
    weekendSpecial.title    = @"Weekend Special";
    weekendSpecial.subtitle = [self weekendSpecialSubtitle];
    
    EHIDebugOptionViewModel *analyticsReminder = [EHIDebugOptionViewModel new];
    analyticsReminder.type     = EHIDebugOptionTypeAnalyticsReminder;
    analyticsReminder.title    = @"Analytics Reminder";
    analyticsReminder.subtitle = [self analyticsSubtitle];

    BOOL canMakeInAppReview = NSClassFromString(@"SKStoreReviewController") != nil;
    EHIDebugOptionViewModel *appleStoreRate = [EHIDebugOptionViewModel new];
    appleStoreRate.type     = canMakeInAppReview ? EHIDebugOptionTypeInAppAppleStoreRate : EHIDebugOptionTypeAppleStoreRate;
    appleStoreRate.title    = @"Apple Store Review";
    appleStoreRate.subtitle = canMakeInAppReview ? [self inAppRateSubtitle] : [self appleRateSubtitle];
    
    EHIDebugOptionViewModel *prepayNABanner = [EHIDebugOptionViewModel new];
    prepayNABanner.type     = EHIDebugOptionTypePrepayNABanner;
    prepayNABanner.title    = @"Prepay NA Banner";
    prepayNABanner.subtitle = [self prepayNASubtitle];
    
    EHIDebugOptionViewModel *surveyPooling = [EHIDebugOptionViewModel new];
    surveyPooling.type     = EHIDebugOptionTypeSurveyPooling;
    surveyPooling.title    = @"Survey Pooling";
    surveyPooling.subtitle = [self surveyPoolingState];

    EHIDebugOptionViewModel *surveyReset = [EHIDebugOptionViewModel new];
    surveyReset.type     = EHIDebugOptionTypeSurveyResetState;
    surveyReset.title    = @"Survey State";
    surveyReset.subtitle = @"Reset survey state";
    
    EHIDebugOptionViewModel *rewards = [EHIDebugOptionViewModel new];
    rewards.type     = EHIDebugOptionTypeRewardsBenefitsState;
    rewards.title    = @"Rewards and Benefits Header State";
    rewards.subtitle = [self rewardsBenefitsSubtitle];
    
    EHIDebugOptionViewModel *filterTip = [EHIDebugOptionViewModel new];
    filterTip.type     = EHIDebugOptionTypeLocationMapFilterTip;
    filterTip.title    = @"Location Map Filter Tip";
    filterTip.subtitle = [self filterTipSubtitle];

    EHIDebugOptionViewModel *pushNotificationEvent = [EHIDebugOptionViewModel new];
    pushNotificationEvent.type     = EHIDebugOptionTypePushNotificationEvent;
    pushNotificationEvent.title    = @"Send push notification Event";
    
    EHIDebugOptionViewModel *issuingAuthorityMock = [EHIDebugOptionViewModel new];
    issuingAuthorityMock.type     = EHIDebugOptionTypeIssuingAuthorityRequiredMock;
    issuingAuthorityMock.title    = @"Force Issuing Authority Required";
    issuingAuthorityMock.subtitle = [self issuingAuthoritySubtitle];
    
    EHIDebugOptionViewModel *unauthJoinModal = [EHIDebugOptionViewModel new];
    unauthJoinModal.type     = EHIDebugOptionTypeUnauthJoinModal;
    unauthJoinModal.title    = @"Unauthenticated Confirmation Join Modal";
    unauthJoinModal.subtitle = [self unauthJoinModalSubtitle];
    
    EHIDebugOptionViewModel *gdprState = [EHIDebugOptionViewModel new];
    gdprState.type     = EHIDebugOptionTypeGDPRState;
    gdprState.title    = @"GDPR (SDK Opt Out Statuses)";
    
    EHIDebugOptionViewModel *clearData = [EHIDebugOptionViewModel new];
    clearData.type     = EHIDebugOptionTypeClearData;
    clearData.title    = @"\u2620 Clear Data \u2620";
    clearData.subtitle = [self clearDataSubtitle];

    return @[stringBehavior, environment, searchEnvironment, invalidateAuthToken, wrongApiKey, map, officeGeofence, notifications, weekendSpecial, analyticsReminder, appleStoreRate, prepayNABanner, surveyPooling, surveyReset, rewards, filterTip, pushNotificationEvent, issuingAuthorityMock, unauthJoinModal, gdprState, clearData];
}

//
// Helpers
//

+ (NSString *)constructAuthTokenSubtitle
{
    NSString *currentAuthToken = [[EHIUser currentUser] authorizationToken];
    BOOL invalidToken = [currentAuthToken isEqualToString:EHIDebugOptionInvalidAuthToken];
    
    return invalidToken ? @"INVALID TOKEN" : @"TOKEN IS VALID";
}

+ (NSString *)constructWrongApiKeySubtitle
{
    BOOL forcing = EHISettings.sharedInstance.forceWrongApiKey;
    
    return forcing ? @"Sending wrong api key will trigger the Update alert all over the app." : nil;
}

+ (NSString *)constructOfficeGeofenceSubtitle
{
    BOOL hasOfficeGeofence = ([EHINotificationManager sharedInstance].pendingNotifications ?: @[]).any(^(UNNotificationRequest *request) {
        return request.content.userInfo[EHIDebugOptionOfficeGeofenceNotificationKey] != nil;
    });

    NSString *onString = hasOfficeGeofence ? @"ON" : @"OFF";
    
    return [NSString stringWithFormat:@"%@: Places geofence around Chicago office for distance testing", onString];
}

+ (NSString *)weekendSpecialSubtitle
{
    NSString *promoCode = [EHISettings savedPromotionCode];
    return [NSString stringWithFormat:@"Invalidate promotion %@", promoCode];
}

+ (NSString *)analyticsSubtitle
{
    NSString *nextDate = [[EHISettings dateToRemindAnalytics] ehi_localizedShortDateString];
    return [NSString stringWithFormat:@"Analytics reminder will be presented in %@", nextDate];
}

+ (NSString *)appleRateSubtitle
{
    NSInteger count = [EHISettings confirmationScreenCount];
    BOOL presented  = [EHISettings didPresentedAppStoreRate];
    
    NSString *onString = presented ? @"ON" : @"OFF";

    return [NSString stringWithFormat:@"Confirmation screen viewed: %lu time(s)\nApple Store rate presented: %@", (long)count, onString];
}

+ (NSString *)inAppRateSubtitle
{
    NSInteger count = [EHISettings confirmationScreenCount];
    BOOL presented  = [EHISettings isDebugingInAppReview];
    
    NSString *onString = presented ? @"ON" : @"OFF";
    
    return [NSString stringWithFormat:@"Confirmation screen viewed: %lu time(s)\nIn app review is: %@", (long)count, onString];
}

+ (NSString *)prepayNASubtitle
{
    BOOL shouldShowBanner = [EHISettings shouldShowPrepayBanner];
    
    NSString *showString = shouldShowBanner ? @"show" : @"hide";
    
    return [NSString stringWithFormat:@"Prepay NA Bannner should %@", showString];
}

+ (NSString *)surveyPoolingState
{
    BOOL isSkiping = [EHISettings sharedInstance].skipSurveyPoolingCheck;
    
    NSString *onString = isSkiping ? @"ON" : @"OFF";
    
    return [NSString stringWithFormat:@"Survey skip pooling check is %@", onString];
}

+ (NSString *)rewardsBenefitsSubtitle
{
    EHIUserLoyalty *loyalty = [EHIUser currentUser].profiles.basic.loyalty;
    NSInteger tier = [EHISettings tierForLoyalty:loyalty];
    NSString *savedTier = [EHILoyaltyTierTypeTransform() reverseTransformedValue:@(tier)];
    
    return [NSString stringWithFormat:@"%@ is saved.", tier == EHIUserLoyaltyTierUnknown ? @"No tier" : savedTier];
}

+ (NSString *)filterTipSubtitle
{
    BOOL willShowFilterTip = [EHISettings shouldShowLocationsMapFilterTip];
    
    return [NSString stringWithFormat:@"Will%@show filter tip on the locations map screen", willShowFilterTip ? @" " : @" not "];
}

+ (NSString *)issuingAuthoritySubtitle
{
    BOOL shouldForceIssuingAuthority = [EHISettings shouldForceIssuingAuthorityRequired];
    
    return [NSString stringWithFormat:@"Force Issuing Authority is %@", shouldForceIssuingAuthority ? @"ON" : @"OFF"];
}

+ (NSString *)unauthJoinModalSubtitle
{
    NSString *status = [NSString string];
    if(EHIUser.currentUser != nil) {
        status = @"not show because you are logged in.";
    } else {
        BOOL shouldShowModal = [EHISettings shouldShowJoinModal];

        status = [NSString stringWithFormat:@"%@ show", shouldShowModal ? @"" : @"not"];
    }
    
    return [NSString stringWithFormat:@"Join Modal will %@", status];
}

+ (NSString *)clearDataSubtitle
{
    return [NSString stringWithFormat:@"This will reset all flags"];
}

@end
