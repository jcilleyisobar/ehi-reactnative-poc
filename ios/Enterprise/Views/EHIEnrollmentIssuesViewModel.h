//
//  EHIEnrollmentIssuesViewModel.h
//  Enterprise
//
//  Created by Rafael Machado on 8/16/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIEnrollmentStepViewModel.h"
#import "EHIEnrollmentPasswordViewModel.h"
#import "EHIFormFieldTextViewModel.h"
#import "EHIFormFieldTextToggleViewModel.h"
#import "EHIFormFieldActionButtonViewModel.h"
#import "EHIReviewSectionHeaderViewModel.h"

typedef NS_ENUM(NSInteger, EHIEnrollmentIssuesSection) {
    EHIEnrollmentIssuesSectionWarning,
    EHIEnrollmentIssuesSectionHeader,
    EHIEnrollmentIssuesSectionRequiredInfo,
    EHIEnrollmentIssuesSectionProfile,
    EHIEnrollmentIssuesSectionAddress,
    EHIEnrollmentIssuesSectionPhone,
    EHIEnrollmentIssuesSectionEmail,
    EHIEnrollmentIssuesSectionPasswordCreate,
    EHIEnrollmentIssuesSectionPasswordRules,
    EHIEnrollmentIssuesSectionPasswordConfirmation,
    EHIEnrollmentIssuesSectionJoin
};

@interface EHIEnrollmentIssuesViewModel : EHIEnrollmentStepViewModel <MTRReactive>

+ (instancetype)modelWithPassword:(NSString *)password confirmation:(NSString *)confirmation readTerms:(BOOL)readTerms;

@property (copy  , nonatomic) NSString *headerTitle;
@property (copy  , nonatomic) NSString *warning;
@property (copy  , nonatomic) NSArray *profileModels;
@property (copy  , nonatomic) NSArray *addressModels;
@property (strong, nonatomic) EHIFormFieldTextViewModel *phoneModel;
@property (strong, nonatomic) EHIFormFieldTextToggleViewModel *emailModel;
@property (strong, nonatomic) EHIEnrollmentPasswordViewModel *createPasswordModel;
@property (copy  , nonatomic) NSArray *passwordSection;
@property (strong, nonatomic) EHIEnrollmentPasswordViewModel *confirmPasswordModel;
@property (strong, nonatomic) EHIFormFieldActionButtonViewModel *joinModel;
@property (assign, nonatomic) BOOL invalidForm;
@property (assign, nonatomic) BOOL isLoading;

- (EHIReviewSectionHeaderViewModel *)headerForSection:(EHIEnrollmentIssuesSection)section;
- (void)exit;

@end
