//
//  EHIItineraryPickupLocationViewModel.h
//  Enterprise
//
//  Created by mplace on 3/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIItineraryPickupLocationViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic) NSString *title;
@property (copy  , nonatomic, readonly) NSString *iconImageName;
@property (assign, nonatomic) BOOL shouldHideIcon;
@property (assign, nonatomic) BOOL shouldHideLockIcon;

/** Navigates to the location search screen with the pickup location search type*/
- (void)searchForPickupLocation;
@end
