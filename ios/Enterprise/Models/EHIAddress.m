//
//  EHIAddress.m
//  Enterprise
//
//  Created by fhu on 5/19/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIAddress.h"
#import "EHIDataStore.h"
#import "EHICountry.h"

@implementation EHIAddress

# pragma mark - Formatting

- (NSString *)formattedAddress
{
    return [self formattedAddress:NO];
}

- (NSString *)formattedAddress:(BOOL)forceLinebreak
{
    NSString *address = @"";
    
    if(self.addressLines.count) {
        NSString *addressLine = self.addressLines.select(^(NSString *string){
            return string.length > 0;
        }).join(@", ");
        address = [address ehi_appendComponent:addressLine joinedBy:(forceLinebreak ? @"\n" : @", ")];
    }
    
    NSArray *lineTwo = @[];
    NSString *join   = @"";
    if([self isEuropeanAddress]) {
        lineTwo = [lineTwo ehi_safelyAppend:self.postalCode];
        lineTwo = [lineTwo ehi_safelyAppend:self.city];
        join = @" ";
    }
    else
    {
        lineTwo = [lineTwo ehi_safelyAppend:self.city];
        lineTwo = [lineTwo ehi_safelyAppend:self.subdivisionCode];
        lineTwo = [lineTwo ehi_safelyAppend:self.postalCode];
        join = @", ";
    }
    
    address = [address ehi_appendComponent:lineTwo.select(^(NSString *string){
        return string.length > 0;
    }).join(join)];
    
    return address;
}

//
// Helpers
//

- (BOOL)isEuropeanAddress
{
    return self.country.isEuropeanAddress;
}

# pragma mark - Accessors

- (EHICountry *)country
{
    NSArray *countries = [EHIDataStore findInMemory:[EHICountry class]];

    return (countries ?: @[]).find(^(EHICountry *country) {
        return [country.code isEqualToString:self.countryCode];
    });
}

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHIAddress *)model
{
    return @{
        @"state"            : @key(model.subdivisionCode),
        @"postal"           : @key(model.postalCode),
        @"country_code"     : @key(model.countryCode),
        @"country_name"     : @key(model.countryName),
        @"street_addresses" : @key(model.addressLines),
        @"address_type"     : @key(model.addressType),
        @"country_subdivision_code" : @key(model.subdivisionCode),
        @"country_subdivision_name" : @key(model.subdivisionName),
    };
}

# pragma mark - EHINetworkEncodable

- (void)encodeWithRequest:(EHINetworkRequest *)request
{
    [super encodeWithRequest:request];
    
    request[@"street_addresses"]            = self.addressLines;
    request[@"city"]                        = self.city;
    request[@"country_subdivision_code"]    = self.subdivisionCode;
    request[@"country_subdivision_name"]    = self.subdivisionName;
    request[@"country_code"]                = self.countryCode;
    request[@"country_name"]                = self.countryName;
    request[@"postal"]                      = self.postalCode;
    request[@"address_type"]                = self.addressType;
}

@end
