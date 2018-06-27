//
//  EHILocationIconProviderTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 03/10/17.
//Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHILocationIconProvider.h"

SpecBegin(EHILocationIconProviderTests)

describe(@"EHILocationIconProvider", ^{

    it(@"should return nil when location is nil", ^{
        expect([EHILocationIconProvider iconForLocation:nil]).to.beNil();
    });

    it(@"when location is airport, return airpoirt icon", ^{
        EHILocation *location = [EHILocation modelWithDictionary:@{
           @"location_type" : @"AIRPORT"
        }];
        
        expect([EHILocationIconProvider iconForLocation:location]).to.equal(@"icon_airport_gray");
    });

    it(@"when location is train, return train icon", ^{
        EHILocation *location = [EHILocation modelWithDictionary:@{
           @"location_type" : @"RAIL"
        }];

        expect([EHILocationIconProvider iconForLocation:location]).to.equal(@"icon_train_01");
    });

    it(@"when location is port, return port icon", ^{
        EHILocation *location = [EHILocation modelWithDictionary:@{
            @"location_type" : @"PORT_OF_CALL"
        }];

        expect([EHILocationIconProvider iconForLocation:location]).to.equal(@"icon_portofcall_01");
    });
    
    it(@"when location is exotics, return exotics icon", ^{
        EHILocation *location = [EHILocation modelWithDictionary:@{
           @"attributes" : @[ @"EXOTICS" ]
        }];

        expect([EHILocationIconProvider iconForLocation:location]).to.equal(@"icon_exotics");
    });
});

SpecEnd
