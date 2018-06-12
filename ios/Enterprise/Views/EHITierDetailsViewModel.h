//
//  EHITierDetailsViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 1/19/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHITierItemViewModel.h"
#import "EHILoyaltyTierDataProvider.h"

@interface EHITierDetailsViewModel : EHIViewModel <MTRReactive>

- (instancetype)initWithTier:(EHIUserLoyaltyTier)tier;

@property (assign, nonatomic, readonly) EHIUserLoyaltyTier tier;

@property (copy  , nonatomic, readonly) NSString *goalTitle;
@property (strong, nonatomic, readonly) EHITierItemViewModel *goalsModel;

@property (copy  , nonatomic, readonly) NSString *benefitsTitle;
@property (strong, nonatomic, readonly) EHITierItemViewModel *benefitsModel;

@end
