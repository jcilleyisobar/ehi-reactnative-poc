//
//  EHILocationsFilterBannerViewModelTests.m
//  Enterprise
//
//  Created by Rafael Machado on 19/05/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHILocationsFilterBannerViewModel.h"
#import "EHIFilters.h"

SpecBegin(EHILocationsFilterBannerViewModelTests)

describe(@"EHILocationsFilterBannerViewModel", ^{
    __block EHILocationsFilterBannerViewModel *model;
    
    it(@"should hide when there's no filters", ^{
        model = EHILocationsFilterBannerViewModel.new;
        
        expect(model.hasData).to.beFalsy();
        expect(model.filters).to.beNil();
    });
    
    it(@"should hide when there's an empty filter", ^{
        model = EHILocationsFilterBannerViewModel.new;
        
        [model updateWithModel:@[]];
        
        expect(model.hasData).to.beFalsy();
        expect(model.filters).to.beNil();
    });
    
    it(@"should show filter name when have it", ^{
        model = EHILocationsFilterBannerViewModel.new;
        
        NSArray *filters = [EHIFilters locationTypeFilters];
        
        [model updateWithModel:[EHIFilters locationTypeFilters]];
        
        EHIFilters *filter;
        NSString *title = filters.pluck(@key(filter.displayTitle)).join(@", ");
        
        expect(model.hasData).to.beTruthy();
        expect(model.filters).to.equal(title);
    });
});

SpecEnd
