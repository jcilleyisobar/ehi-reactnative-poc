//
//  EHIAirline.h
//  Enterprise
//
//  Created by Alex Koller on 6/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"

#define EHIAirlineOtherCode  @"XX"
#define EHIAirlineWalkInCode @"WU"

@interface EHIAirline : EHIModel

@property (copy, nonatomic, readonly) NSString *code;
@property (copy, nonatomic, readonly) NSString *details;

// set by user
@property (copy, nonatomic, readonly) NSString *flightNumber;

- (BOOL)isOther;
- (BOOL)isWalkIn;

@end

EHIAnnotatable(EHIAirline);