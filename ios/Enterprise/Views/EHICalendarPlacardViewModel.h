//
//  EHICalendarPlacardViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 5/9/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHICalendarPlacardViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic, readonly) NSString *title;
@property (strong, nonatomic) NSDate *date;
@end
