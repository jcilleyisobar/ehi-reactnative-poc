//
//  EHIReviewLocationsViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 4/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIReviewLocationsViewModel : EHIViewModel <MTRReactive>
/** Title for the pickup section */
@property (copy  , nonatomic) NSString *pickupSectionTitle;
/** Title for the pickup location container */
@property (copy  , nonatomic) NSString *pickupTitle;
/** Icon name for the pickup location container */
@property (copy  , nonatomic) NSString *pickupIconImageName;

/** @c YES if the return location container should be hidden */
@property (assign, nonatomic) BOOL showsReturn;
/** Title for the return section */
@property (copy  , nonatomic) NSString *returnSectionTitle;
/** Title for the return location container */
@property (copy  , nonatomic) NSString *returnTitle;
/** Icon name for the return location container */
@property (copy  , nonatomic) NSString *returnIconImageName;

@property (assign, nonatomic) BOOL shouldHideLockIcon;

/** Prompts for editing pickup location */
- (void)selectPickupLocation;
/** Prompts for editing return location */
- (void)selectReturnLocation;

@end
