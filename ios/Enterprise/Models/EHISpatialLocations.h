//
//  EHISpatialLocations.h
//  Enterprise
//
//  Created by mplace on 3/5/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHILocation.h"

@interface EHISpatialLocations : EHIModel
@property (copy  , nonatomic, readonly) NSArray *brands;
@property (copy  , nonatomic, readonly) NSArray<EHILocation> *locations;
/** Returns the final search radius in kilometers */
@property (assign, nonatomic, readonly) NSInteger radius;
@property (assign, nonatomic, readonly) BOOL hasOffbrandLocations;
@end
