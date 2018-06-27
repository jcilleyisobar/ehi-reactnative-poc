//
//  EHIEnrollmentPasswordCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 8/10/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIEnrollmentPasswordCell.h"
#import "EHIEnrollmentPasswordViewModel.h"
#import "EHILabel.h"
#import "EHIToggleButton.h"
#import "EHISigninField.h"

@interface EHIEnrollmentPasswordCell () <EHISigninFieldActions>
@property (strong, nonatomic) EHIEnrollmentPasswordViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@property (weak  , nonatomic) IBOutlet EHISigninField *passwordField;
@property (weak  , nonatomic) IBOutlet EHILabel *termsLabel;
@property (weak  , nonatomic) IBOutlet EHIToggleButton *termsToggle;
@property (weak  , nonatomic) IBOutlet UILabel *passwordDontMatchLabel;
@property (weak  , nonatomic) IBOutlet UIView *passwordDontMatchView;
@property (weak  , nonatomic) IBOutlet UIView *termsContainerView;
@end

@implementation EHIEnrollmentPasswordCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIEnrollmentPasswordViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.passwordField.alertColor = [UIColor ehi_redColor];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIEnrollmentPasswordViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateTermsContainer:)];
    [MTRReactor autorun:self action:@selector(invalidateFieldAlert:)];
    [MTRReactor autorun:self action:@selector(invalidatePasswordsDontMatchView:)];
    
    model.bind.map(@{
        source(model.signinModel)         : dest(self, .passwordField.model),
        source(model.password)            : dest(self, .passwordField.value),
        source(model.termsRead)           : dest(self, .termsToggle.selected),
        source(model.terms)               : dest(self, .termsLabel.attributedText),
        source(model.passwordsDoNotMatch) : dest(self, .passwordDontMatchLabel.text),
    });
}

- (void)invalidateTermsContainer:(MTRComputation *)computation
{
    BOOL hideTerms = self.viewModel.hideTerms;
    
    MASLayoutPriority priority = hideTerms ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    
    [self.termsContainerView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0).priority(priority);
    }];
}

- (void)invalidateFieldAlert:(MTRComputation *)computation
{
    BOOL showAlert = self.viewModel.showAlert;
    
    self.passwordField.showAlert = showAlert;
}

- (void)invalidatePasswordsDontMatchView:(MTRComputation *)computation
{
    BOOL showView = self.viewModel.showPasswordsDontMatch;
    
    MASLayoutPriority priority = showView ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    [self.passwordDontMatchView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0f).priority(priority);
    }];
    
    UIView.animate(!computation.isFirstRun).duration(0.25f).transform(^{
        self.passwordDontMatchLabel.alpha = showView ? 1.0f : 0.0f;
        [self layoutIfNeeded];
    }).start(nil);
    
    [self ehi_performAction:@selector(passwordCellDidShowNoMatch:) withSender:self];
}

- (BOOL)becomeFirstResponder
{
    return [self.passwordField becomeFirstResponder];
}

# pragma mark - EHISigninFieldActions

- (void)didReturnForSigninField:(EHISigninField *)signinField
{
    [signinField resignFirstResponder];
    
    [self ehi_performAction:@selector(passwordCellWillDismissKeyboard:) withSender:self];
}

# pragma mark - Actions

- (IBAction)passwordFieldValueChanged:(EHISigninField *)field
{
    self.viewModel.password = field.value;
    
    // invalidate alert when the user starts typing again
    BOOL isShowingAlert = self.viewModel.showAlert;
    if(isShowingAlert) {
        self.passwordField.showAlert = NO;
    }
}

- (IBAction)didTapTermsButton:(id)sender
{
    [self.viewModel toggleReadTerms];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetHeight(self.containerView.frame) + EHIMediumPadding
    };
}

@end
