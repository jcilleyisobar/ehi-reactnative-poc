//
//  EHIUserProfileFetch.m
//  Enterprise
//
//  Created by Rafael Machado on 8/16/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIUserProfileFetch.h"

@interface EHIUserProfileFetch ()
@property (copy, nonatomic)   NSString *country;
@property (copy, nonatomic)   NSString *countrySubdivision;
@property (copy, nonatomic)   NSString *issuingAuthority;
@property (copy, nonatomic)   NSString *licenseNumber;
@property (copy, nonatomic)   NSString *lastName;

@end

@implementation EHIUserProfileFetch

+ (instancetype)modelForUser:(EHIUser *)user
{
    EHIUserProfileFetch *fetch = [EHIUserProfileFetch new];
    
    fetch.country              = user.license.countryCode ?: @"";
    fetch.countrySubdivision   = user.license.subdivisionCode ?: @"";
    fetch.licenseNumber        = user.license.licenseNumber ?: @"";
    fetch.lastName             = user.profiles.basic.lastName ?: @"";
    fetch.issuingAuthority     = user.license.issuingAuthority ?: @"";
    
    return fetch;
}

# pragma mark - EHINetworkEncodable

- (void)encodeWithRequest:(EHINetworkRequest *)request
{
    request[@"country"]                = self.country;
    request[@"driver_license_number"]  = self.licenseNumber;
    request[@"last_name"]              = self.lastName;
    request[@"issuing_authority"]   = self.issuingAuthority;
    request[@"country_subdivision"] = self.countrySubdivision;
}

@end
