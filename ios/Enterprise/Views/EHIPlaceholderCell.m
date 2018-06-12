//
//  EHIPlaceholderCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 8/12/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPlaceholderCell.h"

@implementation EHIPlaceholderCell

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = 0.5f
    };
}

+ (EHILayoutMetrics *)defaultMetrics
{
    EHILayoutMetrics *metrics = [super defaultMetrics];
    metrics.fixedSize = (CGSize) {
        .width  = EHILayoutValueNil,
        .height = 0.5f
    };
    
    return metrics;
}

@end
