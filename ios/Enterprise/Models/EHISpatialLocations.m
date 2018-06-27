//
//  EHISpatialLocations.m
//  Enterprise
//
//  Created by mplace on 3/5/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel_Subclass.h"
#import "EHISpatialLocations.h"

@implementation EHISpatialLocations

- (void)parseDictionary:(NSMutableDictionary *)dictionary
{
    [super parseDictionary:dictionary];
   
    // transform the solr dictionaries into a form EHILocation will understand
    [dictionary ehi_transform:@key(self.locations) block:^(NSArray *dictionaries) {
        return [EHILocation processSolrDictionaries:dictionaries];
    }];
}

# pragma mark - Accessors

- (BOOL)hasOffbrandLocations
{
    // validate that we have at least one offbrand location (cannot mix enterprise and offbrand)
    return (self.brands ?: @[]).any(^(NSNumber *brand) {
        return brand.integerValue > EHILocationBrandEnterprise;
    });
}

# pragma mark - EHIModel

+ (NSDictionary *)mappings:(EHISpatialLocations *)model
{
    return @{
        @"radiusUsedInKilometers" : @key(model.radius),
        @"brandsInResult"         : @key(model.brands),
        @"locationsResult"        : @key(model.locations)
    };
}

+ (void)registerTransformers:(EHISpatialLocations *)model
{
    [self key:@key(model.brands) registerTransformer:EHILocationBrandTransformer()];
}

@end
