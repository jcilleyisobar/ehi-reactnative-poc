//
//  EHIRentalsFallbackViewModel.h
//  Enterprise
//
//  Created by fhu on 4/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIRentalsViewModel.h"

@interface EHIRentalsFallbackViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic) NSString *headerText;
- (instancetype)initWithMode:(EHIRentalsMode)mode;

@end
