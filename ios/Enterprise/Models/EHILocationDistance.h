//
//  EHILocationDistance.h
//  Enterprise
//
//  Created by Ty Cobb on 1/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"

@interface EHILocationDistance : EHIModel
@property (assign, nonatomic, readonly) double distance;
@property (copy  , nonatomic, readonly) NSString *unit;
@end

EHIAnnotatable(EHILocationDistance)
