//
//  NSLocale+Country.m
//  Enterprise
//
//  Created by cgross on 12/10/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "NSLocale+Country.h"
#import "EHIDataStore.h"

@implementation NSLocale (Country)

+ (EHICountry *)ehi_country
{
    NSArray *countries = [EHIDataStore findInMemory:[EHICountry class]];
    
    return (countries ?: @[]).find(^(EHICountry *country) {
        return [country.code isEqualToString:[self ehi_region]];
    });
}

@end
