//
//  CLLocation+Utility.h
//  Enterprise
//
//  Created by Ty Cobb on 3/11/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import <CoreLocation/CoreLocation.h>

@interface CLLocation (Utility)
/** Convenience initializer that calls through to @c -initWithLatitude:longitude: */
- (instancetype)initWithCoordinate:(CLLocationCoordinate2D)coordinate;
@end
