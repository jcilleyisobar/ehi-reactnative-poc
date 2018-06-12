//
//  EHIUser.m
//  Enterprise
//
//  Created by mplace on 2/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIUser.h"
#import "EHIUserManager.h"
#import "EHIUserAdditionalData.h"

@interface EHIUser ()
@property (strong, nonatomic) EHIUserPaymentMethod *attachedPaymentMethod;
@property (strong, nonatomic) EHIAddress *address;
@property (strong, nonatomic) EHIUserContactProfile *contact;
@property (strong, nonatomic) EHIUserPreferencesProfile *preference;
@end

@implementation EHIUser

- (id)uid
{
    return @"user";
}

# pragma mark - Accessors

+ (EHIUser *)currentUser
{
    return [EHIUserManager sharedInstance].currentUser;
}

- (NSString *)loyaltyNumber
{
    return self.profiles.basic.loyalty.number;
}

- (NSString *)loyaltyPoints
{
    return [@(self.profiles.basic.loyalty.pointsToDate) ehi_localizedDecimalString];
}

- (NSString *)displayName
{
    return [NSString stringWithFormat:@"%@ %@", self.firstName, self.lastName];
}

- (NSString *)username
{
    return self.profiles.basic.username;
}

- (NSString *)firstName
{
    return self.profiles.basic.firstName;
}

- (NSString *)lastName
{
    return self.profiles.basic.lastName;
}

- (BOOL)isOnDnrList
{
    return self.license.isOnDnrList;
}

- (NSArray*)paymentMethods
{
    return self.payment.paymentMethods;
}

- (NSArray *)rentalLocations
{
    return @[].concat(self.currentRentals.all).concat(self.upcomingRentals.all).inject(@[], ^(NSMutableArray *memo, EHIUserRental *rental) {
        EHILocation *pickupLocation = rental.pickupLocation.uid ? rental.pickupLocation : nil;
        EHILocation *returnLocation = rental.returnLocation.uid ? rental.returnLocation : nil;
        
        [memo ehi_safelyAppend:pickupLocation];
        [memo ehi_safelyAppend:returnLocation];
        
        return memo;
    });
}

- (NSArray *)billingAccounts
{
    EHIUserPaymentProfile *paymentProfile = self.payment;
    
    return (paymentProfile.paymentMethods ?: @[]).select(^(EHIUserPaymentMethod *account) {
        return account.paymentType == EHIUserPaymentTypeBilling;
    });
}

- (NSArray *)paymentAccounts
{
    EHIUserPaymentProfile *paymentProfile = self.payment;
    
    return (paymentProfile.paymentMethods ?: @[]).select(^(EHIUserPaymentMethod *account) {
        return account.paymentType == EHIUserPaymentTypeCard;
    });
}

- (EHIUserPaymentMethod *)preferredBillingAccount
{
    return (self.billingAccounts ?: @[]).find(^(EHIUserPaymentMethod *paymentMethod) {
        return paymentMethod.isPreferred;
    });
}

- (EHIUserPaymentMethod *)preferredPaymentAccount
{
    return (self.paymentAccounts ?: @[]).find(^(EHIUserPaymentMethod *paymentMethod) {
        return paymentMethod.isPreferred;
    });
}

- (EHIContractDetails *)corporateContract
{
    return self.profiles.corporateContract;
}

- (NSArray<EHIUserPaymentMethod> *)creditCardPaymentMethods
{
    return (NSArray<EHIUserPaymentMethod> *)(self.payment.paymentMethods ?: @[]).select(^(EHIUserPaymentMethod *paymentMethod){
        return paymentMethod.paymentType == EHIUserPaymentTypeCard;
    });
}

- (NSString *)authorizationToken
{
    return self.additionalData.authToken;
}

- (NSString *)encryptedCredentials
{
    return self.additionalData.credentials;
}

- (NSString *)individualId
{
    return self.profiles.individualId;
}

- (NSInteger)points
{
    return self.profiles.basic.loyalty.pointsToDate;
}

- (void)attachPaymentMethod:(EHIUserPaymentMethod *)paymentMethod
{
    self.attachedPaymentMethod = paymentMethod;
}

- (void)updateAddress:(EHIAddress *)address
{
    self.address = address;
}

- (void)updateContact:(EHIUserContactProfile *)contact
{
    self.contact = contact;
}

- (void)updateSpecialOffersOptIn:(EHIOptionalBoolean)optIn
{
    EHIUserEmailPreferences *email = EHIUserEmailPreferences.new;
    email.specialOffers = optIn;

    (self.preference = self.preference ?: EHIUserPreferencesProfile.new).email = email;
}

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHIUser *)model
{
    return @{
        @"profile"                          : @key(model.profiles),
        @"additional_data"                  : @key(model.additionalData),
        @"prompt_for_email_update"          : @key(model.shouldPromptForEmail),
        @"prompt_login_possible_with_email" : @key(model.shouldPromptForLogin),
        @"contact_profile"                  : @key(model.contact),
        @"address_profile"                  : @key(model.address),
        @"license_profile"                  : @key(model.license),
        @"payment_profile"                  : @key(model.payment),
        @"terms_and_conditions"             : @key(model.terms),
    };
}

# pragma mark - EHINetworkEncodable

- (void)encodeWithRequest:(EHINetworkRequest *)request
{
    request[@"contact"]         = self.contact;
    request[@"address"]         = self.address;
    request[@"drivers_license"] = self.license;
    request[@"preference"]      = self.preference;
    
    if(self.attachedPaymentMethod) {
        request[@"payment"] = @{
            @"payment_method" : self.attachedPaymentMethod
        };
    }
}

# pragma mark - EHIAnalyticsEncodable

+ (void)encodeWithContext:(EHIAnalyticsContext *)context instance:(nullable EHIUser *)instance
{
    context[EHIAnalyticsUserAuthenticatedKey] = @((BOOL)(instance != nil));
    context[EHIAnalyticsUserEmailExtrasKey]   = @((BOOL)instance.preference.email.specialOffers == EHIOptionalBooleanTrue);
}

@end
