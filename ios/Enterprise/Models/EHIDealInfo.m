//
//  EHIDealInfo.m
//  Enterprise
//
//  Created by Rafael Ramos on 06/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIDealInfo.h"
#import "EHIModel_Subclass.h"

@implementation EHIDealInfo

+ (NSDictionary *)mappings:(EHIDealInfo *)model
{
    return @{
        @"long_title"           : @key(model.longTitle),
        @"short_title"          : @key(model.shortTitle),
        @"long_description"     : @key(model.longDescription),
        @"small_description"    : @key(model.smallDescription),
        @"terms_and_conditions" : @key(model.terms),
        @"image"                : @key(model.image),
    };
}

# pragma mark - EHIPromotionRenderable

- (NSString *)title
{
    return self.shortTitle;
}

- (NSString *)subtitle
{
    return self.smallDescription;
}

- (EHIImage *)imageModel
{
    return self.image;
}

@end
