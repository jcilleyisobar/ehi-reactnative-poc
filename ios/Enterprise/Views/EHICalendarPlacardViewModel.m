//
//  EHICalendarPlacardViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/9/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHICalendarPlacardViewModel.h"
#import "EHISingleDateCalendarEnums.h"

@interface EHICalendarPlacardViewModel ()
@property (copy, nonatomic) NSString *title;
@property (assign, nonatomic) EHISingleDateCalendarType type;
@end

@implementation EHICalendarPlacardViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        if([model isKindOfClass:NSNumber.class]) {
            self.type = (EHISingleDateCalendarType)[model integerValue];
        }
    	self.title = self.dateTitle;
    }
    
    return self;
}

- (void)setDate:(NSDate *)date
{
    _date = date;
    
    NSString *title = self.dateTitle;
    if(date) {
        title = [date ehi_localizedDateString];
    }

    self.title = title;
}

- (NSString *)dateTitle
{
    if(self.type == EHISingleDateCalendarTypePickup) {
        return EHILocalizedString(@"reservation_scheduler_pickup_date_callout", @"Select pick-up date", @"");
    } else {
        return EHILocalizedString(@"reservation_scheduler_return_date_callout", @"Select return date", @"");
    }
}

@end
