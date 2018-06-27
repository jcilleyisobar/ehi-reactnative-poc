//
//  EHIGaugeStructure.h
//  Enterprise
//
//  Created by Rafael Ramos on 1/9/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIMeterFillStrategy.h"

typedef struct {
    EHIMeterFillStrategyType fillStrategy;
    NSInteger segments;
    CGColorRef backgroundColor;
    CGColorRef fillColor;
    CGColorRef outlineColor;
} EHIMeterData;

typedef struct {
    NSInteger segments;
    CGFloat lineWidth;
    CGFloat offset;
    CGColorRef segmentColor;
} EHIArcSegmentData;

static EHIArcSegmentData EHIArcSegmentDataNull = {
    .segments     = 0.0f,
    .lineWidth    = 0.0f,
    .offset       = 0.0f,
};
