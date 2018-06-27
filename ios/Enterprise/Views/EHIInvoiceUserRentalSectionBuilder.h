//
//  EHIInvoiceUserRentalSectionBuilder.h
//  Enterprise
//
//  Created by Rafael Ramos on 7/21/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIUserRental.h"
#import "EHIInvoiceSublistItemViewModel.h"

typedef NS_ENUM(NSInteger, EHIInvoiceUserRentalSection) {
    EHIInvoiceUserRentalSectionPriceDetails,
    EHIInvoiceUserRentalSectionRenter,
    EHIInvoiceUserRentalSectionVehicleDetails,
    EHIInvoiceUserRentalSectionDistance
};

@interface EHIInvoiceUserRentalSectionBuilder : NSObject
+ (NSArray *)modelSection:(EHIInvoiceUserRentalSection)section rental:(EHIUserRental *)rental;
+ (NSString *)titleForSection:(EHIInvoiceUserRentalSection)section;
@end
