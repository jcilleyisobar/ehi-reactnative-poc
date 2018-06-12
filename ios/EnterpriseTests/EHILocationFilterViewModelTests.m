//
//  EHILocationFilterViewModelTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/4/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHILocationFilterViewModel.h"
#import "EHILocationFilterQuery.h"

SpecBegin(EHILocationFilterViewModelTests)

describe(@"EHILocationFilterViewModel", ^{
    __block EHILocationFilterViewModel *model = [EHILocationFilterViewModel new];
    
    it(@"on request for open during travel section should have title", ^{
        NSString *title = [model headerModelForSection:EHILocationFilterSectionOpenDuringTravel].title;
        expect(title).to.localizeFrom(@"filter_details_days_section_title");
    });
    
    it(@"on request for location type section should have title", ^{
        NSString *title = [model headerModelForSection:EHILocationFilterSectionLocationType].title;
        expect(title).to.localizeFrom(@"location_filter_location_type_header_title");
    });
    
    it(@"on request for miscellaneous section should have title", ^{
        NSString *title = [model headerModelForSection:EHILocationFilterSectionMiscellaneous].title;
        expect(title).to.localizeFrom(@"location_filter_miscellaneous_header_title");
    });
    
    context(@"interactions", ^{
        it(@"on clear pickup date, time also must be wiped", ^{
            EHILocationFilterViewModel *model     = EHILocationFilterViewModel.new;
            EHILocationFilterQuery *query         = EHILocationFilterQuery.new;
            EHILocationFilterDateQuery *dateQuery = EHILocationFilterDateQuery.new;
            dateQuery.pickupDate = NSDate.new;
            dateQuery.pickupTime = NSDate.new;
            
            query.datesFilter = dateQuery;
            
            [model updateWithModel:query];
            
            [model didTapOnCleanSection:EHIDateTimeComponentSectionPickupDate];
            
            expect(model.dateTimeFilter.pickupDateModel.value).to.beNil();
            expect(model.dateTimeFilter.pickupTimeModel.value).to.beNil();
            expect(dateQuery.pickupDate).to.beNil();
            expect(dateQuery.pickupTime).to.beNil();
        });
        
        it(@"on clear return date, time also must be wiped", ^{
            EHILocationFilterViewModel *model     = EHILocationFilterViewModel.new;
            EHILocationFilterQuery *query         = EHILocationFilterQuery.new;
            EHILocationFilterDateQuery *dateQuery = EHILocationFilterDateQuery.new;
            dateQuery.returnDate = NSDate.new;
            dateQuery.returnTime = NSDate.new;
            
            query.datesFilter = dateQuery;
            
            [model updateWithModel:query];
            
            [model didTapOnCleanSection:EHIDateTimeComponentSectionReturnDate];
            
            expect(model.dateTimeFilter.returnDateModel.value).to.beNil();
            expect(model.dateTimeFilter.returnTimeModel.value).to.beNil();
            expect(dateQuery.returnDate).to.beNil();
            expect(dateQuery.returnTime).to.beNil();
        });
        
        it(@"clear pickup time", ^{
            EHILocationFilterViewModel *model     = EHILocationFilterViewModel.new;
            EHILocationFilterQuery *query         = EHILocationFilterQuery.new;
            EHILocationFilterDateQuery *dateQuery = EHILocationFilterDateQuery.new;
            dateQuery.pickupTime = NSDate.new;
            
            query.datesFilter = dateQuery;
            
            [model updateWithModel:query];
            
            [model didTapOnCleanSection:EHIDateTimeComponentSectionPickupTime];
            
            expect(model.dateTimeFilter.pickupTimeModel.value).to.beNil();
            expect(dateQuery.pickupTime).to.beNil();
        });
        
        it(@"clear return time", ^{
            EHILocationFilterViewModel *model     = EHILocationFilterViewModel.new;
            EHILocationFilterQuery *query         = EHILocationFilterQuery.new;
            EHILocationFilterDateQuery *dateQuery = EHILocationFilterDateQuery.new;
            dateQuery.returnTime = NSDate.new;
            
            query.datesFilter = dateQuery;
            
            [model updateWithModel:query];
            
            [model didTapOnCleanSection:EHIDateTimeComponentSectionReturnTime];
            
            expect(model.dateTimeFilter.returnTimeModel.value).to.beNil();
            expect(dateQuery.returnTime).to.beNil();
        });
    });
});

SpecEnd
