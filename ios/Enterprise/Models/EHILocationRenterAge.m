//
//  EHILocationRenterAge.m
//  Enterprise
//
//  Created by cgross on 7/9/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationRenterAge.h"
#import "EHIModel_Subclass.h"

@implementation EHILocationRenterAge

- (void)parseDictionary:(NSMutableDictionary *)dictionary
{
    [super parseDictionary:dictionary];
    
    [dictionary ehi_transform:@key(self.text) block:^(NSString *text) {
        return [text ehi_applyReplacementMap:@{
            @"age_or_older" : EHILocalizedString(@"age_or_older_label", @"or older", @"Age options token replacement for older"),
            @"age_to" : EHILocalizedString(@"age_to_label", @"to", @"Age options token replacement for to")
        }];
    }];
}

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHILocationRenterAge *)model
{
    return @{
        @"label"    : @key(model.text),
        @"selected" : @key(model.isDefault)
    };
}

@end
