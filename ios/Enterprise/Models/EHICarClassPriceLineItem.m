//
//  EHICarClassPaymentLineItem.m
//  Enterprise
//
//  Created by mplace on 2/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel_Subclass.h"
#import "EHICarClassPriceLineItem.h"
#import "EHICarClassExtra.h"
#import "EHICarClassMileage.h"
#import "EHIPriceFormatter.h"
#import "EHICarClass.h"

@interface EHICarClassPriceLineItem ()
@property (copy  , nonatomic) NSString *title;
@property (copy  , nonatomic) NSString *percent;
@property (weak  , nonatomic) EHICarClassExtra *extra;
@property (weak  , nonatomic) EHICarClass *carClass;
@property (assign, nonatomic) EHIReservationLineItemType type;
@property (assign, nonatomic) EHICarClassPriceLineItemStatus status;
@property (copy  , nonatomic) NSString *rentalDurationTitle;
@property (copy  , nonatomic) NSString *rentalRateTitle;
@end

@implementation EHICarClassPriceLineItem

- (void)parseDictionary:(NSMutableDictionary *)dictionary
{
    [super parseDictionary:dictionary];

    [dictionary ehi_transform:@key(self.title) block:^(NSString *title) {
        if([title ehi_isEqualToStringIgnoringCase:@"Discount"]) {
            return EHILocalizedString(@"class_line_item_discount", @"Discount", @"");
        } else if([title ehi_isEqualToStringIgnoringCase:@"Account Adjustment"]) {
            return EHILocalizedString(@"class_line_item_account_adjustment", @"Account Adjustment", @"");
        }
        
        return title;
    }];
}

# pragma mark - Accessors

- (BOOL)isIncluded
{
    return self.status == EHICarClassPriceLineItemStatusIncluded
        || self.extra.isIncluded // we're also included if the extra is
        || (self.type == EHIReservationLineItemTypeFeeSummary && self.total.amount == 0.0f); // or if the fee summary totals to 0
}

- (BOOL)isCharged
{
    return self.status == EHICarClassPriceLineItemStatusCharged;
}

- (BOOL)isWaived
{
    return self.status == EHICarClassPriceLineItemStatusWaived
        || self.extra.isWaived; // we're also waived if the extra is
    
}

# pragma mark - EHIReservationLineItemRenderable

- (NSString *)formattedTitle
{
    switch(self.type) {
        case EHIReservationLineItemTypeVehicleRate:
            return self.rentalDurationTitle;
        case EHIReservationLineItemTypeEquipment:
        case EHIReservationLineItemTypeCoverage:
            return self.extra.name ?: self.title;
        case EHIReservationLineItemTypeRedemption:
            return EHILocalizedString(@"redemption_line_item_title", @"Redemption Credit", @"title for the redemption savings cell");
        default: return self.title;
    }
}

- (NSString *)formattedRate
{
    if(_formattedRate) {
        return _formattedRate;
    }
    
    switch(self.type) {
        case EHIReservationLineItemTypeEquipment:
        case EHIReservationLineItemTypeVehicleRate:
            return self.rentalRateTitle;
        case EHIReservationLineItemTypeRedemption:
            return self.formattedSubtitle;
        default: return nil;
    }
}

- (NSString *)formattedTotal
{
    return [self formatPrice:self.total];
}

- (EHIPrice *)viewPrice
{
    return self.total;
}

- (BOOL)hasDetails
{
    switch(self.type) {
        case EHIReservationLineItemTypeEquipment:
        case EHIReservationLineItemTypeCoverage:
            return self.extra.longDetails.length != 0;
        default: return NO;
    }
}

- (NSInteger)quantity
{
    return self.extra.selectedQuantity;
}

- (NSString *)longDetails
{
    return self.extra.longDetails;
}

- (NSString *)formattedSubtitle
{
    switch(self.type) {
            // uncomment when redemption credit calculation is fixed in ORCH
        case EHIReservationLineItemTypeRedemption:
            return [NSString stringWithFormat:@"%ld %@: %ld %@",
                    (long)self.carClass.daysToRedeem, EHILocalizedString(@"reservation_rate_daily_unit_plural", @"Days", @""),
                    (long)self.carClass.redemptionPoints, EHILocalizedString(@"redemption_points_per_day", @"points/day", @"")];
        default: return nil;
    }
}

- (NSString *)formattedType
{
    NSString *rateUnit = [self rateUnit:NO];
    switch (self.rateType) {
        case EHIPriceRateTypePercent:
            return [NSString stringWithFormat:@"%.2f %@", self.rate.amount, rateUnit];
        default: {
            NSString *rateFormat =
            EHILocalizedString(@"reservation_line_item_rental_rate_title", @"#{price} / #{unit}", @"");
            
            return [rateFormat ehi_applyReplacementMap:@{
                @"price" : [self formatPrice:self.rate],
                @"unit"  : rateUnit,
            }];
        }
    }
}

- (NSString *)formattedRateTotal
{
    switch (self.rateType) {
        case EHIPriceRateTypeDay:
        case EHIPriceRateTypePercent:
            [self formatPrice:self.rate];
        default: return nil;
    }
}

//
// Helpers
//

- (NSString *)rentalDurationTitle
{
    NSString *titleFormat =
        EHILocalizedString(@"reservation_line_item_rental_duration_title", @"#{duration} #{unit} of Rental", @"Title for the rental duration line item");
    
    return [titleFormat ehi_applyReplacementMap:@{
        @"duration" : @(self.duration),
        @"unit"     : [self rateUnit:self.duration > 1],
    }];
}

- (NSString *)rentalRateTitle
{
    NSString *unit = [self rateUnit:NO];
    BOOL hasUnit   = ![unit ehi_isEqualToStringIgnoringCase:EHIPriceRateTypeLocalizedUnitNull];
    BOOL shouldShowRate =  hasUnit && !self.isWaived;
    if(shouldShowRate){
        NSString *rateFormat =
        EHILocalizedString(@"reservation_line_item_rental_rate_title", @"#{price} / #{unit}", @"Rate text for the rental duration line item ");
        
        return [rateFormat ehi_applyReplacementMap:@{
            @"price" : [self formatPrice:self.rate],
            @"unit"  : unit,
        }];
    } else {
        return nil;
    }
}

- (NSString *)rateUnit:(BOOL)plural
{
    return EHIPriceRateTypeLocalizedUnit(self.rateType, plural);
}

- (NSString *)formatPrice:(EHIPrice *)price
{
    if (self.isIncluded){
        return EHILocalizedString(@"payment_line_item_included", @"Included", @"");
    } else if (self.isWaived){
        return @"-";
    } else {
        return [EHIPriceFormatter format:price].scalesChange(NO).string;
    }
}

# pragma mark - Links

- (void)linkExtra:(EHICarClassExtra *)extra
{
    self.extra = extra;
}

- (void)linkCarClass:(EHICarClass *)carClass
{
    self.carClass = carClass;
}

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHICarClassPriceLineItem *)model
{
    return @{
        @"description"          : @key(model.title),
        @"category"             : @key(model.type),
        @"rate_type"            : @key(model.rateType),
        @"total_amount_view"    : @key(model.total),
        @"rate_amount"          : @key(model.percent),
        @"rate_amount_view"     : @key(model.rate),
        @"rate_quantity"        : @key(model.duration),
        // fee-specific
        @"estimated_total_taxes_and_fees_view"       : @key(model.total),
        // extras-specific
        @"estimated_total_extras_and_coverages_view" : @key(model.total),
        // rental-specific
        @"estimated_vehicle_rate_view"               : @key(model.total),
        // miscellaneous-specific
        @"estimated_savings_view"                    : @key(model.total)
    };
}

+ (void)registerTransformers:(EHICarClassPriceLineItem *)model
{
    [self key:@key(model.rateType) registerTransformer:EHIPriceRateTypeTransformer()];
    [self key:@key(model.type) registerMap:@{
        @"FEE"          : @(EHIReservationLineItemTypeFee),
        @"FEE_SUMMARY"  : @(EHIReservationLineItemTypeFeeSummary),
        @"VEHICLE_RATE" : @(EHIReservationLineItemTypeVehicleRate),
        @"COVERAGE"     : @(EHIReservationLineItemTypeCoverage),
        @"EQUIPMENT"    : @(EHIReservationLineItemTypeEquipment),
        @"SAVINGS"      : @(EHIReservationLineItemTypeSavings),
        @"EPLUS_REDEMPTION_SAVINGS" : @(EHIReservationLineItemTypeRedemption)
    } defaultValue:@(EHIReservationLineItemTypeUnknown)];
    
    [self key:@key(model.status) registerMap:@{
        @"INCLUDED"     : @(EHICarClassPriceLineItemStatusIncluded),
        @"CHARGED"      : @(EHICarClassPriceLineItemStatusCharged),
        @"WAIVED"      : @(EHICarClassPriceLineItemStatusWaived),
    } defaultValue:@(EHICarClassPriceLineItemStatusNone)];
}

@end

@implementation EHICarClassPriceLineItem (Generators)

+ (instancetype)lineItemForExtra:(EHICarClassExtra *)extra type:(EHIReservationLineItemType)type
{
    EHICarClassPriceLineItem *lineItem = [EHICarClassPriceLineItem new];
    lineItem.type = type;
    
    [lineItem linkExtra:extra];
    
    return lineItem;
}

+ (instancetype)lineItemForMileage:(EHICarClassMileage *)mileage
{
    EHICarClassPriceLineItem *lineItem = [EHICarClassPriceLineItem new];
    
    lineItem.type = EHIReservationLineItemTypeFeeMileage;
    lineItem.status = EHICarClassPriceLineItemStatusIncluded;
    lineItem.formattedRate = mileage.subtitle;
    
    [lineItem updateWithDictionary:@{
        @key(lineItem.title) : (mileage.title ?: @""),
    }];
    
    return lineItem;
}

@end
