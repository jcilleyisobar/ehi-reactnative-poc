//
//  EHITimePickerTimeViewModel.m
//  Enterprise
//
//  Created by Michael Place on 3/29/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITimePickerTimeViewModel.h"
#import "EHITimePickerTime.h"

@interface EHITimePickerTimeViewModel ()
@property (strong, nonatomic) EHITimePickerTime *time;
@end

@implementation EHITimePickerTimeViewModel

- (void)updateWithModel:(EHITimePickerTime *)time
{
    [super updateWithModel:time];
    
    if([time isKindOfClass:[EHITimePickerTime class]]) {
        self.time = time;
    }
}

# pragma mark - Day

- (void)setTime:(EHITimePickerTime *)time
{
    _time = time;
    
    self.title    = time.title;
    self.isClosed = time.isClosed;
    self.isAfterHours   = time.isAfterHours;
    self.isBoundaryTime = time.isBoundaryTime;
}

@end
