//
//  EHILocationDetailsHoursViewModel.h
//  Enterprise
//
//  Created by Ty Cobb on 2/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHILocationTimesSlice.h"

@interface EHILocationDetailsHoursViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic, readonly) NSString *date;
@property (copy  , nonatomic, readonly) NSString *time;
@property (assign, nonatomic, readonly) BOOL isToday;
@end
