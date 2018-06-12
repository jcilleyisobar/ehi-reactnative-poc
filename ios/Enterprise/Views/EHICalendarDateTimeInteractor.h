//
// Created by Rafael Ramos on 5/17/17.
// Copyright (c) 2017 Enterprise. All rights reserved.
//

#import "EHIDateTimeComponentSection.h"
#import "EHICalendarData.h"
#import "EHISingleDateCalendarEnums.h"

typedef void (^EHICalendarDateTimeInteractorHandler)(NSDate *pickupValue, NSDate *returnValue);

@protocol EHIDateTimeUpdatableProtocol;
@interface EHICalendarDateTimeInteractor : NSObject

- (instancetype)initWithComponent:(id<EHIDateTimeUpdatableProtocol>)component inFlow:(EHISingleDateCalendarFlow)flow;
- (void)handleChangesInSection:(EHIDateTimeComponentSection)section with:(EHICalendarData *)calendarData completion:(EHICalendarDateTimeInteractorHandler)completion;

@end