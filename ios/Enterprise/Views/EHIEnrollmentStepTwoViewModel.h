//
//  EHIEnrollmentStepTwoViewModel.h
//  Enterprise
//
//  Created by Rafael Machado on 8/6/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIEnrollmentStepViewModel.h"
#import "EHIEnrollmentStepHeaderViewModel.h"
#import "EHIFormFieldActionButtonViewModel.h"
#import "EHIEnrollmentStepTwoMatchViewModel.h"

typedef NS_ENUM(NSInteger, EHIEnrollmentStepTwoSection) {
    EHIEnrollmentStepTwoSectionWarning,
    EHIEnrollmentStepTwoSectionHeader,
    EHIEnrollmentStepTwoSectionRequiredInfo,
    EHIEnrollmentStepTwoSectionAddress,
    EHIEnrollmentStepTwoSectionButton
};

@class EHIAddress;
@interface EHIEnrollmentStepTwoViewModel : EHIEnrollmentStepViewModel <MTRReactive>
@property (strong, nonatomic) EHIFormFieldActionButtonViewModel *buttonModel;
@property (strong, nonatomic) EHIEnrollmentStepTwoMatchViewModel *matchViewModel;
@property (copy  , nonatomic) NSString *warning;
@property (strong, nonatomic) NSArray *formModels;
@property (assign, nonatomic) BOOL isLoading;
@property (assign, nonatomic) BOOL invalidForm;
@property (assign, nonatomic) BOOL addExtraPadding;

- (EHIAddress *)currentAddress;
- (NSArray *)errorMessagesShowingErrors:(BOOL)showErrors;

- (void)changeAddress;
- (void)keepAddress;

@end
