//
//  EHIReservationRentalPriceTotalViewModel.h
//  Enterprise
//
//  Created by mplace on 3/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

typedef NS_ENUM(NSInteger, EHIReservationRentalPriceTotalLayout) {
    EHIReservationRentalPriceTotalLayoutReview,
    EHIReservationRentalPriceTotalLayoutInvoice,
    EHIReservationRentalPriceTotalLayoutUnpaidRefund
};

@interface EHIReservationRentalPriceTotalViewModel : EHIViewModel <MTRReactive>
@property (assign, nonatomic, readonly) EHIReservationRentalPriceTotalLayout layout;
@property (assign, nonatomic, readonly) BOOL showTopDivider;
/** Title for the price total section */
@property (copy  , nonatomic, readonly) NSString *totalTitle;
/** Title for the updated total label */
@property (copy  , nonatomic, readonly) NSString *updatedTotalTitle;
/** Label for the updated total label */
@property (copy  , nonatomic, readonly) NSAttributedString *updatedTotalLabel;
/** Title for the paid amount label */
@property (copy  , nonatomic, readonly) NSString *paidAmountTitle;
/** Label for the paid amount label */
@property (copy  , nonatomic, readonly) NSAttributedString *paidAmountLabel;
/** Title for the original total label */
@property (copy  , nonatomic, readonly) NSString *originalTotal;
/** Title for the original total label */
@property (copy  , nonatomic, readonly) NSString *endOfRental;
/** Title for the original total label */
@property (copy  , nonatomic, readonly) NSString *actualAmount;
/** Title for the transparency section */
@property (copy  , nonatomic) NSString *transparencyTitle;
/** Total string for the price total section */
@property (copy  , nonatomic) NSAttributedString *total;
/** Transparency string for the transparency section */
@property (copy  , nonatomic) NSAttributedString *transparency;
/** @YES if the transparency section should be shown */
@property (assign, nonatomic) BOOL showsTransparency;
/** @YES if the payment option button should be visible */
@property (assign, nonatomic) BOOL showOtherPaymentOption;
/** @YES if the expanded total payment cell be visible */
@property (copy  , nonatomic) NSString *otherPaymentOptionTotal;

- (instancetype)initWithModel:(id)model
			   prepaySelected:(BOOL)prepaySelected
				   paidAmount:(EHIPrice *)paidAmount
				 actualAmount:(EHIPrice *)actualAmount
			  showOtherOption:(BOOL)showOption
					   layout:(EHIReservationRentalPriceTotalLayout)layout
                 isSecretRate:(BOOL)isSecretRate;

- (void)didTapChangePayment;

@end
