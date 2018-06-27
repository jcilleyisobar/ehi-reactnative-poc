//
//  EHIReservationSublistSectionFooter.m
//  Enterprise
//
//  Created by Ty Cobb on 4/27/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationSublistSectionFooter.h"

@interface EHIReservationSublistSectionFooter()
@property (weak, nonatomic) IBOutlet UIView *divider;
@end

@implementation EHIReservationSublistSectionFooter

- (void)updateWithModel:(id)model metrics:(EHILayoutMetrics *)metrics
{
    [super updateWithModel:model metrics:metrics];
    
    self.backgroundColor = metrics.backgroundColor;
    self.divider.backgroundColor = metrics.primaryColor;
}

# pragma mark - Layout

+ (EHILayoutMetrics *)defaultMetrics
{
    EHILayoutMetrics *metrics = [super defaultMetrics];
    metrics.fixedSize = (CGSize) { .width = EHILayoutValueNil, .height = EHIMediumPadding + 1.0f };
    return metrics;
}

@end
