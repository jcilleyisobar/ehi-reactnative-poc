//
//  EHICarClassPrice.m
//  Enterprise
//
//  Created by mplace on 2/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICarClassPriceSummary.h"
#import "EHIModel_Subclass.h"

@interface EHICarClassPriceSummary ()
@property (assign, nonatomic) CGFloat totalCharged;
@property (assign, nonatomic) CGFloat amountDue;
@property (copy  , nonatomic) NSString *currencyCode;
@property (copy  , nonatomic) NSString *currencySymbol;
@end

@implementation EHICarClassPriceSummary

- (void)parseDictionary:(NSMutableDictionary *)dictionary
{
    [super parseDictionary:dictionary];
 
    // munge the fee summary prices into a nested line item
    dictionary[@key(self.feeSummmary)] = [dictionary ehi_remove:@[
        @"estimated_total_taxes_and_fees_view",
        @"estimated_total_taxes_and_fees_payment",
    ]].extend(@{
         @key(self.feeSummmary.title) : EHILocalizedString(@"class_details_taxes_fees_summary_title", @"Taxes & Fees", @"Class details taxes & fees line item title"),
         @key(self.feeSummmary.type)  : @"FEE_SUMMARY",
         @key(self.feeSummmary.total) : dictionary[@key(self.viewTotalTaxesFees)] ?: @{},
    });
}

# pragma mark - Computed

- (EHICarClassPriceLineItem *)redemptionSavings
{
    return (self.lineItems ?: @[]).find(^(EHICarClassPriceLineItem *lineItem) {
        return lineItem.type == EHIReservationLineItemTypeRedemption;
    });
}

- (BOOL)hasChargedItems
{
    return (self.lineItems ?: @[]).any(^(EHICarClassPriceLineItem *lineItem) {
        return lineItem.status == EHICarClassPriceLineItemStatusCharged;
    });
}

- (EHICarClassPriceLineItem *)findPriceLineItemWithCode:(NSString *)code
{
    return (self.lineItems ?: @[]).find(^(EHICarClassPriceLineItem *lineItem) {
        return [lineItem.code isEqualToString:code];
    });
}

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHICarClassPriceSummary *)model
{
    return @{
        @"payment_line_items"                           : @key(model.lineItems),
        @"estimated_total_view"                         : @key(model.viewTotal),
        @"estimated_total_payment"                      : @key(model.paymentTotal),
        @"estimated_total_taxes_and_fees_view"          : @key(model.viewTotalTaxesFees),
        @"estimated_total_taxes_and_fees_payment"       : @key(model.paymentTotalTaxesFees),
        @"estimated_total_extras_and_coverages_view"    : @key(model.viewTotalExtrasCoverage),
        @"estimated_total_extras_and_coverages_payment" : @key(model.paymentTotalExtrasCoverage),
        @"estimated_total_vehicle_view"                 : @key(model.viewTotalVehicle),
        @"estimated_total_vehicle_payment"              : @key(model.paymentTotalVehicle),
        @"estimated_total_savings_view"                 : @key(model.viewTotalSavings),
        @"estimated_total_savings_payment"              : @key(model.paymentTotalSavings),
        @"total_charged"                                : @key(model.totalCharged),
        @"amount_due"                                   : @key(model.amountDue),
        @"currency_code"                                : @key(model.currencyCode),
        @"currency_symbol"                              : @key(model.currencySymbol)
    };
}

# pragma mark - EHIPriceContext

- (BOOL)eligibleForCurrencyConvertion
{
    BOOL usaCA = [self.viewPrice.code isEqualToString:EHICurrencyCodeUSA] && [self.paymentPrice.code isEqualToString:EHICurrencyCodeCA];
    BOOL caUSA = [self.viewPrice.code isEqualToString:EHICurrencyCodeCA] && [self.paymentPrice.code isEqualToString:EHICurrencyCodeUSA];
    
    return usaCA || caUSA;
}

- (BOOL)viewCurrencyDiffersFromSourceCurrency
{
    return ![self.viewTotal.code isEqualToString:self.paymentTotal.code];
}

- (EHIPrice *)viewPrice
{
    return self.viewTotal;
}

- (EHIPrice *)paymentPrice
{
    return self.paymentTotal;
}

- (EHIPrice *)totalPrice
{
    return [EHIPrice modelWithDictionary:@{
        @"amount" : @(self.totalCharged),
        @"code"   : self.currencyCode ?: @"",
        @"symbol" : self.currencySymbol ?: @""
    }];
}

- (EHIPrice *)duePrice
{
    return [EHIPrice modelWithDictionary:@{
        @"amount" : @(self.amountDue),
        @"code"   : self.currencyCode ?: @"",
        @"symbol" : self.currencySymbol ?: @""
    }];
}

@end
