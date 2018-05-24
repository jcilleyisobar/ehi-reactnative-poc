//
//  EHIThirdPartyMapFactory.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/8/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIThirdPartyMapFactory.h"
#import "EHIThirdPartyMapGoogle.h"
#import "EHIThirdPartyMapWaze.h"

@implementation EHIThirdPartyMapFactory

+(id<EHIThirdPartyMapProtocol>)thirdPartyMapWithType:(EHIThirdPartyMapType)type
{
    switch(type) {
        case EHIThirdPartyMapTypeGoogleMaps:
            return [EHIThirdPartyMapGoogle new];
        case EHIThirdPartyMapTypeWaze:
            return [EHIThirdPartyMapWaze new];
        default:
            NSAssert(false, @"This third party map has no concrete class");
            return nil;
    }
}

@end
