//
//  CLLocation+Utility.m
//  Enterprise
//
//  Created by Ty Cobb on 3/11/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "CLLocation+Utility.h"

@implementation CLLocation (Utility)

- (instancetype)initWithCoordinate:(CLLocationCoordinate2D)coordinate
{
    return [self initWithLatitude:coordinate.latitude longitude:coordinate.longitude];
}

@end
