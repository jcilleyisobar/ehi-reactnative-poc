//
//  EHIReservationEligibility.h
//  Enterprise
//
//  Created by Alex Koller on 8/12/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"

@interface EHIReservationEligibility : EHIModel

@property (assign, nonatomic, readonly) BOOL canCreate;
@property (assign, nonatomic, readonly) BOOL canModify;
@property (assign, nonatomic, readonly) BOOL canCancel;
@property (assign, nonatomic, readonly) BOOL canViewBase;
@property (assign, nonatomic, readonly) BOOL canViewFull;
@property (copy  , nonatomic, readonly) NSArray *blockedReasons;

@end
