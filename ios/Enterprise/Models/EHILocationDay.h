//
//  EHILocationDay.h
//  Enterprise
//
//  Created by Ty Cobb on 5/8/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationTimes.h"

@interface EHILocationDay : EHIModel
@property (strong, nonatomic) EHILocationTimes *standardTimes;
@property (strong, nonatomic) EHILocationTimes *dropTimes;
@property (strong, nonatomic) EHILocationTimes *afterHoursTimes;
@end

EHIAnnotatable(EHILocationDay);