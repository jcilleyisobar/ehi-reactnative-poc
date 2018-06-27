//
//  EHIReservationPriceViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 7/14/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIPrice.h"

typedef void (^EHIReservationPriceExpansionHandler)(BOOL expanded);

typedef NS_ENUM(NSInteger, EHIReservationPriceLineSection) {
    EHIReservationPriceLineSectionRental,
    EHIReservationPriceLineSectionMiscellaneous,
    EHIReservationPriceLineSectionExtras,
    EHIReservationPriceLineSectionTaxes
};

@class EHICarClassVehicleRate;
@interface EHIReservationPriceViewModel : EHIViewModel <MTRReactive>

- (instancetype)initWithTitle:(NSString *)title total:(NSString *)total;

@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) NSString *total;

@end
