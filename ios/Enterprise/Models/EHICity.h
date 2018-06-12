//
//  EHILocationsCity.h
//  Enterprise
//
//  Created by Ty Cobb on 1/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocation.h"
#import "EHILocationCoordinate.h"

@interface EHICity : EHIModel <EHIAnalyticsEncodable>
@property (copy  , nonatomic, readonly) NSString *formattedName;
@property (strong, nonatomic, readonly) EHILocationCoordinate *position;
@end

EHIAnnotatable(EHICity);