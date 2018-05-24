//
//  EHICountryTerms.m
//  Enterprise
//
//  Created by frhoads on 10/26/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHITermsCountries.h"

@implementation EHITermsCountries

+ (NSDictionary *)mappings:(EHITermsCountries *)model
{
    return @{
                @"rental_terms_and_conditions" : @key(model.termsLanguages),
             };
}

@end
