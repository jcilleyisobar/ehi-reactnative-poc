//
//  EHILocationsCity.m
//  Enterprise
//
//  Created by Ty Cobb on 1/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICity.h"
#import "EHIModel_Subclass.h"

@interface EHICity ()
@property (copy, nonatomic, readonly) NSString *name;
@end

@implementation EHICity

# pragma mark - EHIModel

- (void)parseDictionary:(NSMutableDictionary *)dictionary
{
    [super parseDictionary:dictionary];
    
    // nest the position
    [dictionary ehi_nest:@key(self.position) fields:@[
        @"latitude", @"longitude"
    ]];
}

+ (NSDictionary *)mappings:(EHICity *)model
{
    return @{
        @"cityId"    : @key(model.uid),
        @"shortName" : @key(model.name),
        @"longName"  : @key(model.formattedName),
    };
}

+ (void)prepareCollection:(EHICollection *)collection
{
    // save the last 5 items
    collection.historyLimit = 5;
}

# pragma mark - EHIAnalyticsEncodable

+ (void)encodeWithContext:(EHIAnalyticsContext *)context instance:(nullable EHICity *)instance
{
    context[EHIAnalyticsLocSearchAreaKey] = instance.name;
}

@end
