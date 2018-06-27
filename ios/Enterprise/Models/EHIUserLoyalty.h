//
//  EHIUserLoyalty.h
//  Enterprise
//
//  Created by mplace on 2/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHIUserLoyaltyGoal.h"

typedef NS_ENUM(NSInteger, EHIUserLoyaltyProgram) {
    EHIUserLoyaltyProgramUnknow,
    EHIUserLoyaltyProgramEnterprisePlus,
    EHIUserLoyaltyProgramEmeraldClub,
    EHIUserLoyaltyProgramQuickSilver,
    EHIUserLoyaltyProgramNonLoyalty
};

@interface EHIUserLoyalty : EHIModel
@property (copy  , nonatomic, readonly) NSString *number;
@property (assign, nonatomic, readonly) NSInteger pointsToDate;
@property (assign, nonatomic, readonly) EHIUserLoyaltyTier tier;
@property (assign, nonatomic, readonly) EHIUserLoyaltyProgram program;
@property (strong, nonatomic, readonly) EHIUserLoyaltyGoal *goal;

@end
