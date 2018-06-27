//
//  EHILocationHours.h
//  Enterprise
//
//  Created by Ty Cobb on 5/8/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationDay.h"

@interface EHILocationHours : EHIModel
@property (strong, nonatomic) NSDictionary<EHILocationDay> *days;
@end

@interface EHILocationHours (Subscripting)
/** Hours are keyable by either a date string or an @c NSDate */
- (EHILocationDay *)objectForKeyedSubscript:(id)key;
@end
