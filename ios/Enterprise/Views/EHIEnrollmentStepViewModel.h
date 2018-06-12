//
//  EHIEnrollmentStepViewModel.h
//  Enterprise
//
//  Created by Rafael Machado on 8/11/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIEnrollmentStepHeaderViewModel.h"
#import "EHIEnrollProfile.h"
#import "EHIUserManager.h"

typedef NS_ENUM(NSInteger, EHIEnrollmentAddressRow) {
    EHIEnrollmentAddressRowCountryOfResidence,
    EHIEnrollmentAddressRowStreetOne,
    EHIEnrollmentAddressRowStreetTwo,
    EHIEnrollmentAddressRowCity,
    EHIEnrollmentAddressRowState,
    EHIEnrollmentAddressRowZip
};

typedef NS_ENUM(NSInteger, EHIEnrollmentProfile) {
    EHIEnrollmentProfileFirstName,
    EHIEnrollmentProfileLastName,
    EHIEnrollmentProfileLicenseIssuedCountry,
    EHIEnrollmentProfileLicenseIssuedRegion,
    EHIEnrollmentProfileLicenseNumber,
    EHIEnrollmentProfileIssueDate,
    EHIEnrollmentProfileExpirationDate,
    EHIEnrollmentProfileBirth
};

@class EHIRequiredInfoViewModel;
@interface EHIEnrollmentStepViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) NSString *currentState;
@property (assign, nonatomic, readonly) BOOL didMatchProfile;

@property (strong, nonatomic) EHIEnrollmentStepHeaderViewModel *headerModel;
@property (strong, nonatomic) EHIRequiredInfoViewModel *requiredInfoModel;
@property (assign, nonatomic) EHIEnrollmentStep step;
@property (strong, nonatomic) EHIEnrollProfile *enrollmentProfile;
@property (assign, nonatomic) EHIEnrollmentProfileMatch profileMatch;
@property (assign, nonatomic) BOOL signinFlow;
@property (copy  , nonatomic) void (^handler)();

- (void)persistUser:(EHIUser *)user;
- (void)persistUser:(EHIUser *)user password:(NSString *)password readTerms:(BOOL)terms;

- (void)cloneCreateProfile:(EHIEnrollProfile *)profile handler:(void (^)(EHIUser *user, EHIServicesError *error))handler;

- (void)reset;

@end
