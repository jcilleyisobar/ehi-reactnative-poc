//
//  EHIDebugMapViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 12/10/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

@import MapKit;

#import "EHIViewModel.h"
#import "EHIMockUserAnnotation.h"
#import "NSValue+MapKit.h"

@interface EHIDebugMapViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic) NSString *title;
@property (copy  , nonatomic) NSValue *region;
@property (strong, nonatomic) EHIMockUserAnnotation *mockUserLocation;
@property (copy  , nonatomic) NSArray *overlays;
@property (assign, nonatomic) BOOL isMockingLocations;

- (void)testLocations;

@end
