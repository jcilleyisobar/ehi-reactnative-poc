//
//  EHIClassDetailsViewModel.h
//  Enterprise
//
//  Created by mplace on 3/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationStepViewModel.h"
#import "EHICarClassViewModel.h"
#import "EHIRedemptionPointsViewModel.h"
#import "EHISectionHeader.h"
#import "EHIReservationRentalPriceTotalViewModel.h"

typedef NS_ENUM(NSUInteger, EHIClassDetailsSection) {
    EHIClassDetailsSectionRedemption,
    EHIClassDetailsSectionCarClass,
    EHIClassDetailsSectionLoading,
    EHIClassDetailsSectionGeneralInfo,
    EHIClassDetailsSectionPriceSummary,
    EHIClassDetailsSectionPriceTotal,
    EHIClassDetailsSectionTermsAndConditions
};

@interface EHIClassDetailsViewModel : EHIReservationStepViewModel <MTRReactive>

/** The title for the details scren */
@property (copy  , nonatomic) NSString *title;
/** The title for 'select class' action button */
@property (copy  , nonatomic) NSString *actionButtonTitle;
/** The car class data model backing this view model */
@property (strong, nonatomic) EHICarClass *carClass;
/** The car class view model */
@property (strong, nonatomic) EHICarClassViewModel *carClassViewModel;
/** The header model for the price line items */
@property (strong, nonatomic) EHISectionHeaderModel *priceHeader;
@property (strong, nonatomic) EHIReservationRentalPriceTotalViewModel *totalPriceViewModel;
/** The charge model for the transparency section */
@property (strong, nonatomic) id<EHIPriceContext> priceContext;
/** The list of visible price line items */
@property (copy  , nonatomic) NSArray *priceLineItems;
/** The redemption model for handling points visibility */
@property (strong, nonatomic) EHIRedemptionPointsViewModel *redemptionModel;
/** Whether car class details fetched has completed */
@property (assign, nonatomic) BOOL hasLoadedCarClassDetails;
/** Reactive property that triggers activity indicator while making a service call */
@property (assign, nonatomic) BOOL isLoading;

@property (strong, nonatomic) EHIModel *termsModel;

/** Selects this as the reservation's car class */
- (void)selectClass;

/** @c YES if the line item is selectable */
- (BOOL)shouldSelectLineItemAtIndexPath:(NSIndexPath *)indexPath;
/** Selects the line item, transitioning to the correct detail view */
- (void)selectLineItemAtIndexPath:(NSIndexPath *)indexPath;

@end
