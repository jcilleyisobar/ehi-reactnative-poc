//
//  NSLocale+Utility.m
//  Enterprise
//
//  Created by Ty Cobb on 2/10/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@implementation NSLocale (Utility)

+ (NSString *)ehi_identifier
{
    return [[self autoupdatingCurrentLocale] localeIdentifier];
}

+ (NSString *)ehi_region
{
    return [[self autoupdatingCurrentLocale] objectForKey:NSLocaleCountryCode];
}

+ (NSString *)ehi_language
{
    // Check for preferred lanaguage first
    return [[self autoupdatingCurrentLocale] objectForKey:NSLocaleLanguageCode];
}

+ (BOOL)ehi_isEnLanguage
{
    return [[self ehi_language] isEqualToString:@"en"];
}


# pragma mark - Country specific settings

+ (BOOL)ehi_hideFeedbackMenu
{
    BOOL countryShouldShowFeedback = [self ehi_country].isUS || [self ehi_country].isCanada;
    return !countryShouldShowFeedback || ![NSLocale ehi_isEnLanguage];
}

+ (BOOL)ehi_shouldCacheDriverInfo
{
    // we can't use `ehi_country` here because this is needed before we call the countries endpoint
    return [[self ehi_region] isEqualToString:EHICountryCodeUS];
}

+ (BOOL)ehi_shouldPromptDataTrackingOnFirstRun
{
    // we can't use `ehi_country` here because this is needed before we call the countries endpoint
    return [[self ehi_region] isEqualToString:EHICountryCodeGermany];
}

+ (BOOL)ehi_shouldShowDataCollectionReminder
{
    return [self ehi_country].isFrance;
}

+ (BOOL)ehi_shouldCheckEmailNotificationsByDefault
{
    return [self ehi_country].defaultEmailOptIn;
}

+ (BOOL)ehi_shouldCheckRememberMeByDefault
{
    return [self ehi_country].isUS;
}

+ (BOOL)ehi_shouldShowDoubleOptInForEmailSpecials
{
// hide it for now
//    return [self ehi_country].isGermany;
    return NO;
}

+ (BOOL)ehi_shouldShowPrepayBanner
{
    return self.ehi_isUSAOrCanada;
}

+ (BOOL)ehi_shouldAllowSelectPayment
{
    return self.ehi_isUSAOrCanada;
}

+ (BOOL)ehi_shouldAllowProfilePaymentEdit
{
    return self.ehi_isUSAOrCanada;
}

+ (BOOL)ehi_shouldDefaultToPayLater
{
    return self.ehi_isUSAOrCanada;
}

+ (BOOL)ehi_shouldCommitReservationWith3dsCheck
{
    return !self.ehi_isUSAOrCanada;
}

+ (BOOL)ehi_shouldUseCancellationFees
{
    return self.ehi_isUSAOrCanada;
}

+ (BOOL)ehi_shouldAllowQuickBookReservation
{
    return self.ehi_isUSAOrCanada;
}

+ (BOOL)ehi_shouldSkipModifyReservationModal
{
    return self.ehi_isUSAOrCanada;
}

+ (NSString *)ehi_welcomeImagePath
{
    // we can't use `ehi_country` here because this is needed before we call the countries endpoint
    if ([[self ehi_region] isEqualToString:EHICountryCodeUS] || [[self ehi_region] isEqualToString:EHICountryCodeCanada]) {
        return @"welcomeImage_NA";
    }
    
    return @"welcomeImage_rest";
}

+ (BOOL)ehi_shouldShowSurveyPrompt
{
    return [self ehi_country].isUS && self.ehi_isEnLanguage;
}

+ (BOOL)ehi_isGDPR
{
    return !self.ehi_isUSAOrCanada;
}

//
// Helpers
//

+ (BOOL)ehi_isUSAOrCanada
{
    return [self ehi_country].isUS || [self ehi_country].isCanada;
}


@end
