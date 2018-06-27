//
//  EHIDashboardSearchViewModel.h
//  Enterprise
//
//  Created by Ty Cobb on 1/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIDashboardSearchViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic) NSString *title;
@property (copy  , nonatomic) NSString *placeholder;

- (void)searchNearby;
- (void)searchLocations;

@end
