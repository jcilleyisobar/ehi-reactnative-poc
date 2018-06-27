//
//  EHIUser.h
//  Enterprise
//
//  Created by mplace on 2/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHIUserProfiles.h"
#import "EHIUserContactProfile.h"
#import "EHIAddress.h"
#import "EHIUserLicenseProfile.h"
#import "EHIUserPaymentProfile.h"
#import "EHIUserPreferencesProfile.h"
#import "EHIEnrollTerms.h"
#import "EHIUserRentals.h"
#import "EHIAnalyticsEncodable.h"
#import "EHIUserAdditionalData.h"

#ifdef DEBUG
#define EHIUserAutofillCredentials 1
#define EHIUserMockLogin (EHIMockEnabled && 1)
#define EHIUserMockRentals (EHIMockEnabled && 1)
#define EHIUserMockProfileOptions (EHIMockEnabled && 1)
#else
#define EHIUserAutofillCredentials 0
#define EHIUserMockLogin (EHIMockEnabled && 0)
#define EHIUserMockRentals 0
#define EHIUserMockProfileOptions 0
#endif

@interface EHIUser : EHIModel <EHIAnalyticsEncodable, EHINetworkEncodable>

@property (strong, nonatomic, readonly) EHIUserProfiles *profiles;
@property (strong, nonatomic, readonly) EHIUserAdditionalData *additionalData;
@property (strong, nonatomic, readonly) EHIUserContactProfile *contact;
@property (strong, nonatomic, readonly) EHIAddress *address;
@property (strong, nonatomic, readonly) EHIUserLicenseProfile *license;
@property (strong, nonatomic, readonly) EHIUserPaymentProfile *payment;
@property (strong, nonatomic, readonly) EHIUserPreferencesProfile *preference;
@property (strong, nonatomic, readonly) EHIEnrollTerms *terms;
@property (copy  , nonatomic, readonly) NSString *individualId;
@property (copy  , nonatomic, readonly) NSString *authorizationToken;
@property (copy  , nonatomic, readonly) NSString *encryptedCredentials;
@property (assign, nonatomic, readonly) NSInteger points;
@property (assign, nonatomic, readonly) BOOL shouldPromptForEmail;
@property (assign, nonatomic, readonly) BOOL shouldPromptForLogin;


// my-rentals caching
@property (strong, nonatomic) EHIUserRentals *pastRentals;
@property (strong, nonatomic) EHIUserRentals *currentRentals;
@property (strong, nonatomic) EHIUserRentals *upcomingRentals;

+ (EHIUser *)currentUser;

- (void)attachPaymentMethod:(EHIUserPaymentMethod *)paymentMethod;
- (void)updateAddress:(EHIAddress *)address;
- (void)updateContact:(EHIUserContactProfile *)contact;
- (void)updateSpecialOffersOptIn:(EHIOptionalBoolean)optIn;

@end

@interface EHIUser (Accessors)

/** First and last name separated by a space */
@property (copy, nonatomic, readonly) NSString *displayName;
/** First name of the user */
@property (copy, nonatomic, readonly) NSString *firstName;
/** Last name of the user */
@property (copy, nonatomic, readonly) NSString *lastName;
/** Username of the user */
@property (copy, nonatomic, readonly) NSString *username;
/** The loyalty number on the user's account */
@property (copy, nonatomic, readonly) NSString *loyaltyNumber;
/** Loyalty points to date of the user (formatted for current locale) */
@property (copy, nonatomic, readonly) NSString *loyaltyPoints;
/** Checks if user is on Do Not Rent list */
@property (assign, nonatomic, readonly) BOOL isOnDnrList;

/** All locations for current and upcoming rentals */
@property (readonly, nonatomic) NSArray *rentalLocations;

/** Accounts of type billing (may be empty) */
@property (copy, nonatomic, readonly) NSArray *billingAccounts;
/** Accounts of type payment (may be empty) */
@property (copy, nonatomic, readonly) NSArray *paymentAccounts;
@property (copy, nonatomic, readonly) NSArray<EHIUserPaymentMethod> *paymentMethods;
/** Preferred credit card (may be nil) */
@property (readonly, nonatomic) EHIUserPaymentMethod *preferredPaymentAccount;
/** Preferred billing account (may be nil) */
@property (readonly, nonatomic) EHIUserPaymentMethod *preferredBillingAccount;
/** Corporate contract for the user */
@property (readonly, nonatomic) EHIContractDetails *corporateContract;
/** Filter out just payment methods that uses a credit card */
@property (readonly, nonatomic) NSArray<EHIUserPaymentMethod> *creditCardPaymentMethods;

@end
