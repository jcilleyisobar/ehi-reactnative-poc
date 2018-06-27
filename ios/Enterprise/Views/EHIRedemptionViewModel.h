//
//  EHIRedemptionViewModel.h
//  Enterprise
//
//  Created by mplace on 8/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationStepViewModel.h"
#import "EHIReservationPriceButtonType.h"
#import "EHIPriceContext.h"

typedef NS_ENUM(NSUInteger, EHIRedemptionSection) {
    EHIRedemptionSectionHeader,
    EHIRedemptionSectionPointsPicker,
    EHIRedemptionSectionTotal,
    EHIRedemptionSectionLineItems,
    EHIRedemptionSectionSavings
};

@interface EHIRedemptionViewModel : EHIReservationStepViewModel <MTRReactive>
/** Title for the screen */
@property (copy, nonatomic, readonly) NSString *title;
/** Title for the footer */
@property (copy, nonatomic, readonly) NSString *footerTitle;
/** Subtitle type for the footer button */
@property (assign, nonatomic) EHIReservationPriceButtonSubtitleType footerSubtitleType;
/** Model for the header section */
@property (strong, nonatomic) EHIViewModel *headerModel;
/** Model for the points picker section */
@property (strong, nonatomic) EHIModel *pointsModel;
/** Model for the total section */
@property (strong, nonatomic) id totalModel;
/** Model for the confirmation footer */
@property (strong, nonatomic) EHIModel *footerModel;
/** Model for the line items section */
@property (strong, nonatomic) NSArray *lineItemsModel;
/** Model for the savings section */
@property (strong, nonatomic) EHIModel *savingsModel;
/** @c YES if there is an active service call */
@property (assign, nonatomic) BOOL isLoading;
/** @c YES if in the process of committing the redemption selections */
@property (assign, nonatomic) BOOL isCommitting;
/** @c YES if this is in the middle of the reservation flow and the extras screen is next */
@property (assign, nonatomic) BOOL shouldGotoExtrasWhenDone;

/** Toggles the line items section visibility */
- (void)toggleLineItems;
/** Finalizes the number of days redeemed and navigates to the review screen */
- (void)commitRedemption;
/** Fetches new car class information based on the days the user has chosen to redeem */
- (void)updateReservationWithDaysRedeemed;
/** Selects the line item, transitioning to the correct view */
- (void)selectLineItemAtIndexPath:(NSIndexPath *)indexPath;
@end
