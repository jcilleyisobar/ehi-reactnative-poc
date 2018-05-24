//
//  EHIRedemptionTotalViewModel.h
//  Enterprise
//
//  Created by mplace on 8/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIRedemptionTotalViewModel : EHIViewModel <MTRReactive>
/** Title for the cell */
@property (copy, nonatomic, readonly) NSString *title;
/** Action button title */
@property (copy, nonatomic) NSString *actionTitle;
/** Value title for the cell */
@property (copy, nonatomic) NSAttributedString *value;
/** @c YES if the cell is in the selected state */
@property (assign, nonatomic) BOOL showsLineItems;
@end
