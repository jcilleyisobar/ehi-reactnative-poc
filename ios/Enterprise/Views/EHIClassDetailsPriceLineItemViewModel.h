//
//  EHIClassDetailsPriceLineItemViewModel.h
//  Enterprise
//
//  Created by mplace on 3/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIClassDetailsPriceLineItemViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic) NSAttributedString *titleAndRate;
@property (copy  , nonatomic) NSString *accessoryTitle;
@property (assign, nonatomic) BOOL hideIcon;
@property (assign, nonatomic) BOOL hasDetails;
@end
