//
//  EHICarClassCategory.m
//  Enterprise
//
//  Created by mplace on 2/19/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICarClassCategory.h"

#define EHICarClassCategoryVan  @"500"

@implementation EHICarClassCategory

- (BOOL)isVan
{
    return [self.code isEqualToString:EHICarClassCategoryVan];
}

@end
