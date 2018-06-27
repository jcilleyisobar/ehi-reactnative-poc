//
//  EHIThirdPartyMapFactory.h
//  Enterprise
//
//  Created by Rafael Ramos on 1/8/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "EHIThirdPartyMap.h"

typedef NS_ENUM(NSUInteger, EHIThirdPartyMapType) {
    EHIThirdPartyMapTypeGoogleMaps,
    EHIThirdPartyMapTypeWaze,
};

@class EHIThirdPartyMap;
@interface EHIThirdPartyMapFactory : NSObject

+(id<EHIThirdPartyMapProtocol>)thirdPartyMapWithType:(EHIThirdPartyMapType)type;

@end
