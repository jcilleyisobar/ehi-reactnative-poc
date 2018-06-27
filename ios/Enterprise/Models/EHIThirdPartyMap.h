//
//  EHIThirdPartyMap.h
//  Enterprise
//
//  Created by Rafael Ramos on 1/7/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIModel.h"

@protocol EHIThirdPartyMapProtocol <NSObject>

- (NSString *)name;
- (NSString *)scheme;
- (NSString *)coordinatesPlaceholder;

- (NSURL *)urlWithCoordinate:(EHILocationCoordinate *)coordinate;

@end