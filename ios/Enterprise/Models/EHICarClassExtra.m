//
//  EHICarClassExtra.m
//  Enterprise
//
//  Created by mplace on 2/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICarClassExtra.h"
#import "EHIModel_Subclass.h"
#import "EHIPriceFormatter.h"

@implementation EHICarClassExtra

# pragma mark - Accessors

- (NSString *)statusText
{
    return [[self.class transformerForKey:@key(self.status)] reverseTransformedValue:@(self.status)];
}

- (NSString *)frequencyText
{
    return EHIPriceRateTypeLocalizedUnit(self.rateType, NO);
}

- (NSString *)rateDescriptionWithMax
{
    if (self.status == EHICarClassExtraStatusWaived) {
        return [EHILocalizedString(@"car_extras_waived_extra_pricing_info", @"Free", @"") uppercaseString];
    }
    
    // generate the rate string at a minimum
    NSString *rate       = [EHIPriceFormatter format:self.rate].string;
    NSString *rateFormat = EHILocalizedString(@"reservation_line_item_rental_rate_title", @"#{price} / #{unit}", @"");
    NSString *result     = [rateFormat ehi_applyReplacementMap:@{
        @"price" : rate,
        @"unit"  : self.frequencyText,
    }];
    
    // if rate unit == @"<null>" just show the price
    BOOL hasUnit = ![self.frequencyText ehi_isEqualToStringIgnoringCase:EHIPriceRateTypeLocalizedUnitNull] || self.frequencyText.length;
    if(!hasUnit) {
        result = rate;
    }
    
    // if we have a max price
    if(self.maxPrice.amount > 0.0f) {
        NSString *maximum   = [EHIPriceFormatter format:self.maxPrice].string;
        NSString *maxFormat = EHILocalizedString(@"extra_max", @"(#{price} MAX)", @"");
      
        // and can format it successfully, append it to the result
        if(maximum) {
            result = [result stringByAppendingFormat:@" %@", [maxFormat ehi_applyReplacementMap:@{
                @"price" : maximum
            }]];
        }
    }
    
    return result;
}

- (BOOL)isSelected
{
    return self.selectedQuantity > 0;
}

- (BOOL)isOptional
{
    return self.status == EHICarClassExtraStatusOptional;
}

- (BOOL)isIncluded
{
    return self.status == EHICarClassExtraStatusIncluded;
}

- (BOOL)isMandatory
{
    return self.status == EHICarClassExtraStatusMandatory;
}

- (BOOL)isWaived
{
    return self.status == EHICarClassExtraStatusWaived;
}

# pragma mark - EHINetworkEncodable

- (void)encodeWithRequest:(EHINetworkRequest *)request
{
    request[@"code"]     = self.code;
    request[@"quantity"] = @(self.selectedQuantity);
}

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHICarClassExtra *)model
{
    return @{
        @"rate_type"            : @key(model.rateType),
        @"lrd_policy_code"      : @key(model.keyFactsCode),
        @"rate_amount_view"     : @key(model.rate),
        @"max_amount_view"      : @key(model.maxPrice),
        @"max_quantity"         : @key(model.maxQuantity),
        @"selected_quantity"    : @key(model.selectedQuantity),
        @"description"          : @key(model.shortDetails),
        @"detailed_description" : @key(model.longDetails),
        @"total_amount_view"    : @key(model.total)
    };
}

+ (void)registerTransformers:(EHICarClassExtra *)model
{
    // register a mapping for the extra status
    [self key:@key(model.status) registerMap:@{
        @"OPTIONAL"   : @(EHICarClassExtraStatusOptional),
        @"MANDATORY"  : @(EHICarClassExtraStatusMandatory),
        @"INCLUDED"   : @(EHICarClassExtraStatusIncluded),
        @"WAIVED"     : @(EHICarClassExtraStatusWaived),
    } defaultValue:@(EHICarClassExtraStatusUnknown)];
  
    // register a mapping for the extra allocation
    [self key:@key(model.allocation) registerMap:@{
        @"ON_REQUEST" : @(EHICarClassExtraAllocationOnRequest),
        @"FREE_SELL"  : @(EHICarClassExtraAllocationFreeSell),
        @"STOP_SELL"  : @(EHICarClassExtraAllocationStopSell),
    } defaultValue:@(EHICarClassExtraAllocationUnknown)];
    
    // register the common rate type transformer
    [self key:@key(model.rateType) registerTransformer:EHIPriceRateTypeTransformer()];
}

# pragma mark - EHIAnalyticsEncodable

+ (void)encodeWithContext:(EHIAnalyticsContext *)context instance:(nullable EHICarClassExtra *)instance
{
    context[EHIAnalyticsResTappedExtraKey] = instance.code;
}

@end
