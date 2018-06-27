//
//  EHIReservationFeeViewModel.h
//  Enterprise
//
//  Created by Ty Cobb on 4/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIReservationFeeViewModel : EHIViewModel <MTRReactive>

/** @c YES if this cell is selected */
@property (assign, nonatomic) BOOL isSelected;
/** The title for this fee */
@property (copy, nonatomic, readonly) NSString *title;
/** The price text for this fee */
@property (copy, nonatomic, readonly) NSString *priceText;
/** The details text for this fee */
@property (copy, nonatomic, readonly) NSAttributedString *detailsText;
/** @c YES if this is the learn more view model */
@property (assign, nonatomic, readonly) BOOL isLearnMore;

/** Generates a view model for the "Learn More" link */
+ (EHIReservationFeeViewModel *)learnMoreViewModel;

@end
