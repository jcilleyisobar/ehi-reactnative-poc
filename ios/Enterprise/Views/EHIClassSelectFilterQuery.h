//
//  EHIClassSelectFilterQuery.h
//  Enterprise
//
//  Created by mplace on 4/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHIFilters.h"
#import "EHIAnalyticsEncodable.h"

@interface EHIClassSelectFilterQuery : EHIModel <EHIAnalyticsEncodable>

/** Master list of car classes */
@property (copy, nonatomic) NSArray *carClasses;
/** Master list of car classes */
@property (copy, nonatomic) NSArray *filteredCarClasses;
/** Vehicle type filters to apply */
@property (copy, nonatomic) NSArray *vehicleTypeFilters;
/** Vehicle feature filters to apply */
@property (copy, nonatomic) NSArray *vehicleFeatureFilters;

- (NSArray *)activeFilters;

@end
