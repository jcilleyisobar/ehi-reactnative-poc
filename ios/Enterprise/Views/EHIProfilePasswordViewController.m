//
//  EHIProfilePasswordViewController.m
//  Enterprise
//
//  Created by fhu on 5/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIProfilePasswordViewController.h"
#import "EHIProfilePasswordViewModel.h"
#import "EHISigninField.h"
#import "EHIActionButton.h"
#import "EHIActivityIndicator.h"
#import "EHIRestorableConstraint.h"
#import "EHIListCollectionView.h"
#import "EHIProfilePasswordRuleCell.h"
#import "EHIProfilePasswordRuleViewModel.h"
#import "EHIRequiredInfoView.h"

@interface EHIProfilePasswordViewController () <EHISigninFieldActions, EHIListCollectionViewDelegate>
@property (strong, nonatomic) EHIProfilePasswordViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHISigninField *passwordField;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *activityIndicator;
@property (weak  , nonatomic) IBOutlet EHISigninField *passwordConfirmationField;
@property (weak  , nonatomic) IBOutlet UIView *inlineErrorContainer;
@property (weak  , nonatomic) IBOutlet UIView *alertContainer;
@property (weak  , nonatomic) IBOutlet UILabel *inlineErrorLabel;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *inlineErrorHeight;
@property (weak  , nonatomic) IBOutlet UILabel *updateAlertLabel;
@property (weak  , nonatomic) IBOutlet EHIActionButton *changePasswordButton;
@property (weak  , nonatomic) IBOutlet UIScrollView *scrollView;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *collectionViewHeight;
@property (weak  , nonatomic) IBOutlet EHIRequiredInfoView *requiredInfoWarningContainer;
@end

@implementation EHIProfilePasswordViewController

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHIProfilePasswordViewModel new];
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.passwordField.model = self.viewModel.passwordNewFieldModel;
    self.passwordConfirmationField.model = self.viewModel.passwordConfirmFieldModel;

    self.collectionView.section.klass  = EHIProfilePasswordRuleCell.class;
    self.collectionView.section.isDynamicallySized = YES;
    self.collectionView.section.models = self.viewModel.passwordSection;
    
    [self.collectionView ehi_invalidateLayoutAnimated:YES completion:^(BOOL finished) {
        self.collectionViewHeight.constant = self.collectionView.collectionViewLayout.collectionViewContentSize.height;
    }];

    self.requiredInfoWarningContainer.viewModel = self.viewModel.requiredInfoViewModel;
}

- (BOOL)needsBottomLine
{
    return YES;
}

#pragma mark - Reactions

- (void)registerReactions:(EHIProfilePasswordViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title)                             : dest(self, .title),
        source(model.password)                          : dest(self, .passwordField.value),
        source(model.passwordConfirmation)              : dest(self, .passwordConfirmationField.value),
        source(model.hasValidPasswordAndConfirmation)   :^(NSNumber *isEnabled) {
                                                            self.changePasswordButton.enabled = isEnabled.boolValue;
                                                            [self invalidateViewBelowSafeArea:!isEnabled.boolValue];
                                                        },
        source(model.isLoading)                         : dest(self, .activityIndicator.isAnimating),
        source(model.updateAlert)                       : dest(self, .updateAlertLabel.text),
        source(model.passwordsDoNotMatch)               : dest(self, .inlineErrorLabel.text),
    });
    
    
    [MTRReactor autorun:self action:@selector(invalidateAlertBanner:)];
    [MTRReactor autorun:self action:@selector(invalidateInlineError:)];
    [MTRReactor autorun:self action:@selector(invalidatePassword:)];
}

- (void)invalidateAlertBanner:(MTRComputation *)computation
{
    MASLayoutPriority constraintPriority = self.viewModel.forceUpdatePassword ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    [self.alertContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
    }];
}

- (void)invalidateInlineError:(MTRComputation *)computation
{
    BOOL showError = self.viewModel.shouldShowInlineError;
    
    // update constraints
    self.inlineErrorHeight.isDisabled = !showError;
    
    UIView.animate(!computation.isFirstRun).duration(0.25f).transform(^{
        self.inlineErrorLabel.alpha = showError ? 1.0f : 0.0f;
        [self.view layoutIfNeeded];
    }).start(nil);
}

- (void)invalidatePassword:(MTRComputation *)computation
{
    BOOL shouldShowAlert = !self.viewModel.hasValidPassword && self.viewModel.shouldHighlightPasswordFieldIfNecessary;
    [self.passwordField setShowAlert:shouldShowAlert];
    
    // validate the requirements
    self.collectionView.section.models.each(^(EHIProfilePasswordRuleViewModel *model, NSInteger index) {
        [model invalidatePassword:self.viewModel.password shouldShowFailed:self.viewModel.shouldHighlightPasswordFieldIfNecessary];
    });
}

# pragma mark - Interface Actions

- (IBAction)fieldDidChangeValue:(EHISigninField *)field
{
    if(field == self.passwordField) {
        self.viewModel.password = field.value;
    } else {
        self.viewModel.passwordConfirmation = field.value;
    }
}

- (IBAction)didSelectChangePassword:(id)sender
{
    [self.viewModel changePassword];
}

# pragma mark - EHISigninFieldActions

- (void)didReturnForSigninField:(EHISigninField *)field
{
    if(field == self.passwordField) {
        [self.passwordConfirmationField becomeFirstResponder];
    } else {
        [field resignFirstResponder];
    }
}

- (void)didBeginEditingForSigninField:(EHISigninField *)field
{
    // move the view up a bit to show all the requirements when keyboard is visible
    if (field == self.passwordField) {
        [self.scrollView setContentOffset:(CGPoint){0, self.alertContainer.bounds.size.height} animated:YES];
    }
}

- (void)didEndEditingForSigninField:(EHISigninField *)field
{
    if (field == self.passwordField) {
        self.viewModel.shouldHighlightPasswordFieldIfNecessary = YES;
    }
}

# pragma mark - Keyboard

- (UIScrollView *)keyboardSupportedScrollView
{
    return self.scrollView;
}

- (UIButton *)keyboardSupportedActionButton
{
    return self.changePasswordButton;
}

- (BOOL)shouldDismissKeyboardForTouch:(UITouch *)touch
{
    return ![touch.view isEqual:self.passwordField.actionButton]
        && ![touch.view isEqual:self.passwordConfirmationField.actionButton];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenProfilePassword;
}

@end
