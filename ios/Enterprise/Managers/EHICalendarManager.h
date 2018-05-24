//
//  EHICalendarManager.h
//  Enterprise
//
//  Created by cgross on 8/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "EHIReservation.h"

@interface EHICalendarManager : NSObject

+ (void)addEventForReservation:(EHIReservation *)reservation handler:(void (^)(BOOL success, NSError *error))handler;

@end
