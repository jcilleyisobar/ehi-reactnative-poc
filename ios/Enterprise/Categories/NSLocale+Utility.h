//
//  NSLocale+Utility.h
//  Enterprise
//
//  Created by Ty Cobb on 2/10/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@import Foundation;

@interface NSLocale (Utility)

/** Returns the current locale's identifier */
+ (NSString *)ehi_identifier;
/** Returns the current locale's region */
+ (NSString *)ehi_region;
/** Returns the current locale's language */
+ (NSString *)ehi_language;

/** Returns whether user's language is EN */
+ (BOOL)ehi_isEnLanguage;

/** Returns @YES if feedback menu should be hidden */
+ (BOOL)ehi_hideFeedbackMenu;
/** Returns @YES if driver info should be cached */
+ (BOOL)ehi_shouldCacheDriverInfo;
/** Returns @YES if data tracking should be prompted */
+ (BOOL)ehi_shouldPromptDataTrackingOnFirstRun;
/** Returns @YES if data collection reminder should be shown */
+ (BOOL)ehi_shouldShowDataCollectionReminder;
/** Returns @YES if email notifications should be checked by default on driver info */
+ (BOOL)ehi_shouldCheckEmailNotificationsByDefault;
/** Returns @YES if remember me on sign in should be checked by default */
+ (BOOL)ehi_shouldCheckRememberMeByDefault;
/** Returns @YES if message about double opt-in for email specials should be shown */
+ (BOOL)ehi_shouldShowDoubleOptInForEmailSpecials;
/** Returns @YES if should show 'Select Payment' screen */
+ (BOOL)ehi_shouldAllowSelectPayment;
/** Returns @YES if add/remove/edit credit card is permited */
+ (BOOL)ehi_shouldAllowProfilePaymentEdit;
/** Returns @YES if should show "Introducing more ways of payment" in the payment screen */
+ (BOOL)ehi_shouldShowPrepayBanner;
/** Returns @YES if pay later prices are preferred over prepay */
+ (BOOL)ehi_shouldDefaultToPayLater;
/** Returns @YES if reservation should submit for 3DS verification */
+ (BOOL)ehi_shouldCommitReservationWith3dsCheck;
/** Returns @YES if cancel flow should utilize EHICancellationFee */
+ (BOOL)ehi_shouldUseCancellationFees;
/** Returns @YES if quick booking is allowed */
+ (BOOL)ehi_shouldAllowQuickBookReservation;
/** Returns @YES if should skip modify modal */
+ (BOOL)ehi_shouldSkipModifyReservationModal;
/** Returns @YES locale is NA or CA */
+ (BOOL)ehi_isUSAOrCanada;
/** Returns the current locale's welcome image path */
+ (NSString *)ehi_welcomeImagePath;
/** Returns @YES if should show Foresee servey prompt */
+ (BOOL)ehi_shouldShowSurveyPrompt;
/** Returns @YES if current locale has GDPR regulation */
+ (BOOL)ehi_isGDPR;

@end
