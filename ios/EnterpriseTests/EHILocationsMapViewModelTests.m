//
//  EHILocationsMapViewModelTests.m
//  Enterprise
//
//  Created by Ty Cobb on 2/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHILocationsMapViewModel.h"
#import "EHILocations.h"
#import "EHISpatialLocations.h"
#import "EHIMapping.h"

SpecBegin(EHILocationsMapViewModelTests)

describe(@"the locations map view model", ^{
    
    __block EHILocationsMapViewModel *model;
    
    beforeAll(^{
        model = [EHILocationsMapViewModel new];
    });
    
    it(@"should provide a title for the scroll to top button", ^{
        expect(model.scrollToTopTitle).to.localizeFrom(@"locations_scrolls_title");
    });
 
    context(@"when it's displaying locations for a city", ^{
        
        __block EHILocationsMapViewModel *model;
       
        beforeAll(^{
            model = [EHILocationsMapViewModel new];
            model.city = [EHILocations mock:@"locations"].cities.firstObject;
            model.listModels = [EHISpatialLocations mock:@"spatial"].locations;
        });
        
        it(@"should provide a title that matches the city", ^{
            expect(model.title).to.equal(model.city.formattedName);
        });
       
//        it(@"should provide annotations with the correct coordinate", ^{
//            for(EHILocationAnnotation *annotation in model.annotations) {
//                expect(annotation.coordinate.latitude).to.equal(annotation.location.position.latitude);
//                expect(annotation.coordinate.longitude).to.equal(annotation.location.position.longitude);
//            }
//        });
        
//        it(@"should provide a region that fits all the annotations", ^{
//            MKCoordinateRegion region = NSValueUnbox(MKCoordinateRegion, model.animatedMapRegion);
//            for(EHILocationAnnotation *annotation in model.annotations) {
//                expect(MKCoordinateRegionContains(region, annotation.coordinate)).to.beTruthy();
//            }
//        });
        
    });
    
    context(@"when it's displaying nearby locations", ^{
        
        __block EHILocationsMapViewModel *model;
        
        beforeAll(^{
            model = [EHILocationsMapViewModel new];
            model.nearbyLocation = [EHIUserLocation new];
            model.nearbyLocation.currentLocation = [CLLocation new];
        });
        
        it(@"should provide a title for nearby locations", ^{
            expect(model.title).to.localizeFrom(@"locations_nearby_title");
        });
        
        it(@"should center the map region on the location", ^{
            MKCoordinateRegion region = NSValueUnbox(MKCoordinateRegion, model.animatedMapRegion);
            expect(region.center).to.equal(model.nearbyLocation.currentLocation.coordinate);
        });
        
        context(@"after receiving the nearby locations", ^{
            
            beforeAll(^{
                model.listModels = [EHISpatialLocations mock:@"spatial"].locations;
            });
            
        });
        
    });
    
    context(@"when the user moved the map", ^{
      
        __block EHILocationsMapViewModel *model;
        
        beforeAll(^{
            model = [EHILocationsMapViewModel new];
            model.mapCenter = (CLLocationCoordinate2D){ .latitude = 84.0, .longitude = 34.0 };
        });
    });
    
});

SpecEnd
