//
//  EHIEnrollmentStepOneViewModel.h
//  Enterprise
//
//  Created by Rafael Machado on 8/4/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIEnrollmentStepViewModel.h"
#import "EHIEnrollmentStepHeaderViewModel.h"
#import "EHIFormFieldActionButtonViewModel.h"

typedef NS_ENUM(NSInteger, EHIEnrollmentStepOneSection) {
    EHIEnrollmentStepOneSectionWarning,
    EHIEnrollmentStepOneSectionHeader,
    EHIEnrollmentStepOneSectionRequiredInfo,
    EHIEnrollmentStepOneSectionProfile,
    EHIEnrollmentStepOneSectionButton
};

@class EHIUserProfiles;
@interface EHIEnrollmentStepOneViewModel : EHIEnrollmentStepViewModel <MTRReactive>
@property (strong, nonatomic) NSArray *formModels;
@property (strong, nonatomic) EHIFormFieldActionButtonViewModel *buttonModel;
@property (copy  , nonatomic) NSString *warning;
@property (assign, nonatomic) BOOL isLoading;
@property (assign, nonatomic) BOOL invalidForm;
@property (assign, nonatomic) BOOL addExtraPadding;
//exposing property for testing
@property (strong, nonatomic) EHICountry *selectedCountry;

- (EHIUser *)currentUser;
- (NSArray *)errorMessagesShowingErrors:(BOOL)showErrors;
//exposing for tests purposes
- (EHIUser *)createUserWithBasicProfile:(EHIUserBasicProfile *)basicProfile andLicenseProfile:(EHIUserLicenseProfile *)licenseProfile;

- (void)didTapBack;

@end
