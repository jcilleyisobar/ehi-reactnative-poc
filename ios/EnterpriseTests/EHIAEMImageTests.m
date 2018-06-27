//
//  EHIAEMImageTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 07/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHIAEMImage.h"

SpecBegin(EHIAEMImageTests)

describe(@"EHIAEMImage", ^{
    context(@"image parsing", ^{
        NSDictionary *input = @{
            @"480" : @"/content/dam/ehicom/legacy-images/austin-harmony.png.480.270.png",
            @"640" : @"/content/dam/ehicom/legacy-images/austin-harmony.png.640.360.png",
            @"1280": @"/content/dam/ehicom/legacy-images/austin-harmony.png.1280.720.png"
        };
        
        EHIAEMImage *image = [EHIAEMImage modelWithDictionary:input];
        
        it(@"when query for a size, less than min, return min", ^{
            expect([image pathForWidth:10 quality:EHIImageQualityHigh]).to.equal(@"/content/dam/ehicom/legacy-images/austin-harmony.png.480.270.png");
        });
        
        it(@"when query for a size, between min and mid, return mid", ^{
            expect([image pathForWidth:500 quality:EHIImageQualityHigh]).to.equal(@"/content/dam/ehicom/legacy-images/austin-harmony.png.640.360.png");
        });
        
        it(@"when query for a size, between mid and max, return max", ^{
            expect([image pathForWidth:680 quality:EHIImageQualityHigh]).to.equal(@"/content/dam/ehicom/legacy-images/austin-harmony.png.1280.720.png");
        });
        
        it(@"when query for a size, greater than max, return max", ^{
            expect([image pathForWidth:2040 quality:EHIImageQualityHigh]).to.equal(@"/content/dam/ehicom/legacy-images/austin-harmony.png.1280.720.png");
        });
    });
});

SpecEnd
