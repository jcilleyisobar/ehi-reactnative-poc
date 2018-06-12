//
//  EHIReservationPriceSublistViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 7/14/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIReservation.h"
#import "EHIUserRental.h"
#import "EHIPlacardViewModel.h"
#import "EHIReservationPriceMileageViewModel.h"
#import "EHIReservationPriceViewModel.h"

typedef NS_ENUM(NSInteger, EHIReservationPriceSublistSection) {
    EHIReservationPriceSublistMileage,
    EHIReservationPriceSublistRate,
    EHIReservationPriceSublistRental,
    EHIReservationPriceSublistRentalItems,
    EHIReservationPriceSublistAdjustment,
    EHIReservationPriceSublistAdjustmentItems,
    EHIReservationPriceSublistExtras,
    EHIReservationPriceSublistExtrasItems,
    EHIReservationPriceSublistTaxesFees,
    EHIReservationPriceSublistTaxesFeesItems
};

typedef NS_ENUM(NSInteger, EHIReservationPriceSublistSectionState) {
    EHIReservationPriceSublistSectionStateCollapsed,
    EHIReservationPriceSublistSectionStateExpanded,
};

@interface EHIReservationPriceSublistViewModel : EHIViewModel <MTRReactive>

- (instancetype)initWithCarClass:(EHICarClass *)carClass prepay:(BOOL)prepay;
- (instancetype)initWithRental:(EHIUserRental *)rental;

@property (strong, nonatomic) EHIPlacardViewModel *placard;
@property (strong, nonatomic) EHICarClass *carClass;
@property (strong, nonatomic) EHIReservationPriceMileageViewModel *mileageModel;

@property (strong, nonatomic, readonly) NSArray *modelsForRentalItems;
@property (strong, nonatomic, readonly) NSArray *modelsForAdjustmentItems;
@property (strong, nonatomic, readonly) NSArray *modelsForExtrasItems;
@property (strong, nonatomic, readonly) NSArray *modelsForTaxesFeesItems;

- (NSArray *)itemsForSection:(EHIReservationPriceSublistSection)section;
- (EHIReservationPriceViewModel *)modelForPriceSection:(EHIReservationPriceSublistSection)section;
- (void)expandCollapseSection:(EHIReservationPriceSublistSection)section;

@end
