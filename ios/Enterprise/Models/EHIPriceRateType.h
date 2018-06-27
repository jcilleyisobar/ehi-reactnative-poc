//
//  EHIPriceRateType.h
//  Enterprise
//
//  Created by Ty Cobb on 4/8/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIMapTransformer.h"
#import "EHILocalization.h"

#define EHIPriceRateTypeLocalizedUnitNull @"<null>"

typedef NS_ENUM(NSInteger, EHIPriceRateType) {
    EHIPriceRateTypeUnknown,
    EHIPriceRateTypeHourly,
    EHIPriceRateTypeDaily,
    EHIPriceRateTypeWeekly,
    EHIPriceRateTypeMonthly,
    EHIPriceRateTypeExtraDaily,
    EHIPriceRateTypeRental,
    EHIPriceRateTypeGallon,
    EHIPriceRateTypeDay,
    EHIPriceRateTypePercent
};

NS_INLINE NSValueTransformer * EHIPriceRateTypeTransformer()
{
    // apply the service key -> enum mapping
    EHIMapTransformer *transformer = [[EHIMapTransformer alloc] initWithMap:@{
        @"HOURLY"      : @(EHIPriceRateTypeHourly),
        @"DAY"         : @(EHIPriceRateTypeDay),
        @"DAILY"       : @(EHIPriceRateTypeDaily),
        @"WEEKLY"      : @(EHIPriceRateTypeWeekly),
        @"MONTHLY"     : @(EHIPriceRateTypeMonthly),
        @"RENTAL"      : @(EHIPriceRateTypeRental),
        @"EXTRA_DAILY" : @(EHIPriceRateTypeExtraDaily),
        @"GALLON"      : @(EHIPriceRateTypeGallon),
        @"PERCENT"     : @(EHIPriceRateTypePercent)
    }];
   
    // default to unknown
    transformer.defaultValue = @(EHIPriceRateTypeUnknown);
    
    return transformer;
}

NS_INLINE NSString * EHIPriceRateTypeLocalizedUnit(EHIPriceRateType rate, BOOL plural)
{
    switch(rate) {
        case EHIPriceRateTypeHourly:
            return plural
                ? EHILocalizedString(@"reservation_rate_hourly_unit_plural", @"Hours", @"Unit plural for the 'hours' rental rate")
                : EHILocalizedString(@"reservation_rate_hourly_unit", @"Hour", @"Unit name for the 'hour' rental rate");
        case EHIPriceRateTypeDay:
        case EHIPriceRateTypeDaily:
            return plural
                ? EHILocalizedString(@"reservation_rate_daily_unit_plural", @"Days", @"Unit plural for the 'daily' rental rate")
                : EHILocalizedString(@"reservation_rate_daily_unit", @"Day", @"Unit name for the 'daily' rental rate");
        case EHIPriceRateTypeWeekly:
            return plural
                ? EHILocalizedString(@"reservation_rate_weekly_unit_plural", @"Weeks", @"Unit plural for the 'weekly' rental rate")
                : EHILocalizedString(@"reservation_rate_weekly_unit", @"Week", @"Unit name for the 'weekly' rental rate");
        case EHIPriceRateTypeMonthly:
            return plural
                ? EHILocalizedString(@"reservation_rate_monthly_unit_plural", @"Months", @"Unit plural for the 'weekly' rental rate")
                : EHILocalizedString(@"reservation_rate_monthly_unit", @"Month", @"Unit name for the 'weekly' rental rate");
        case EHIPriceRateTypeGallon:
            return plural
                ? EHILocalizedString(@"reservation_rate_gallon_unit_plural", @"Gallons", @"Unit plural for the 'gallon' rental rate")
                : EHILocalizedString(@"reservation_rate_gallon_unit", @"Gallon", @"Unit name for the 'gallon' rental rate");
        case EHIPriceRateTypeRental:
            return EHILocalizedString(@"reservation_rate_rental_unit", @"Rental", @"Unit name for the 'rental' rental rate");
        case EHIPriceRateTypePercent:
            return @"%";
        default: return EHIPriceRateTypeLocalizedUnitNull;
    }
}
