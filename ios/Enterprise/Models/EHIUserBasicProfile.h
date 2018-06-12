//
//  EHIUserBasicProfile.h
//  Enterprise
//
//  Created by mplace on 2/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIUserLoyalty.h"
#import "EHIContractDetails.h"

@interface EHIUserBasicProfile : EHIModel

@property (copy  , nonatomic, readonly) NSString *lastName;
@property (copy  , nonatomic, readonly) NSString *firstName;
@property (assign, nonatomic, readonly) NSInteger age;
@property (copy  , nonatomic, readonly) NSString *username;
@property (copy  , nonatomic, readonly) NSString *maskedBirthDate;
@property (strong, nonatomic, readonly) EHIUserLoyalty *loyalty;

// computed
@property (nonatomic, readonly) NSString *fullName;

@end
