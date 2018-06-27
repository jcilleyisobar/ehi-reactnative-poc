//
//  EHICarClassPrice.h
//  Enterprise
//
//  Created by mplace on 2/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIPrice.h"
#import "EHICarClassPriceLineItem.h"
#import "EHIPriceContext.h"

@interface EHICarClassPriceSummary : EHIModel <EHIPriceContext>
@property (strong, nonatomic, readonly) EHIPrice *viewTotal;
@property (strong, nonatomic, readonly) EHIPrice *paymentTotal;
@property (strong, nonatomic, readonly) EHIPrice *viewTotalTaxesFees;
@property (strong, nonatomic, readonly) EHIPrice *paymentTotalTaxesFees;
@property (strong, nonatomic, readonly) EHIPrice *viewTotalExtrasCoverage;
@property (strong, nonatomic, readonly) EHIPrice *paymentTotalExtrasCoverage;
@property (strong, nonatomic, readonly) EHIPrice *viewTotalVehicle;
@property (strong, nonatomic, readonly) EHIPrice *paymentTotalVehicle;
@property (strong, nonatomic, readonly) EHIPrice *viewTotalSavings;
@property (strong, nonatomic, readonly) EHIPrice *paymentTotalSavings;
@property (strong, nonatomic, readonly) EHICarClassPriceLineItem *feeSummmary;
@property (copy  , nonatomic, readonly) NSArray<EHICarClassPriceLineItem> *lineItems;
@property (assign, nonatomic, readonly) BOOL hasChargedItems;

// computed
- (EHIPrice *)totalPrice;
- (EHIPrice *)duePrice;

/** The redemption savings line item */
@property (strong, nonatomic, readonly) EHICarClassPriceLineItem *redemptionSavings;

- (EHICarClassPriceLineItem *)findPriceLineItemWithCode:(NSString *)withCode;

@end
