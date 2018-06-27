//
//  EHIUserLoyaltyGoal.h
//  Enterprise
//
//  Created by mplace on 2/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHILoyaltyTierDataProvider.h"

@interface EHIUserLoyaltyGoal : EHIModel
@property (assign, nonatomic, readonly) NSInteger remainingRentals;
@property (assign, nonatomic, readonly) NSInteger remainingRentalDays;
@property (assign, nonatomic, readonly) NSInteger nextTierRentals;
@property (assign, nonatomic, readonly) NSInteger nextTierRentalDays;
@property (assign, nonatomic, readonly) EHIUserLoyaltyTier tier;
@end
