//
//  EHILocationEmptyQueryViewModelTests.m
//  Enterprise
//
//  Created by mplace on 2/13/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHILocationEmptyQueryViewModel.h"

SpecBegin(EHILocationEmptyQueryViewModelTests)

describe(@"the location empty query cell view model", ^{
    
    EHILocationEmptyQueryViewModel *viewModel = [EHILocationEmptyQueryViewModel new];
    NSString *model = @"Chicago";
    
    [viewModel updateWithModel:model];
    
    it(@"should provide localized error message title ", ^{
        // Need to provide support for localized format strings
        unimplemented();
    });
    
    it(@"should provide localized error message title", ^{
        expect(viewModel.title).to.localizeFromMap(@"locations_empty_query_title", @{
            @"query" : model,
        });
    });
    
    it(@"should provide localized error message subtitle", ^{
        expect(viewModel.subtitle).to.localizeFrom(@"locations_empty_query_subtitle");
    });
    
    it(@"should provide a localized nearby button title", ^{
        expect(viewModel.nearbyButtonTitle).to.localizeFrom(@"locations_empty_query_nearby_title");
    });
    
    it(@"should provide a localized call button title", ^{
        expect(viewModel.callButtonTitle).to.localizeFrom(@"locations_empty_query_call_title");
    });
});

SpecEnd