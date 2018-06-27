//
//  EHIRedemptionSavingsViewModel.h
//  Enterprise
//
//  Created by mplace on 8/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIRedemptionSavingsViewModel : EHIViewModel <MTRReactive>
/** Title for the cell */
@property (copy, nonatomic) NSString *title;
/** Subtitle for the cell */
@property (copy, nonatomic) NSString *subtitle;
/** Savings value string for the cell */
@property (copy, nonatomic) NSString *value;
@end
