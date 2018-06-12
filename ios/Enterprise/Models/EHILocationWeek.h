//
//  EHILocationHours.h
//  Enterprise
//
//  Created by Ty Cobb on 1/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationTimes.h"

typedef NS_ENUM(NSUInteger, EHILocationHoursType) {
    EHILocationHoursTypeUnknown,
    EHILocationHoursTypeStandard,
    EHILocationHoursTypeDrop,
};

@interface EHILocationWeek : EHIModel
@property (assign, nonatomic, readonly) EHILocationHoursType type;
@property (copy  , nonatomic, readonly) NSArray<EHILocationTimes> *days;
@end

EHIAnnotatable(EHILocationWeek)
