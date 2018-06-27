//
//  EHICarClassExtras.m
//  Enterprise
//
//  Created by mplace on 2/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICarClassExtras.h"

@implementation EHICarClassExtras

- (NSArray *)all
{
    return @[].concat(self.equipment).concat(self.insurance).concat(self.ancillary).concat(self.fuel);
}

- (NSArray *)selected
{
    return self.all.select(^(EHICarClassExtra *extra) {
        return extra.isSelected;
    });
}

- (NSArray *)selectedByUser
{
    return (self.all ?: @[])
    .select(^(EHICarClassExtra *extra) {
        return extra.isSelected;
    }).select(^(EHICarClassExtra *extra) {
        return extra.isWaived || extra.isOptional;
    });
}

@end
