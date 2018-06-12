//
//  EHIEnrollmentIssuesViewModel.m
//  Enterprise
//
//  Created by Rafael Machado on 8/16/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIEnrollmentIssuesViewModel.h"
#import "EHIProfilePasswordRule.h"
#import "EHIProfilePasswordRuleViewModel.h"
#import "EHIEnrollmentStepOneViewModel.h"
#import "EHIEnrollmentStepTwoViewModel.h"
#import "EHIEnrollmentStepThreeViewModel.h"
#import "EHIEnrollProfile.h"
#import "EHIServices+User.h"
#import "EHIEnrollmentConfirmationViewModel.h"

@interface EHIEnrollmentIssuesViewModel () <EHIFormFieldDelegate, EHIFormFieldTextToggleDelegate>
@property (strong, nonatomic) EHIEnrollmentStepOneViewModel *stepOne;
@property (strong, nonatomic) EHIEnrollmentStepTwoViewModel *stepTwo;
@property (strong, nonatomic) EHIEnrollmentStepThreeViewModel *stepThree;
@end

@implementation EHIEnrollmentIssuesViewModel

+ (instancetype)modelWithPassword:(NSString *)password confirmation:(NSString *)confirmation readTerms:(BOOL)readTerms
{
    EHIEnrollmentIssuesViewModel *model = [EHIEnrollmentIssuesViewModel new];
    model.stepThree.createPasswordModel.password  = password;
    model.stepThree.confirmPasswordModel.password = confirmation;
    model.stepThree.readTerms = readTerms;
    
    return model;
}

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        [self buildModels];
    }
    
    return self;
}

- (void)didBecomeActive
{
    [super didBecomeActive];
    
    self.stepOne.bind.map(@{
        source(self.stepOne.formModels)  : dest(self, .profileModels),
        source(self.stepOne.isLoading)   : dest(self, .isLoading),
        source(self.stepOne.invalidForm) : dest(self, .invalidForm)
    });
    
    self.stepTwo.bind.map(@{
        source(self.stepTwo.formModels)  : dest(self, .addressModels),
        source(self.stepTwo.isLoading)   : dest(self, .isLoading),
        source(self.stepTwo.invalidForm) : dest(self, .invalidForm)
    });
    
    self.stepThree.bind.map(@{
        source(self.stepThree.isLoading)            : dest(self, .isLoading),
        source(self.stepThree.emailModel)           : dest(self, .emailModel),
        source(self.stepThree.phoneModel)           : dest(self, .phoneModel),
        source(self.stepThree.createPasswordModel)  : dest(self, .createPasswordModel),
        source(self.stepThree.passwordSection)      : dest(self, .passwordSection),
        source(self.stepThree.confirmPasswordModel) : dest(self, .confirmPasswordModel),
        source(self.stepThree.invalidForm)          : dest(self, .invalidForm)
    });
}

- (void)buildModels
{
    self.stepOne   = [EHIEnrollmentStepOneViewModel new];
    self.stepTwo   = [EHIEnrollmentStepTwoViewModel new];
    self.stepThree = [EHIEnrollmentStepThreeViewModel new];
    
    self.stepOne.addExtraPadding = YES;
    self.stepTwo.addExtraPadding = YES;

    self.joinModel = [EHIFormFieldActionButtonViewModel new];
    self.joinModel.title    = EHILocalizedString(@"enroll_join_action", @"JOIN", @"");
    self.joinModel.delegate = self;
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHIUser class]]) {
        [self updateWithUser:model];
    }
}

- (void)updateWithUser:(EHIUser *)user
{
    [self.stepOne updateWithModel:user];
    [self.stepTwo updateWithModel:user];
    [self.stepThree updateWithModel:user];
}

- (NSString *)headerTitle
{
    return EHILocalizedString(@"enroll_long_form_title", @"Join Enterprise Plus to start earning free rental days.", @"");
}

# pragma mark - EHIFormFieldDelegate

- (void)formFieldViewModelDidChangeValue:(EHIFormFieldViewModel *)viewModel { }

- (void)formFieldViewModelButtonTapped:(EHIFormFieldViewModel *)viewModel
{
    [self invalidateModels];
    
    BOOL hasErrors = self.invalidForm;
    if(!hasErrors) {
        [EHIAnalytics trackAction:EHIAnalyticsEnrollmentContinue handler:nil];
        
        EHIUser *user = self.stepOne.currentUser;
        [user updateAddress:self.stepTwo.currentAddress];
        [user updateContact:self.stepThree.currentContact];
        
        [self createEnrollProfileWithUser:user];
    }
}

- (void)createEnrollProfileWithUser:(EHIUser *)user
{
    NSString *password = self.stepThree.createPasswordModel.password;
    BOOL acceptedTerms = self.stepThree.confirmPasswordModel.termsRead;
    EHIEnrollProfile *enrollProfile = [EHIEnrollProfile modelForUser:user password:password acceptedTerms:acceptedTerms];
    
    self.isLoading = YES;
    [self cloneCreateProfile:enrollProfile handler:^(EHIUser *user, EHIServicesError *error) {
        self.isLoading = NO;
        if(!error.hasFailed) {
            [self showConfirmation:user];
        }
    }];
}

- (void)showConfirmation:(EHIUser *)user
{
    NSString *username = user.profiles.basic.loyalty.number;
    NSString *password = self.stepThree.createPasswordModel.password;
    EHIEnrollmentConfirmationViewModel *model = [EHIEnrollmentConfirmationViewModel initWithUsername:username password:password];
    model.signinFlow = self.signinFlow;
    model.handler    = self.handler;
    
    // if not in the signin flow, confirmation should pop 5 view controllers to land on the dashboard.
    model.stackPop = 5;
    
    self.router.transition.push(EHIScreenEnrollmentConfirmation).object(model).start(nil);
}

# pragma mark - Validation

- (void)invalidateModels
{
    NSArray *stepOneErrors   = [self.stepOne errorMessagesShowingErrors:YES];
    NSArray *stepTwoErrors   = [self.stepTwo errorMessagesShowingErrors:YES];
    NSArray *stepThreeErrors = [self.stepThree errorMessagesShowingErrors:YES];
    NSArray *messages = [NSArray arrayWithObjects:stepOneErrors, stepTwoErrors, stepThreeErrors, nil].flatten;

    // format warning message
    NSString *title = EHILocalizedString(@"enroll_field_validation_message", @"Please check next if the fields are valid:", @"");
    NSString *message = (messages ?: @[]).map(^(NSString *message){
        return [NSString stringWithFormat:@"• %@", message];
    }).join(@"\n");
    
    self.warning = messages.count > 0 ? [NSString stringWithFormat:@"%@\n%@", title, message] : nil;
}

- (void)setInvalidForm:(BOOL)invalidForm
{
    __block BOOL invalid = NO;

    [MTRReactor nonreactive:^{
        invalid |= self.stepOne.invalidForm;
        invalid |= self.stepTwo.invalidForm;
        invalid |= self.stepThree.invalidForm;
    }];
    
    _invalidForm = invalid;
    
    self.joinModel.isFauxDisabled = invalid;
}

# pragma mark - Section headers

- (EHIReviewSectionHeaderViewModel *)headerForSection:(EHIEnrollmentIssuesSection)section
{
    NSString *title = EHILocalizedString(@"enroll_long_form_step_title", @"STEP #{step}", @"");
    switch (section) {
        case EHIEnrollmentIssuesSectionProfile: {
            title = [title ehi_applyReplacementMap:@{ @"step" : @(1).description }];
            break;
        }
        case EHIEnrollmentIssuesSectionAddress: {
            title = [title ehi_applyReplacementMap:@{ @"step" : @(2).description }];
            break;
        }
        case EHIEnrollmentIssuesSectionPhone: {
            title = [title ehi_applyReplacementMap:@{ @"step" : @(3).description }];
            break;
        }
        default: return nil;
    }
    
    EHIReviewSectionHeaderViewModel *model = [EHIReviewSectionHeaderViewModel new];
    model.title = title;
    
    return model;
}

# pragma mark - Actions

- (void)exit
{
    EHIAlertViewBuilder *alertView = EHIAlertViewBuilder.new
        .title(EHILocalizedString(@"enroll_exit_dialog_title", @"Exit enrollment", @""))
        .message(EHILocalizedString(@"enroll_exit_dialog_message", @"Are you sure?", @""))
        .button(EHILocalizedString(@"enroll_exit_dialog_confirm_action", @"Yes", @""))
        .cancelButton(EHILocalizedString(@"enroll_exit_dialog_cancel_action", @"Cancel", @""));
    
    alertView.show(^(NSInteger index, BOOL canceled) {
        if(!canceled) {
            self.router.transition.root(EHIScreenDashboard).animated(NO).dismiss.start(nil);
        }
    });
}

@end
