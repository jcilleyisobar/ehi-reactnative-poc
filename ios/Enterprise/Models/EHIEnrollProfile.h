//
//  EHIEnrollProfile.h
//  Enterprise
//
//  Created by Rafael Ramos on 8/19/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHIPhone.h"
#import "EHIAddress.h"
#import "EHIUserLicenseProfile.h"
#import "EHIEnrollTerms.h"
#import "EHIUser.h"

typedef NS_ENUM(NSInteger, EHIEnrollmentProfileMatch) {
    EHIEnrollmentProfileMatchNone,
    EHIEnrollmentProfileMatchNoMatch,
    EHIEnrollmentProfileMatchNonLoyalty,
    EHIEnrollmentProfileMatchEmeraldClub,
    EHIEnrollmentProfileMatchEnterprisePlus
};

@interface EHIEnrollProfile : EHIModel
@property (copy  , nonatomic, readonly) NSString *firstName;
@property (copy  , nonatomic, readonly) NSString *lastName;
@property (copy  , nonatomic, readonly) NSString *password;
@property (copy  , nonatomic, readonly) NSString *email;
@property (strong, nonatomic, readonly) EHIAddress *address;
@property (strong, nonatomic, readonly) EHIUserLicenseProfile *license;
@property (strong, nonatomic, readonly) EHIEnrollTerms *terms;
@property (strong, nonatomic, readonly) NSDate *birthDate;
@property (strong, nonatomic, readonly) EHIUserPreferencesProfile *preferences;
@property (strong, nonatomic, readonly) NSArray<EHIPhone> *phones;

+ (instancetype)modelForUser:(EHIUser *)user password:(NSString *)password acceptedTerms:(BOOL)terms;

@end
