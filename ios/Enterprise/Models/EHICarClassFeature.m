//
//  EHICarClassFeature.m
//  Enterprise
//
//  Created by mplace on 6/5/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel_Subclass.h"
#import "EHICarClassFeature.h"

@implementation EHICarClassFeature

- (void)parseDictionary:(NSMutableDictionary *)dictionary
{
    [super parseDictionary:dictionary];

    [dictionary ehi_transform:@key(self.code) block:^(NSString *code) {
        return @([code integerValue]);
    }];
}

@end
