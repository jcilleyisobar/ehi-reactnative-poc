//
//  EHILocationTimesSlice.h
//  Enterprise
//
//  Created by Ty Cobb on 1/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@class EHILocationTimes;

#import "EHIModel.h"

@interface EHILocationTimesSlice : EHIModel
@property (copy, nonatomic, readonly) NSDate *open;
@property (copy, nonatomic, readonly) NSDate *close;
// computed properties
@property (nonatomic, readonly) NSString *displayText;
@property (nonatomic, readonly) BOOL isFirstSlice;
// parent relationship
@property (weak, nonatomic) EHILocationTimes *times;
@end

EHIAnnotatable(EHILocationTimesSlice)
