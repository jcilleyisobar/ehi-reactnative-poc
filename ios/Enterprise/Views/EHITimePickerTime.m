//
//  EHITimePickerTime.m
//  Enterprise
//
//  Created by Michael Place on 3/29/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITimePickerTime.h"

@implementation EHITimePickerTime

- (instancetype)initWithDate:(NSDate *)date
{
    if(self = [super init]) {
        _date = date;
    }
    
    return self;
}

# pragma mark - Accessors

- (NSString *)title
{
    return [self.date ehi_localizedTimeString];
}

- (BOOL)isBoundaryTime
{
    return self.isOpenTime || self.isCloseTime;
}

@end
