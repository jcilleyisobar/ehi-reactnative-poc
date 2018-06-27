//
//  EHISectionDivider.m
//  Enterprise
//
//  Created by Rafael Ramos on 7/26/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHISectionDivider.h"

@interface EHISectionDivider ()
@end

@implementation EHISectionDivider

# pragma mark - Layout

+ (EHILayoutMetrics *)defaultMetrics
{
    EHILayoutMetrics *metrics = [super defaultMetrics];
    metrics.fixedSize = (CGSize){ .width = EHILayoutValueNil, .height = 15.0f };
    
    return metrics;
}

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width = EHILayoutValueNil,
        .height = 15.0f
    };
}

@end
