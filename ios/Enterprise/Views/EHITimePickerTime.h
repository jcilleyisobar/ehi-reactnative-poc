//
//  EHITimePickerTime.h
//  Enterprise
//
//  Created by Michael Place on 3/29/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@interface EHITimePickerTime : NSObject

/** The @c NSDate corresponding to this time */
@property (strong, nonatomic, readonly) NSDate *date;
/** The stringified name for this time */
@property (copy  , nonatomic, readonly) NSString *title;
/** @c YES if the time unavailable due to location closure */
@property (assign, nonatomic) BOOL isClosed;
/** @c YES if the time is available for after hours */
@property (assign, nonatomic) BOOL isAfterHours;
/** @c YES if the time is just before when the location opens (e.i. 5:30am when branch opens at 6am) */
@property (assign, nonatomic) BOOL isOpenTime;
/** @c YES if the time is just when the location closes (e.i. 8pm when the branch closes at 8pm) */
@property (assign, nonatomic) BOOL isCloseTime;
/** @c YES if the time is immediately before an open time or is the close time for a slice */
@property (assign, nonatomic, readonly) BOOL isBoundaryTime;

/** Constructs a new time from the date */
- (instancetype)initWithDate:(NSDate *)date;

@end
