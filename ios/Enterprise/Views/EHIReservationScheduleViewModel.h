//
//  EHIReservationScheduleViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 4/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIReservationScheduleViewType.h"

@interface EHIReservationScheduleViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic) NSString *viewTitle;
@property (copy  , nonatomic) NSString *dateTitle;
@property (copy  , nonatomic) NSString *timeTitle;
@property (assign, nonatomic) EHIReservationScheduleViewType type;
@end
