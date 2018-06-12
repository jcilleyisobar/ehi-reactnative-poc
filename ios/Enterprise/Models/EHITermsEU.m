//
//  EHITermsEU.m
//  Enterprise
//
//  Created by frhoads on 10/26/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHITermsEU.h"
#import "EHIModel_Subclass.h"

@implementation EHITermsEU

+ (NSDictionary *)mappings:(EHITermsEU *)model
{
    return @{
        @"locale_label"                      : @key(model.language),
        @"rental_terms_and_conditions_text"  : @key(model.termsAndConditionsText),
        @"locale"                            : @key(model.countryCode),
    };
}

@end
