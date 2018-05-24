//
//  EHILocationCellViewModelTests.m
//  Enterprise
//
//  Created by mplace on 2/12/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHILocationViewModel.h"
#import "EHILocations.h"
#import "EHILocation.h"
#import "EHICity.h"

SpecBegin(EHILocationCellViewModelTests)

describe(@"the location cell view model", ^{

    __block EHILocationViewModel *model;
    
    beforeAll(^{
        model = [EHILocationViewModel new];
    });
    
    context(@"when updated with a city", ^{
        
        __block EHICity *city;
        
        beforeAll(^{
            city = [EHILocations mock:@"locations"].cities.firstObject;
            model = [EHILocationViewModel new];
            [model updateWithModel:city];
        });
        
        it(@"should provide a title that is the formatted city string", ^{
            expect(model.title.string).to.equal(city.formattedName);
        });
        
        it(@"should not show the airport icon", ^{
            expect(model.hidesIcon).to.beTruthy();
        });
        
        it(@"should not show the location details button", ^{
            expect(model.hidesSelectButton).to.beTruthy();
        });
        
    });
    
    context(@"when updated with a location", ^{
        
        __block EHILocation *location;
        
        beforeAll(^{
            location = [EHILocation mock:@"city"];
            [model updateWithModel:location];
        });
        
        it(@"should provide a title that matches the location name", ^{
            expect(model.title.string).to.equal(location.displayName);
        });
        
        it(@"should provide a subtitle that matches the comma separated address", ^{
            unimplemented();
        });
        
        it(@"should show the location details button", ^{
            expect(model.hidesSelectButton).to.beFalsy();
        });
    });
    
    context(@"when updated with an airport location", ^{
        
        __block EHILocation *location;
        
        beforeAll(^{
            location = [EHILocation mock:@"airport"];
            [model updateWithModel:location];
        });
        
        it(@"should show the airport icon", ^{
            expect(model.hidesIcon).to.beFalsy();
        });
    });
    
    context(@"when updated with a city location", ^{
        
        beforeAll(^{
            [model updateWithModel:[EHILocation mock:@"city"]];
        });
        
        it(@"should not show the airport icon", ^{
            expect(model.hidesIcon).to.beTruthy();
        });
    });
});

SpecEnd
