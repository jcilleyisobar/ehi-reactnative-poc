//
//  EHILocationNearbyViewModelTests.m
//  Enterprise
//
//  Created by mplace on 2/13/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHILocationNearbyViewModel.h"

SpecBegin(EHILocationNearbyViewModelTests)

describe(@"the location nearby cell view model", ^{
    
    EHILocationNearbyViewModel *viewModel = [EHILocationNearbyViewModel new];
    
    it(@"should provide localized header title ", ^{
        expect(viewModel.title).to.localizeFrom(@"locations_empty_query_nearby_title");
    });
    
});

SpecEnd
