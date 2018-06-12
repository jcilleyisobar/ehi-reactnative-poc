//
//  EHIUserProfiles.h
//  Enterprise
//
//  Created by mplace on 2/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHIUserBasicProfile.h"

@interface EHIUserProfiles : EHIModel

@property (strong, nonatomic, readonly) EHIUserBasicProfile *basic;
@property (copy  , nonatomic, readonly) NSString *individualId;
@property (strong, nonatomic) EHIContractDetails *corporateContract;

@end
