//
//  EHIUserAdditionalData.h
//  Enterprise
//
//  Created by Rafael Ramos on 18/08/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIModel.h"

@interface EHIUserAdditionalData : EHIModel <EHINetworkEncodable>

+ (instancetype)modelForProfileUpdate;
+ (instancetype)modelForProfileCreation;

@property (assign, nonatomic, readonly) BOOL isUniqueEmail;
@property (copy  , nonatomic, readonly) NSArray *restrictions;
@property (assign, nonatomic, readonly) BOOL isEditable;
@property (assign, nonatomic, readonly) BOOL isBranchEnrolled;
@property (copy  , nonatomic, readonly) NSString *authToken;
@property (copy  , nonatomic, readonly) NSString *credentials;
@property (assign, nonatomic, readonly) BOOL isCreditCardNearExpiration;
@property (assign, nonatomic, readonly) BOOL isCreditCardExpired;
@property (assign, nonatomic, readonly) BOOL isDriverLicenseExpired;

@end
