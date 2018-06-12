//
//  EHIUserLicenseProfile.m
//  Enterprise
//
//  Created by mplace on 2/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIUserLicenseProfile.h"
#import "EHIModel_Subclass.h"

@interface EHIUserLicenseProfile ()
@property (copy  , nonatomic) NSString *maskedLicenseIssue;
@end

@implementation EHIUserLicenseProfile

- (void)parseDictionary:(NSMutableDictionary *)dictionary
{
    [super parseDictionary:dictionary];
    
    [dictionary ehi_transform:@key(self.licenseExpiry) selector:@selector(ehi_date)];
    [dictionary ehi_transform:@key(self.birthdate) selector:@selector(ehi_date)];

    NSString *licenseIssue = [dictionary valueForKey:@key(self.licenseIssue)];
    if(licenseIssue.ehi_isMasked) {
        self.maskedLicenseIssue = licenseIssue;
    } else {
        [dictionary ehi_transform:@key(self.licenseIssue) selector:@selector(ehi_date)];
    }
}

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHIUserLicenseProfile *)model
{
    return @{
        @"license_number"           : @key(model.licenseNumber),
        @"country_subdivision_code" : @key(model.subdivisionCode),
        @"country_subdivision_name" : @key(model.subdivisionName),
        @"country_code"             : @key(model.countryCode),
        @"country_name"             : @key(model.countryName),
        @"license_issue"            : @key(model.licenseIssue),
        @"license_issue_date"       : @key(model.licenseIssue),
        @"license_expiry"           : @key(model.licenseExpiry),
        @"issuing_authority"        : @key(model.issuingAuthority),
        @"license_expiration_date"  : @key(model.maskedLicenseExpiry),
        @"unmasked_date_of_birth"   : @key(model.birthdate),
        @"birth_date"               : @key(model.maskedBirthDate),
        @"do_not_rent_indicator"    : @key(model.isOnDnrList)
    };
}

# pragma mark - EHINetworkEncodable

- (void)encodeWithRequest:(EHINetworkRequest *)request
{
    [super encodeWithRequest:request];
    
    request[@"license_number"]           = self.licenseNumber;
    request[@"issuing_authority"]        = self.issuingAuthority;
    request[@"country_subdivision_code"] = self.subdivisionCode;
    request[@"country_subdivision_name"] = self.subdivisionName;
    request[@"country_code"]             = self.countryCode;
    request[@"country_name"]             = self.countryName;
    request[@"license_expiration_date"]  = [self.licenseExpiry ehi_string] ?: self.maskedLicenseExpiry;
    request[@"unmasked_date_of_birth"]   = [self.birthdate ehi_string];
    request[@"birth_date"]               = self.maskedBirthDate;
    
    // validation for masking
    NSString *licenseIssue = NSString.new;
    if([self.licenseIssue isKindOfClass:NSDate.class]) {
        licenseIssue = self.licenseIssue.ehi_string;
    } else {
        licenseIssue = self.maskedLicenseIssue;
    }
    request[@"license_issue_date"] = licenseIssue;
}

@end
