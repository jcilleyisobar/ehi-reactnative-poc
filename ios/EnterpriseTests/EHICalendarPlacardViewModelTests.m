//
//  EHICalendarPlacardViewModelTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/9/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHICalendarPlacardViewModel.h"
#import "NSDate+Formatting.h"
#import "EHISingleDateCalendarEnums.h"

SpecBegin(EHICalendarPlacardViewModelTests)

describe(@"EHICalendarPlacardViewModel", ^{
    
    it(@"on date screen, should show select a date title", ^{
        EHICalendarPlacardViewModel *pickupModel = [[EHICalendarPlacardViewModel alloc] initWithModel:@(EHISingleDateCalendarTypePickup)];
        expect(pickupModel.title).to.localizeFrom(@"reservation_scheduler_pickup_date_callout");
    });
    
    
    it(@"on a date selection, should show the date formatted", ^{
        EHICalendarPlacardViewModel *pickupModel = [[EHICalendarPlacardViewModel alloc] initWithModel:@(EHISingleDateCalendarTypePickup)];
        
        pickupModel.date = [NSDate new];
        
        expect(pickupModel.title).to.equal([[NSDate new] ehi_localizedDateString]);
    });
    
    
    it(@"on date screen, should show select a date title", ^{
        EHICalendarPlacardViewModel *returnModel = [[EHICalendarPlacardViewModel alloc] initWithModel:@(EHISingleDateCalendarTypeReturn)];
        expect(returnModel.title).to.localizeFrom(@"reservation_scheduler_return_date_callout");
    });
    
    
    it(@"on a date selection, should show the date formatted", ^{
        EHICalendarPlacardViewModel *returnModel = [[EHICalendarPlacardViewModel alloc] initWithModel:@(EHISingleDateCalendarTypeReturn)];
        
        returnModel.date = [NSDate new];
        
        expect(returnModel.title).to.equal([[NSDate new] ehi_localizedDateString]);
    });
});

SpecEnd
