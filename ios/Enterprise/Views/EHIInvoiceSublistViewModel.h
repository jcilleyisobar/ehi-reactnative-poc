//
//  EHIInvoiceSublistViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 7/20/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIReservationSublistSection.h"

typedef NS_ENUM(NSInteger, EHIInvoiceSublistType) {
    EHIInvoiceSublistTypePriceDetails,
    EHIInvoiceSublistTypeAdditionalInfo
};

typedef NS_ENUM(NSInteger, EHIInvoiceSublistSection) {
    EHIInvoiceSublistPriceDetails,
    EHIInvoiceSublistRenterDetails,
    EHIInvoiceSublistVehicleDetails,
    EHIInvoiceSublistDistance
};

@interface EHIInvoiceSublistViewModel : EHIViewModel <MTRReactive>
- (instancetype)initWithModel:(id)model type:(EHIInvoiceSublistType)type;
@property (strong, nonatomic) NSArray *sections;
@end
