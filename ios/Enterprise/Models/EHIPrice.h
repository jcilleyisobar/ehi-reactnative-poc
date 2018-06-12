//
//  EHIPrice.h
//  Enterprise
//
//  Created by Ty Cobb on 4/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"

static NSString *const EHICurrencyCodeUSA = @"USD";
static NSString *const EHICurrencyCodeCA  = @"CAD";

@interface EHIPrice : EHIModel
@property (assign, nonatomic, readonly) CGFloat amount;
@property (copy  , nonatomic, readonly) NSString *code;
@property (copy  , nonatomic, readonly) NSString *symbol;
@end
