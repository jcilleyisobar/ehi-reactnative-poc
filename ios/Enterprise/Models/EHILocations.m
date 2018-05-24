//
//  EHILocations.m
//  Enterprise
//
//  Created by Ty Cobb on 1/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel_Subclass.h"
#import "EHILocations.h"

@implementation EHILocations

- (void)parseDictionary:(NSMutableDictionary *)dictionary
{
    [super parseDictionary:dictionary];

    // transform the solr dictionaries into a form EHILocation will understand
    [dictionary ehi_transform:@key(self.airports) block:^(NSArray *dictionaries) {
        return [EHILocation processSolrDictionaries:dictionaries];
    }];
    
    [dictionary ehi_transform:@key(self.branches) block:^(NSArray *dictionaries) {
        return [EHILocation processSolrDictionaries:dictionaries];
    }];
}

# pragma mark - Accessors

- (NSArray *)all
{
    return @[].concat(self.airports).concat(self.branches);
}

@end
