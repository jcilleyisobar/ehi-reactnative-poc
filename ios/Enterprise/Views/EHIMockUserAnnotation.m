//
//  EHIMockUserAnnotation.m
//  Enterprise
//
//  Created by Alex Koller on 12/17/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIMockUserAnnotation.h"

@interface EHIMockUserAnnotation ()
@property (assign, nonatomic) CLLocationCoordinate2D coordinate;
@end

@implementation EHIMockUserAnnotation

- (instancetype)initWithCoordinate:(CLLocationCoordinate2D)coordinate
{
    if(self = [super init]) {
        _coordinate = coordinate;
    }
    
    return self;
}

@end
