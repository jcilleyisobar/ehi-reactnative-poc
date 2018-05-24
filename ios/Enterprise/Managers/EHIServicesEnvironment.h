//
//  EHIServicesEnvironment.h
//  Enterprise
//
//  Created by Rafael Ramos on 09/08/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIEnvironments.h"

@interface EHIServicesEnvironment : NSObject
@property (copy, nonatomic, readonly) NSString *domainURL;
@property (copy, nonatomic, readonly) NSString *apiKey;

+ (instancetype)serviceWithType:(EHIServicesEnvironmentType)servicesType forEnvironment:(EHIEnvironmentType)environmentType;

@end
