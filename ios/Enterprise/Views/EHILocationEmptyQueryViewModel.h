//
//  EHILocationEmptyQueryViewModel.h
//  Enterprise
//
//  Created by mplace on 2/10/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHILocationEmptyQueryViewModel : EHIViewModel <MTRReactive>

@property (copy, nonatomic, readonly) NSString *title;
@property (copy, nonatomic, readonly) NSString *subtitle;
@property (copy, nonatomic, readonly) NSString *callButtonTitle;
@property (copy, nonatomic, readonly) NSString *nearbyButtonTitle;

- (void)callHelpNumber;
- (void)findNearbyLocations;

@end
