//
//  EHILocationAnnotationTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/24/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHILocationAnnotation.h"

SpecBegin(EHILocationAnnotationTests)

describe(@"EHILocationAnnotation ordering", ^{
    
	EHILocationAnnotation *conflictOne = [[EHILocationAnnotation alloc] initWithLocation:[EHILocation modelWithDictionary:@{
        @"pickupValidity" : @{
            @"validityType": @"INVALID_ALL_DAY"
    	}
    }]];

    EHILocationAnnotation *conflictTwo = [[EHILocationAnnotation alloc] initWithLocation:[EHILocation modelWithDictionary:@{
        @"dropoffValidity" : @{
            @"validityType": @"INVALID_ALL_DAY"
    	}
    }]];

    EHILocationAnnotation *noConflict = [[EHILocationAnnotation alloc] initWithLocation:EHILocation.new];

    it(@"no conflicts locations should be first", ^{
        NSArray *sorted = @[ conflictOne, conflictTwo, noConflict ].sort;
        
        expect(sorted.firstObject).to.equal(noConflict);
    });
    
    it(@"no conflicts locations should be first", ^{
        NSArray *sorted = @[ conflictOne, noConflict, conflictTwo ].sort;
        
        expect(sorted.firstObject).to.equal(noConflict);
    });
});

SpecEnd
