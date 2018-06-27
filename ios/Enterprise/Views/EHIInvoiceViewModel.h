//
//  EHIInvoiceViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 7/20/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIInvoiceRentalInfoViewModel.h"
#import "EHIInvoiceFooterViewModel.h"
#import "EHIInvoiceSublistViewModel.h"
#import "EHIInvoiceTripSummaryViewModel.h"
#import "EHIInvoiceSectionHeaderViewModel.h"
#import "EHIReservationRentalPriceTotalViewModel.h"
#import "EHICurrencyDiffersViewModel.h"

typedef NS_ENUM(NSInteger, EHIInvoiceSection) {
    EHIInvoiceSectionCurrencyDiffers,
    EHIInvoiceSectionRental,
    EHIInvoiceSectionTripSummary,
    EHIInvoiceSectionPriceDetails,
    EHIInvoiceSectionEstimatedTotal,
    EHIInvoiceSectionAdditionalInformation,
    EHIInvoiceSectionFooter
};

@class EHIUserRental;
@interface EHIInvoiceViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic, readonly) NSString *title;
@property (strong, nonatomic) EHICurrencyDiffersViewModel *currencyModel;
@property (strong, nonatomic) EHIInvoiceRentalInfoViewModel *rentalInfo;
@property (strong, nonatomic) EHIInvoiceTripSummaryViewModel *tripSummary;
@property (strong, nonatomic) EHIInvoiceSublistViewModel *priceDetails;
@property (strong, nonatomic) EHIReservationRentalPriceTotalViewModel *estimatedTotal;
@property (strong, nonatomic) EHIInvoiceSublistViewModel *sublistModel;
@property (strong, nonatomic) EHIInvoiceFooterViewModel *footerModel;
@property (assign, nonatomic) BOOL isLoading;

- (instancetype)initFetchingRental:(EHIUserRental *)rental;

- (EHIInvoiceSectionHeaderViewModel *)headerTitleForSection:(EHIInvoiceSection)section;
- (EHIModel *)dividerModelForSection:(EHIInvoiceSection)section;

- (void)saveRentalAsPhoto;
- (void)dismiss;

@end
