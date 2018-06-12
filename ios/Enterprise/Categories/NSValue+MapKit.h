//
//  NSValue+MapKit.h
//  Enterprise
//
//  Created by Ty Cobb on 1/30/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@import MapKit;

@interface NSValue (MapKit)

+ (instancetype)valueWithMKCoordinateRegion:(MKCoordinateRegion)region;
- (MKCoordinateRegion)MKCoordinateRegionValue;

@end
