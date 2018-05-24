//
// Created by Rafael Ramos on 5/17/17.
// Copyright (c) 2017 Enterprise. All rights reserved.
//

#import "EHIDateTimeComponentSection.h"

@protocol EHIDateTimeUpdatableProtocol <NSObject>
- (void)setDate:(NSDate *)date inSection:(EHIDateTimeComponentSection)section;
@end