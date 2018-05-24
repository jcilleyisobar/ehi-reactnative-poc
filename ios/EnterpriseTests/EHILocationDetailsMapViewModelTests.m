//
//  EHILocationDetailsMapViewModelTests.m
//  Enterprise
//
//  Created by Ty Cobb on 3/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHILocationDetailsMapViewModel.h"
#import "EHIMapping.h"

SpecBegin(EHILocationDetailsMapViewModelTests)

describe(@"the location details map view model", ^{
    
    __block EHILocationDetailsMapViewModel *model;
    
    beforeAll(^{
        model = [EHILocationDetailsMapViewModel new];
    });
    
    context(@"when it has a location", ^{
        
        context(@"when it has no coordinate", ^{
            
            beforeAll(^{
                [model updateWithModel:[EHILocation mock:@"city_stub"]];
            });
            
            it(@"should not provide an annotation", ^{
                expect(model.annotations.count).to.equal(0);
            });
          
            it(@"should not provide a region", ^{
                expect(model.regionValue).to.beNil();
            });
            
        });
        
        context(@"when it has a coordinate", ^{
            
            __block EHILocation *location;
            
            beforeAll(^{
                location = [EHILocation mock:@"airport_stub"];
                [model updateWithModel:location];
            });
            
            it(@"should provide a single annotation", ^{
                expect(model.annotations.count).to.equal(1);
            });
            
            it(@"should provide a region", ^{
                expect(model.regionValue).toNot.beNil();
            });
            
            it(@"the region should contain the coordinate", ^{
                MKCoordinateRegion region = model.regionValue.MKCoordinateRegionValue;
                expect(MKCoordinateRegionContains(region, location.position.coordinate)).to.beTruthy();
            });
            
        });
        
    });
    
});

SpecEnd
