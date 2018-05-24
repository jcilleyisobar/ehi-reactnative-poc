//
//  EHICountry.m
//  Enterprise
//
//  Created by Alex Koller on 5/8/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICountry.h"
#import "EHIModel_Subclass.h"

@interface EHICountry()
@property (strong, nonatomic, readonly) NSArray<EHIPromotionContract> *promotions;
@end

@implementation EHICountry

- (id)uid
{
    return self.code;
}

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHICountry *)model
{
    return @{
        @"code"                                : @key(model.code),
        @"country_code"                        : @key(model.code),
        @"country_name"                        : @key(model.name),
        @"enable_as_issuing_country"           : @key(model.enableIssuingCountry),
        @"enable_country_sub_division"         : @key(model.enableCountrySubdivision),
        @"disable_one_way_rental"              : @key(model.disableOneWay),
        @"default_email_opt_in"                : @key(model.defaultEmailOptIn),
        @"street_address_two"                  : @key(model.streetAddressTwo),
        @"house_number"                        : @key(model.houseNumber),
        @"issuing_authority_name"              : @key(model.issuingAuthorityName),
        @"license_issuing_authority_required"  : @key(model.isLicenseIssuingAuthorityRequired),
        @"license_issued_by"                   : @key(model.licenseIssuedBy),
        @"license_expiry_date"                 : @key(model.licenseExpiryDate),
        @"license_issue_date"                  : @key(model.licenseIssueDate),
        @"european_address_flag"               : @key(model.isEuropeanAddress),
        @"key_facts_dispute_email"             : @key(model.disputeEmail),
        @"key_facts_dispute_phone"             : @key(model.disputePhone),
        @"contracts"                           : @key(model.promotions)
    };
}

+ (void)registerTransformers:(EHICountry *)model
{
    NSDictionary *fieldVisibilityMap = @{
        @"UNSUPPORTED" : @(EHICountryFieldVisibilityUnsupported),
        @"OPTIONAL"    : @(EHICountryFieldVisibilityOptional),
        @"MANDATORY"   : @(EHICountryFieldVisibilityMandatory),
    };
    
    [self key:@key(model.streetAddressTwo) registerMap:fieldVisibilityMap defaultValue:@(EHICountryFieldVisibilityUnknown)];
    [self key:@key(model.houseNumber) registerMap:fieldVisibilityMap defaultValue:@(EHICountryFieldVisibilityUnknown)];
    [self key:@key(model.licenseIssuedBy) registerMap:fieldVisibilityMap defaultValue:@(EHICountryFieldVisibilityUnknown)];
    [self key:@key(model.licenseExpiryDate) registerMap:fieldVisibilityMap defaultValue:@(EHICountryFieldVisibilityUnknown)];
    [self key:@key(model.licenseIssueDate) registerMap:fieldVisibilityMap defaultValue:@(EHICountryFieldVisibilityUnknown)];
}


# pragma mark - Accessors

- (BOOL)isNorthAmerica
{
    return self.isUS || self.isCanada;
}

- (BOOL)isUS
{
    return [self.code isEqualToString:EHICountryCodeUS];
}

- (BOOL)isCanada
{
    return [self.code isEqualToString:EHICountryCodeCanada];
}

- (BOOL)isFrance
{
    return [self.code isEqualToString:EHICountryCodeFrance];
}

- (BOOL)isUK
{
    return [self.code isEqualToString:EHICountryCodeUK];
}

- (BOOL)shouldMoveVansToEndOfList
{
    // if outside of US or Canada, move Vans to end of list
    return !self.isUS && !self.isCanada;
}

- (BOOL)shouldShowIdentityCheckWithExternalVendorMessage
{
    return self.isUK;
}

# pragma mark - Promotions

- (EHIPromotionContract *)weekendSpecial
{
    return (self.promotions ?: @[]).find(^(EHIPromotionContract *promotion) {
        return promotion.type == EHIPromotionContractTypeWeekendSpecial;
    });
}

# pragma mark - Collection

+ (void)prepareCollection:(EHICollection *)collection
{
    collection.inMemoryOnly = YES;
}

@end
