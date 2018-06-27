//
//  EHIItineraryReturnLocationViewModel.h
//  Enterprise
//
//  Created by mplace on 3/11/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIReservationBuilder.h"

@interface EHIItineraryReturnLocationViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic) NSString *returnLocationTitle;
@property (copy  , nonatomic) NSString *alternateReturnLocationButtonTitle;
@property (copy  , nonatomic, readonly) NSString *iconImageName;
@property (assign, nonatomic) BOOL showsReturnLocation;
@property (assign, nonatomic) BOOL shouldHideIcon;
@property (assign, nonatomic) BOOL shouldHideLock;

- (void)findReturnLocation;
- (void)clearReturnLocation;

@end
