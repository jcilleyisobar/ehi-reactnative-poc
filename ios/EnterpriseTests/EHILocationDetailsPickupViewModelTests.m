//
//  EHILocationDetailsPickupViewModelTests.m
//  Enterprise
//
//  Created by Ty Cobb on 3/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHILocationDetailsPickupViewModel.h"
#import "EHILocalization.h"

SpecBegin(EHILocationDetailsPickupViewModel)

describe(@"The location deatails pickup model", ^{
    
    __block EHILocationDetailsPickupViewModel *model;
    
    beforeAll(^{
        model = [EHILocationDetailsPickupViewModel new];
    });
   
    it(@"should provide a title", ^{
        expect(model.title).to.localizeFrom(@"location_details_pickup_title");
    });
    
    it(@"should provide details", ^{
        expect(model.details).notTo.beNil();
    });
    
    it(@"should highlight the call-to-action in the details", ^{
        NSString *action = EHILocalizedString(@"location_details_pickup_cta", nil, nil);
        NSRange expected = [model.details rangeOfString:action];
        
        expect(model.highlightRange.location).to.equal(expected.location);
        expect(model.highlightRange.length).to.equal(expected.length);
    });
    
});

SpecEnd
