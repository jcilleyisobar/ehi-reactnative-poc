//
//  EHICountry.h
//  Enterprise
//
//  Created by Alex Koller on 5/8/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHIRegion.h"
#import "EHIPromotionContract.h"

#define EHICountryCodeUS @"US"
#define EHICountryCodeCanada @"CA"
#define EHICountryCodeFrance @"FR"
#define EHICountryCodeGermany @"DE"
#define EHICountryCodeUK @"GB"

typedef NS_ENUM(NSInteger, EHICountryFieldVisibility) {
    EHICountryFieldVisibilityUnknown,
    EHICountryFieldVisibilityUnsupported,
    EHICountryFieldVisibilityOptional,
    EHICountryFieldVisibilityMandatory
};

@interface EHICountry : EHIModel

@property (copy  , nonatomic, readonly) NSString *code;
@property (copy  , nonatomic, readonly) NSString *name;
@property (assign, nonatomic, readonly) BOOL enableIssuingCountry;
@property (assign, nonatomic, readonly) BOOL enableCountrySubdivision;
@property (assign, nonatomic, readonly) BOOL disableOneWay;
@property (assign, nonatomic, readonly) BOOL defaultEmailOptIn;
@property (assign, nonatomic, readonly) BOOL isEuropeanAddress;
@property (assign, nonatomic, readonly) BOOL isLicenseIssuingAuthorityRequired;
@property (assign, nonatomic, readonly) EHICountryFieldVisibility streetAddressTwo;
@property (assign, nonatomic, readonly) EHICountryFieldVisibility houseNumber;
@property (assign, nonatomic, readonly) EHICountryFieldVisibility licenseIssuedBy;
@property (assign, nonatomic, readonly) EHICountryFieldVisibility licenseExpiryDate;
@property (assign, nonatomic, readonly) EHICountryFieldVisibility licenseIssueDate;
@property (copy  , nonatomic, readonly) NSString *disputeEmail;
@property (copy  , nonatomic, readonly) NSString *disputePhone;
@property (copy  , nonatomic, readonly) NSString *issuingAuthorityName;
@property (strong, nonatomic) EHIPromotionContract *weekendSpecial;
@property (strong, nonatomic) NSArray *regions;

// computed
@property (nonatomic, readonly) BOOL isUS;
@property (nonatomic, readonly) BOOL isCanada;
@property (nonatomic, readonly) BOOL isFrance;
@property (nonatomic, readonly) BOOL isUK;
@property (nonatomic, readonly) BOOL isNorthAmerica;

@property (nonatomic, readonly) BOOL shouldMoveVansToEndOfList;
@property (nonatomic, readonly) BOOL shouldShowIdentityCheckWithExternalVendorMessage;

- (EHIPromotionContract *)weekendSpecial;

@end
