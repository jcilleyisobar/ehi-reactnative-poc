//
//  EHIReservationPoliciesViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 4/9/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIReservation.h"

@interface EHIReservationPoliciesViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic) NSString *headerText;
@property (copy  , nonatomic) NSString *headerDetails;
@property (copy  , nonatomic) NSString *keyFactsButtonText;
@property (copy  , nonatomic) NSAttributedString *keyFactsDetails;
@property (copy  , nonatomic) NSString *policyButtonText;
@property (copy  , nonatomic) NSString *policyDetails;
@property (assign, nonatomic) BOOL hideFancyDivider;

@property (assign, readonly, nonatomic) BOOL shouldHideKeyFacts;
@property (assign, readonly, nonatomic) BOOL shouldHidePolicies;

- (void)showKeyFacts;
- (void)showPolicies;

@end
