//
//  EHIRedemptionLineItemsViewModel.h
//  Enterprise
//
//  Created by fhu on 8/12/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIRedemptionLineItemsViewModel : EHIViewModel <MTRReactive>
@property (copy, nonatomic, readonly) NSString *redeemingTitle;
@property (copy, nonatomic, readonly) NSString *creditTitle;
@property (copy, nonatomic, readonly) NSString *pointsTitle;
@property (copy, nonatomic) NSString *redeemingDays;
@property (copy, nonatomic) NSString *points;
@property (copy, nonatomic) NSAttributedString *credits;
@end
