//
//  EHIFavoritesManagerTests.m
//  Enterprise
//
//  Created by Ty Cobb on 2/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHIFavoritesManager.h"
#import "EHILocations.h"

SpecBegin(EHIFavoritesManagerTests)

describe(@"the favorites manager", ^{

    __block EHILocation *sample;
    
    void(^setFavorited)(EHILocation *, BOOL) = ^(EHILocation *location, BOOL isFavorited) {
        [[EHIFavoritesManager sharedInstance] updateLocation:location isFavorited:isFavorited];
    };
    
    beforeAll(^{
        sample = [EHILocation mock:@"city"];
        setFavorited(sample, NO);
    });
    
    it(@"should favorite locations", ^{
        expect(sample.isFavorited).to.equal(NO);
        setFavorited(sample, YES);
        expect(sample.isFavorited).to.equal(YES);
    });
    
    it(@"should un-favorite locations", ^{
        expect(sample.isFavorited).to.equal(YES);
        setFavorited(sample, NO);
        expect(sample.isFavorited).to.equal(NO);
    });

});

SpecEnd
