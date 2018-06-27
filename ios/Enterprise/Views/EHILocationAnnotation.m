//
//  EHILocationAnnotation.m
//  Enterprise
//
//  Created by Ty Cobb on 2/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationAnnotation.h"
#import "EHIMapping.h"
#import "EHILocationMapPinAssetFactory.h"

@implementation EHILocationAnnotation

- (instancetype)initWithLocation:(EHILocation *)location
{
    if(self = [super init]) {
        _location = location;
    }
    
    return self;
}

# pragma mark - Accessors

- (CLLocationCoordinate2D)coordinate
{
    return (CLLocationCoordinate2D) {
        .latitude  = self.location.position.latitude  + self.offset.latitude,
        .longitude = self.location.position.longitude + self.offset.longitude,
    };
}

- (NSString *)imageName
{
    return [EHILocationMapPinAssetFactory assetForLocation:self.location];
}

- (NSString *)selectedImageName
{
    return [EHILocationMapPinAssetFactory assetForLocation:self.location selected:YES];
}

- (NSComparisonResult)compare:(EHILocationAnnotation *)target
{
    if(self.location.hasConflicts && !target.location.hasConflicts) {
        return NSOrderedDescending;
    }
    
    return NSOrderedSame;
}

# pragma mark - Debugging

- (NSString *)description
{
    return [NSString stringWithFormat:@"<%@: %p; loc.id: %@; pos: {%0.2f, %02.f}>",
        self.class, self, self.location.uid, self.location.position.latitude, self.location.position.longitude];
}

@end
