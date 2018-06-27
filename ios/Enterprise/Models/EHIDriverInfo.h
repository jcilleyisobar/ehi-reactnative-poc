//
//  EHIDriverInfo.h
//  Enterprise
//
//  Created by Alex Koller on 4/8/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHIUserLicenseProfile.h"
#import "EHIPhone.h"
#import "EHIOptionalBoolean.h"

typedef NS_ENUM(NSUInteger, EHIDriverInfoLoyaltyType) {
    EHIDriverInfoLoyaltyTypeUnknown,
    EHIDriverInfoLoyaltyTypeEnterprisePlus,
    EHIDriverInfoLoyaltyTypeEmeraldClub,
};

@interface EHIDriverInfo : EHIModel <EHINetworkEncodable>

@property (assign, nonatomic) EHIDriverInfoLoyaltyType loyaltyType;
@property (strong, nonatomic) EHIUserLicenseProfile *licenseProfile;
@property (copy  , nonatomic) NSString *firstName;
@property (copy  , nonatomic) NSString *lastName;
@property (copy  , nonatomic) EHIPhone *phone;
@property (copy  , nonatomic) NSString *email;
@property (copy  , nonatomic) NSString *maskedEmail;
@property (assign, nonatomic) EHIOptionalBoolean wantsEmailNotifications;

// grafted
@property (assign, nonatomic) BOOL shouldSerialize;

// computed properties
@property (nonatomic, readonly) NSString *fullName;
@property (nonatomic, readonly) BOOL hasRequiredFields;

@end
