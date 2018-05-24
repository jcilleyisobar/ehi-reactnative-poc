//
//  EHIReservationSublistSectionHeader.m
//  Enterprise
//
//  Created by Ty Cobb on 4/27/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationSublistSectionHeader.h"

@interface EHIReservationSublistSectionHeader ()
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@end

@implementation EHIReservationSublistSectionHeader

- (void)updateWithModel:(NSString *)title metrics:(EHILayoutMetrics *)metrics
{
    self.titleLabel.text = title;
    self.backgroundColor = metrics.backgroundColor;
}

# pragma mark - Layout

+ (EHILayoutMetrics *)defaultMetrics
{
    EHILayoutMetrics *metrics = [super defaultMetrics];
    metrics.fixedSize = (CGSize){ .width = EHILayoutValueNil, .height = 14.0f + EHIMediumPadding };
    return metrics;
}

@end
