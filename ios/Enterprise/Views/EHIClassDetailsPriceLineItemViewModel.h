//
//  EHIClassDetailsPriceLineItemViewModel.h
//  Enterprise
//
//  Created by mplace on 3/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIClassDetailsPriceLineItemViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic) NSString *title;
@property (copy  , nonatomic) NSString *accessoryTitle;
@property (copy  , nonatomic) NSString *rateString;
@property (assign, nonatomic) BOOL hideIcon;
@property (assign, nonatomic) BOOL hasDetails;
@end
