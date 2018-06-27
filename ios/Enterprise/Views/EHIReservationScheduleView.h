//
//  EHIReservationScheduleView.h
//  Enterprise
//
//  Created by Alex Koller on 4/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIView.h"
#import "EHIReservationScheduleViewType.h"

@interface EHIReservationScheduleView : EHIView
@property (assign, nonatomic) EHIReservationScheduleViewType type;
@property (assign, nonatomic) BOOL isEditable;
@end
