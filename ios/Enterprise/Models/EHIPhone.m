//
//  EHIUserPhoneNumber.m
//  Enterprise
//
//  Created by mplace on 2/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIPhone.h"
#import "EHIModel_Subclass.h"

@implementation EHIPhone

+ (NSDictionary *)mappings:(EHIPhone *)model
{
    return @{
        @"phone_type"        : @key(model.type),
        @"phone"             : @key(model.number),
        @"phone_number"      : @key(model.number),
        @"phoneNumber"       : @key(model.number),
        @"country_code"      : @key(model.countryCode),
        @"country_name"      : @key(model.countryName),
        @"mask_phone_number" : @key(model.maskedNumber),
        @"default_indicator" : @key(model.isDefault),
    };
}

+ (void)registerTransformers:(EHIPhone *)model
{
    [self key:@key(model.type) registerMap:@{
        @"MOBILE"     : @(EHIPhoneTypeMobile),
        @"HOME"       : @(EHIPhoneTypeHome),
        @"OFFICE"     : @(EHIPhoneTypeOffice),
        @"WORK"       : @(EHIPhoneTypeWork),
        @"FAX"        : @(EHIPhoneTypeFax),
        @"OTHER"      : @(EHIPhoneTypeOther),
        @"CONTACT_US" : @(EHIPhoneTypeContactUs),
        @"EPLUS"      : @(EHIPhoneTypeEPlus),
        @"DISABILITES": @(EHIPhoneTypeDisabilities),
        @"DNR"        : @(EHIPhoneTypeDnr),
        @"ROADSIDE_ASSISTANCE" : @(EHIPhoneTypeRoadside),
    } defaultValue : @(EHIPhoneTypeOther)];
}

# pragma mark - Accessors

- (NSString *)typeTitle
{
    return [EHIPhone titleForType:self.type];
}

+ (NSArray *)userPhoneTypeOptions
{
    // create order of phone type options
    return @[
        @(EHIPhoneTypeMobile),
        @(EHIPhoneTypeHome),
        @(EHIPhoneTypeWork),
        @(EHIPhoneTypeFax),
        @(EHIPhoneTypeOther)
    ];
}

+ (NSArray *)userPhoneTypeOptionsStrings
{
    // convert enums to title strings
    return self.userPhoneTypeOptions.map(^(NSNumber *type) {
        return [EHIPhone titleForType:type.unsignedIntegerValue];
    });
}

//
// Helpers
//

+ (NSString *)titleForType:(EHIPhoneType)type
{
    switch(type) {
        case EHIPhoneTypeContactUs:
            return EHILocalizedString(@"user_phone_type_contact_us", @"Contact Us", @"");
        case EHIPhoneTypeRoadside:
            return EHILocalizedString(@"user_phone_type_roadside", @"Roadside Assistance", @"");
        case EHIPhoneTypeEPlus:
            return EHILocalizedString(@"user_phone_type_eplus", @"Enterprise Plus", @"");
        case EHIPhoneTypeDisabilities:
            return EHILocalizedString(@"user_phone_type_disabilities", @"Customers with Disabilities", @"");
        case EHIPhoneTypeMobile:
            return EHILocalizedString(@"user_phone_type_mobile", @"Mobile", @"");
        case EHIPhoneTypeHome:
            return EHILocalizedString(@"user_phone_type_home", @"Home", @"");
        case EHIPhoneTypeWork:
            return EHILocalizedString(@"user_phone_type_work", @"Work", @"");
        case EHIPhoneTypeFax:
            return EHILocalizedString(@"user_phone_type_fax", @"Fax", @"");
        case EHIPhoneTypeOther:
            return EHILocalizedString(@"user_phone_type_other", @"Other", @"");
        case EHIPhoneTypeOffice:
            return EHILocalizedString(@"user_phone_type_office", @"Office", @"");
        case EHIPhoneTypeDnr:
            return EHILocalizedString(@"user_phone_type_dnr", @"DNR", @"");
        case EHIPhoneTypeUnknown:
            return nil;
    }
}

# pragma mark - EHINetworkEncodable

- (void)encodeWithRequest:(EHINetworkRequest *)request
{
    [super encodeWithRequest:request];
    
    // default to mobile for services
    if(self.type == EHIPhoneTypeUnknown) {
        self.type = EHIPhoneTypeMobile;
    }
    
    request[@"phone_type"]   = [[self.class transformerForKey:@key(self.type)] reverseTransformedValue:@(self.type)];
    request[@"priority"]     = @(self.priority);
    request[@"country_code"] = self.countryCode;
    request[@"country_name"] = self.countryName;
    request[@"phone_number"] = self.number.ehi_isMasked ? self.number : [self.number ehi_stripNonDecimalCharacters];
}

@end
