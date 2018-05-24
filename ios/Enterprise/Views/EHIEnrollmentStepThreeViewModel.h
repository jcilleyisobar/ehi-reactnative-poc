//
//  EHIEnrollmentStepThreeViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 8/10/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIEnrollmentStepViewModel.h"
#import "EHIEnrollmentStepHeaderViewModel.h"
#import "EHIFormFieldTextViewModel.h"
#import "EHIFormFieldTextToggleViewModel.h"
#import "EHIEnrollmentPasswordViewModel.h"
#import "EHIFormFieldActionButtonViewModel.h"

typedef NS_ENUM(NSInteger, EHIEnrollmentStepThreeSection) {
    EHIEnrollmentStepThreeSectionWarning,
    EHIEnrollmentStepThreeSectionHeader,
    EHIEnrollmentStepThreeSectionRequiredInfo,
    EHIEnrollmentStepThreeSectionPhone,
    EHIEnrollmentStepThreeSectionEmail,
    EHIEnrollmentStepThreeSectionPassword,
    EHIEnrollmentStepThreeSectionPasswordRules,
    EHIEnrollmentStepThreeSectionPasswordConfirmation,
    EHIEnrollmentStepThreeSectionJoin,
    EHIEnrollmentStepThreeSectionRequiredInfoFootnote
};

@class EHIUserContactProfile;
@class EHIRequiredInfoFootnoteViewModel;
@interface EHIEnrollmentStepThreeViewModel : EHIEnrollmentStepViewModel <MTRReactive>
@property (copy  , nonatomic) NSString *warning;
@property (strong, nonatomic) EHIFormFieldTextViewModel *phoneModel;
@property (strong, nonatomic) EHIFormFieldTextToggleViewModel *emailModel;
@property (strong, nonatomic) EHIEnrollmentPasswordViewModel *createPasswordModel;
@property (strong, nonatomic) NSArray *passwordSection;
@property (strong, nonatomic) EHIEnrollmentPasswordViewModel *confirmPasswordModel;
@property (assign, nonatomic) BOOL readTerms;
@property (strong, nonatomic) EHIFormFieldActionButtonViewModel *joinModel;
@property (strong, nonatomic) EHIRequiredInfoFootnoteViewModel *footnoteModel;
@property (assign, nonatomic) BOOL invalidForm;
@property (assign, nonatomic) BOOL isLoading;

- (EHIUserContactProfile *)currentContact;
- (NSArray *)errorMessagesShowingErrors:(BOOL)showErrors;

@end
