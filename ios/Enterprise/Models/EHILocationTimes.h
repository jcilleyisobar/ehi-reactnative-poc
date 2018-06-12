//
//  EHILocationTimes.h
//  Enterprise
//
//  Created by Ty Cobb on 1/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationTimesSlice.h"

@interface EHILocationTimes : EHIModel

@property (copy  , nonatomic, readonly) NSDate *date;
@property (copy  , nonatomic, readonly) NSString *name;
@property (assign, nonatomic, readonly) BOOL isClosedAllDay;
@property (assign, nonatomic, readonly) BOOL isOpenAllDay;
@property (assign, nonatomic, readonly) BOOL isUnavailable;
@property (copy  , nonatomic) NSArray<EHILocationTimesSlice> *slices;

// computed properties
@property (nonatomic, readonly) NSString *displayText;
@property (nonatomic, readonly) BOOL isToday;

- (BOOL)isOpenForDate:(NSDate *)date;
- (BOOL)doesOpenAtDate:(NSDate *)date;
- (BOOL)doesCloseAtDate:(NSDate *)date;

@end

EHIAnnotatable(EHILocationTimes)
