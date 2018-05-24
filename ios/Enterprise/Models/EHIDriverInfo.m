//
//  EHIDriverInfo.m
//  Enterprise
//
//  Created by Alex Koller on 4/8/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel_Subclass.h"
#import "EHIDriverInfo.h"

@implementation EHIDriverInfo

- (void)parseDictionary:(NSMutableDictionary *)dictionary
{
    [super parseDictionary:dictionary];
    
    // create a dummy uid if necessary
    if(!self.uid) {
        dictionary[@key(self.uid)] = [NSUUID UUID].UUIDString;
    }
}

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHIDriverInfo *)model
{
    return @{
        @"individual_identifier"    : @key(model.uid),
        @"loyalty_program_type"     : @key(model.loyaltyType),
        @"drivers_license"          : @key(model.licenseProfile),
        @"first_name"               : @key(model.firstName),
        @"last_name"                : @key(model.lastName),
        @"phone_number"             : @key(model.phone),
        @"email_address"            : @key(model.email),
        @"mask_email_address"       : @key(model.maskedEmail),
        @"request_email_promotions" : @key(model.wantsEmailNotifications),
    };
}

+ (void)registerTransformers:(EHIDriverInfo *)model
{
    [self key:@key(model.loyaltyType) registerMap:@{
        @"EPLUS"       : @(EHIDriverInfoLoyaltyTypeEnterprisePlus),
        @"EMERALDCLUB" : @(EHIDriverInfoLoyaltyTypeEmeraldClub),
    } defaultValue:@(EHIDriverInfoLoyaltyTypeUnknown)];

    [self key:@key(model.wantsEmailNotifications) registerTransformer:EHIOptionalBooleanTransformer()];
}

# pragma mark - EHINetworkEncodable

- (void)encodeWithRequest:(EHINetworkRequest *)request
{
    [super encodeWithRequest:request];
    
    request[@"first_name"]         = [self firstName];
    request[@"last_name"]          = [self.lastName stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
    request[@"email_address"]      = [self email];
    request[@"mask_email_address"] = [self maskedEmail];
    request[@"phone"]              = [self phone];
    request[@"request_email_promotions"] = [[self.class transformerForKey:@key(self.wantsEmailNotifications)] reverseTransformedValue:@(self.wantsEmailNotifications)];
}

# pragma mark - Accessors

- (NSString *)fullName
{
    return [NSString stringWithFormat:@"%@ %@", self.firstName, self.lastName];
}

- (BOOL)hasRequiredFields
{
    return self.firstName.length != 0
           && self.lastName.length != 0
           && self.phone.number.length != 0
           && [self.email ehi_validEmail];
}

# pragma mark - Collection

+ (void)prepareCollection:(EHICollection *)collection
{
    collection.historyLimit = 1;
    collection.isSecure = YES;
}

@end
