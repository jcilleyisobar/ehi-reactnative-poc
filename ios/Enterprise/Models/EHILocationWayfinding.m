//
//  EHILocationWayfinding.m
//  Enterprise
//
//  Created by Pawel Bragoszewski on 02.03.2015.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel_Subclass.h"
#import "EHILocationWayfinding.h"

@implementation EHILocationWayfinding

- (void)parseDictionary:(NSMutableDictionary *)dictionary
{
    [super parseDictionary:dictionary];

    // attempt to remove html tags from the text
    [dictionary ehi_transform:@key(self.text) selector:@selector(ehi_stripHtml)];
}

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHILocationWayfinding *)model
{
    return @{
        @"path" : @key(model.iconUrl)
    };
}

- (NSDictionary *)encodeForWatch
{
    EHILocationWayfinding *wayfinding;
    
    return @{
            @key(wayfinding.text) : self.text ?: @"",
            @key(wayfinding.iconUrl) : self.iconUrl ?: @""
             };
}

@end
