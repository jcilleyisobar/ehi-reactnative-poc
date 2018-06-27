//
//  EHIUserProfileFetch.h
//  Enterprise
//
//  Created by Rafael Machado on 8/16/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHIUser.h"

@interface EHIUserProfileFetch : EHIModel
@property (copy, nonatomic, readonly) NSString *country;
@property (copy, nonatomic, readonly) NSString *countrySubdivision;
@property (copy, nonatomic, readonly) NSString *issuingAuthority;
@property (copy, nonatomic, readonly) NSString *licenseNumber;
@property (copy, nonatomic, readonly) NSString *lastName;

+ (instancetype)modelForUser:(EHIUser *)user;

@end
