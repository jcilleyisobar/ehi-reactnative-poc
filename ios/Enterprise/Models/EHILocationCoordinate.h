//
//  EHILocationCoordinate.h
//  Enterprise
//
//  Created by Ty Cobb on 1/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"

@import CoreLocation;

@interface EHILocationCoordinate : EHIModel
@property (assign, nonatomic, readonly) CLLocationDegrees latitude;
@property (assign, nonatomic, readonly) CLLocationDegrees longitude;
// computed properties
@property (nonatomic, readonly) CLLocationCoordinate2D coordinate;
@end

EHIAnnotatable(EHILocationCoordinate)
