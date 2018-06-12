//
//  EHICarClassMileage.m
//  Enterprise
//
//  Created by fhu on 7/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICarClassMileage.h"
#import "EHIModel_Subclass.h"
#import "EHIPriceFormatter.h"

@implementation EHICarClassMileage

# pragma mark - Accessors

- (NSString *)title
{
    return EHILocalizedString(@"price_section_mileage_item_title", @"Mileage", @"");
}

- (NSString *)subtitle
{
    return !self.unlimitedMileage
        ? self.ratePrefix
        : nil;
}

- (NSString *)rateString
{
    return [NSString stringWithFormat:@"%@ /%@", [EHIPriceFormatter format:self.excessMileageRate].scalesChange(NO).string, self.distanceUnit];
}

- (NSString *)ratePrefix
{
    NSString *miles      = @(self.self.totalFreeMiles).description;
    NSString *price      = [EHIPriceFormatter format:self.excessMileageRate].string ?: @"";
    NSString *additional = EHILocalizedString(@"price_section_mileage_additional", @"additional", @"");
    NSString *unit       = self.distanceUnit ?: @"";
    
    return [NSString stringWithFormat:@"%@ %@ - %@ / %@ %@", miles, unit, price, additional, unit];
}

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHICarClassMileage *)model
{
    return @{
        @"total_free_miles"           : @key(model.totalFreeMiles),
        @"excess_mileage_rate_view"   : @key(model.excessMileageRate),
        @"distance_unit"              : @key(model.distanceUnit),
        @"unlimited_mileage"          : @key(model.unlimitedMileage),
    };
}

@end
