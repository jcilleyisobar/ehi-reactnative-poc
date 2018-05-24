//
//  EHILocationConflictDataProvider.h
//  Enterprise
//
//  Created by Rafael Ramos on 6/13/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHILocation.h"


@class EHIReservationBuilder;
@interface EHILocationConflictDataProvider : NSObject

@property (copy, nonatomic, readonly) NSString *title;
@property (copy, nonatomic, readonly) NSString *openHours;
@property (copy, nonatomic, readonly) NSAttributedString *afterHours;
@property (copy, nonatomic) void (^afterHoursBlock)();

- (EHILocationConflictDataProvider * (^)(EHILocation *))location;
- (EHILocationConflictDataProvider * (^)(BOOL))oneWay;

@end
