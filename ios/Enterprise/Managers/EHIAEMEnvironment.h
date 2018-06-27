//
//  EHIAEMEnvironment.h
//  Enterprise
//
//  Created by Rafael Ramos on 18/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIEnvironments.h"

@interface EHIAEMEnvironment : NSObject <EHIServicesEnvironmentConfiguration>
+ (instancetype)serviceWithEnvironment:(EHIEnvironmentType)environmentType;
@end
