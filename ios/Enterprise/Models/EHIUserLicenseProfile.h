//
//  EHIUserLicenseProfile.h
//  Enterprise
//
//  Created by mplace on 2/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"

@interface EHIUserLicenseProfile : EHIModel
@property (copy  , nonatomic, readonly) NSString *licenseNumber;
@property (copy  , nonatomic, readonly) NSString *subdivisionCode;
@property (copy  , nonatomic, readonly) NSString *subdivisionName;
@property (copy  , nonatomic, readonly) NSString *issuingAuthority;
@property (copy  , nonatomic, readonly) NSString *countryCode;
@property (copy  , nonatomic, readonly) NSString *countryName;
@property (copy  , nonatomic, readonly) NSDate *licenseIssue;
@property (copy  , nonatomic, readonly) NSString *maskedLicenseIssue;
@property (copy  , nonatomic, readonly) NSDate *licenseExpiry;
@property (copy  , nonatomic, readonly) NSString *maskedLicenseExpiry;
@property (copy  , nonatomic, readonly) NSDate *birthdate;
@property (copy  , nonatomic, readonly) NSString *maskedBirthDate;
@property (assign, nonatomic, readonly) BOOL isOnDnrList;
@end
