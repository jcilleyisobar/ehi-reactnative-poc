//
//  EHILocationValidity.h
//  Enterprise
//
//  Created by Rafael Ramos on 5/22/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHILocationDay.h"

typedef NS_ENUM(NSInteger, EHILocationValidityStatus) {
    EHILocationValidityStatusUnknown,
    EHILocationValidityStatusValidStandardHours,
    EHILocationValidityStatusValidAfterHours,
    EHILocationValidityStatusInvalidAllDay,
    EHILocationValidityStatusInvalidAtThatTime,
};

@interface EHILocationValidity : EHIModel
@property (assign, nonatomic, readonly) EHILocationValidityStatus status;
@property (strong, nonatomic, readonly) EHILocationDay *hours;
@end
