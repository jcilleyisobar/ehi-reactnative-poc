//
//  EHIRedemptionPickerViewModel.h
//  Enterprise
//
//  Created by mplace on 8/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIRedemptionPickerViewModel : EHIViewModel <MTRReactive>
/** Title of the cell */
@property (copy  , nonatomic, readonly) NSString *title;
/** Subtitle of the cell */
@property (copy  , nonatomic) NSString *subtitle;
/** Title at the bottom of the cell */
@property (copy  , nonatomic) NSAttributedString *footerTitle;
/** @c YES if the stepper's plus button should be enabled */
@property (assign, nonatomic) BOOL plusButtonEnabled;
/** @c YES if the stepper's minus button should be enabled */
@property (assign, nonatomic) BOOL minusButtonEnabled;
/** The number of days to be redeemed */
@property (assign, nonatomic) NSInteger daysRedeemed;

/** The title for the item count stepper based on the parameterized daysRedeemed */
- (NSAttributedString *)stepperTitleForDaysRedeemed:(NSInteger)daysRedeemed;
@end
