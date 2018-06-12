//
//  EHIMockUserAnnotation.h
//  Enterprise
//
//  Created by Alex Koller on 12/17/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

@import MapKit;

@interface EHIMockUserAnnotation : NSObject <MKAnnotation>

@property (assign, nonatomic, readonly) CLLocationCoordinate2D coordinate;

- (instancetype)initWithCoordinate:(CLLocationCoordinate2D)coordinate;

@end
