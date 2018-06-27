//
//  EHILocationsViewModelTests.m
//  Enterprise
//
//  Created by Ty Cobb on 1/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHILocationsViewModel.h"
#import "EHIReservationBuilder.h"

SpecBegin(EHILocationsViewModelTests)

describe(@"the locations view model", ^{
   
    __block EHILocationsViewModel *model;
    
    beforeAll(^{
        model = [EHILocationsViewModel new];
    });
    
    it(@"should have localized hint text for the search field", ^{
        expect(model.searchPlaceholder).to.localizeFrom(@"dashboard_search_placeholder");
    });
    
    it(@"should have the localized section headers", ^{
        NSDictionary *headers = @{
            @(EHILocationSectionFavorites) : @"locations_favorites_header_title",
            @(EHILocationSectionRecents) : @"locations_recents_header_title",
        };
       
        headers.each(^(NSNumber *section, NSString *expected) {
            EHISectionHeaderModel *header = [model headerForSection:section.integerValue];
            expect(header.title).to.localizeFrom(expected);
        });
    });
    
    context(@"when it has no locations", ^{
        
        beforeAll(^{
            [model updateWithModel:nil];
        });
        
        it(@"should show the nearby cell", ^{
            expect(model.nearby).toNot.beNil();
        });
        
        it(@"should show the favorites", ^{
            expect(model.favorites).toNot.beNil();
        });
    
    });
    
    context(@"when it has locations", ^{
      
        beforeAll(^{
            [model updateWithModel:[EHILocations mock:@"locations"]];
        });
        
        it(@"should not show the nearby cell", ^{
            expect(model.nearby).to.beNil();
        });
        
        it(@"should not show the favorites", ^{
            expect(model.favorites).to.beNil();
        });
        
        it(@"should show cities", ^{
//            expect(model.cities).toNot.beNil();
        });
        
        it(@"should show airports", ^{
            expect(model.airports).toNot.beNil();
        });

    });
    
});
    
SpecEnd
