//
//  EHIConfiguration.h
//  Enterprise
//
//  Created by Ty Cobb on 6/15/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHIPhone.h"
#import "EHIConfigurationHandler.h"

extern NSString * const EHICountriesRefreshedNotification;

NS_ASSUME_NONNULL_BEGIN

@interface EHIConfiguration : EHIModel

/** The string for the forgot password URL */
@property (copy, nonatomic, readonly, null_unspecified) NSString *forgotPasswordUrl;
/** The string for the send message URL */
@property (copy, nonatomic, readonly, null_unspecified) NSString *sendMessageUrl;
/** The string for the search answers URL */
@property (copy, nonatomic, readonly, null_unspecified) NSString *searchAnswersUrl;
/** The string for the national reservation URL */
@property (copy, nonatomic, readonly, null_unspecified) NSString *nationalReservationUrl;
/** The string for the national forgot password URL */
@property (copy, nonatomic, readonly, null_unspecified) NSString *nationalForgotPasswordUrl;
/** The string for the alamo reservation URL */
@property (copy, nonatomic, readonly, null_unspecified) NSString *alamoReservationUrl;
/** The string for the branch enrollment activation URL */
@property (copy, nonatomic, readonly, null_unspecified) NSString *activateUrl;
/** The string for the URL to print a rental receipt */
@property (copy, nonatomic, readonly, null_unspecified) NSString *printReceiptUrl;
/** The string for feedback URL */
@property (copy, nonatomic, readonly, null_unspecified) NSString *feedbackUrl;

/** The string for the country of residence */
@property (copy, nonatomic, readonly, null_unspecified) NSString *mappedCountryOfResidence;
/** The string for the mapped locale */
@property (copy, nonatomic, readonly, null_unspecified) NSString *mappedLocale;
/** The array of supported locals */
@property (copy, nonatomic, readonly, null_unspecified) NSArray *supportedLocales;

/** The list of available support phone numbers */
@property (copy, nonatomic, readonly, null_unspecified) NSArray<EHIPhone> *supportNumbers;
/** List of numbers for the customer support screen */
@property (copy, nonatomic, readonly, null_unspecified) NSArray *customerSupportNumbers;
/** The primary support phone number to call */
@property (nonatomic, readonly, null_unspecified) EHIPhone *primarySupportPhone;
/** The roadside assistance phone number to call */
@property (nonatomic, readonly, null_unspecified) EHIPhone *roadsideAssistancePhone;
/** The eplus support phone number to call */
@property (nonatomic, readonly, null_unspecified) EHIPhone *eplusPhone;
/** The phone number for DNR */
@property (nonatomic, readonly, null_unspecified) EHIPhone *dnrNumber;

/** @c YES if the configuration is populated */
@property (nonatomic, readonly) BOOL isReady;

/** Accesses the shared configuration */
+ (instancetype)configuration;

- (void)refreshCountries;

@end

@interface EHIConfiguration (Readiness)

/**
 @brief Fetches the config feed, calling back the block when it's ready
 
 This call should be made on startup to ensure the config feed is accessible when
 it's needed.
 
 If the configuration is ready at the time of calling, @c nil is returned. Otherwise
 returns an object for further customization.
 
 @param callback The block to calll when readiness changes
 
 @return A handler object for optional customization
 */

- (nullable EHIConfigurationHandler *)onReady:(EHIConfigurationCallback)callback;

@end

NS_ASSUME_NONNULL_END
