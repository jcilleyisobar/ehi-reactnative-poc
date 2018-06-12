//
//  EHILocations.h
//  Enterprise
//
//  Created by Ty Cobb on 1/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICity.h"

@interface EHILocations : EHIModel
@property (strong, nonatomic, readonly) NSArray<EHICity> *cities;
@property (strong, nonatomic, readonly) NSArray<EHILocation> *airports;
@property (strong, nonatomic, readonly) NSArray<EHILocation> *branches;
// computed properties
@property (nonatomic, readonly) NSArray *all;
@end
