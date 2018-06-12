//
//  EHIProfileAuthenticateViewController.m
//  Enterprise
//
//  Created by fhu on 5/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIProfileAuthenticationViewController.h"
#import "EHIProfileAuthenticationViewModel.h"
#import "EHILabel.h"
#import "EHITextField.h"
#import "EHIActivityIndicator.h"
#import "EHIActionButton.h"
#import "EHISigninField.h"

@interface EHIProfileAuthenticationViewController () <EHISigninFieldActions>
@property (strong, nonatomic) EHIProfileAuthenticationViewModel *viewModel;
@property (weak, nonatomic) IBOutlet EHILabel *topLabel;
@property (weak, nonatomic) IBOutlet EHILabel *bottomLabel;
@property (weak, nonatomic) IBOutlet EHISigninField *passwordField;
@property (weak, nonatomic) IBOutlet EHIButton *forgotPasswordButton;
@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;
@property (weak, nonatomic) IBOutlet EHIActivityIndicator *activityIndicator;
@property (weak, nonatomic) IBOutlet EHIActionButton *continueButton;
@property (weak, nonatomic) IBOutlet EHIButton *cancelButton;

@property (assign, nonatomic) BOOL isSecured;
@end

@implementation EHIProfileAuthenticationViewController

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHIProfileAuthenticationViewModel new];
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.passwordField.model = self.viewModel.passwordFieldModel;
    self.cancelButton.type   = EHIButtonTypeSecondary;
    
    self.topLabel.attributedText = self.topLabel.attributedText.rebuild
        .lineSpacing(6.0).kerning(-1).string;
    self.bottomLabel.attributedText = self.bottomLabel.attributedText.rebuild
        .lineSpacing(6.0).string;
}

- (UIColor *)backgroundColor
{
    return [UIColor whiteColor];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIProfileAuthenticationViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.password)            : dest(self, .passwordField.value),
        source(model.isLoading)           : dest(self, .activityIndicator.isAnimating),
        source(model.titleText)           : dest(self, .topLabel.text),
        source(model.subtitleText)        : dest(self, .bottomLabel.text),
        source(model.forgotPasswordText)  : dest(self, .forgotPasswordButton.ehi_title),
        source(model.continueButtonText)  : dest(self, .continueButton.ehi_title),
        source(model.cancelButtonText)    : dest(self, .cancelButton.ehi_title),
        source(model.hasValidCredentials) : dest(self, .continueButton.enabled)
    });
}

#pragma mark - IB Actions

- (IBAction)passwordFieldValueChanged:(EHISigninField *)field
{
    self.viewModel.password = field.value;
}

- (IBAction)didSelectSignin:(id)sender
{
    [self.view endEditing:YES];
    [self.viewModel authenticateWithHandler:^(NSError *error) {
        if(!error) {
            [self.delegate profileAuthenticationViewControllerDidAuthenticate:self];
        }
    }];
}

- (IBAction)didSelectForgot:(id)sender
{
    [self.viewModel forgotPassword];
}

- (IBAction)didSelectCancel:(id)sender
{
    [self.viewModel cancel];
}

# pragma mark - EHISigninFieldActions

- (void)didReturnForSigninField:(EHISigninField *)signinField
{
    [signinField resignFirstResponder];
}

# pragma mark - Keyboard

- (UIScrollView *)keyboardSupportedScrollView
{
    return self.scrollView;
}

- (BOOL)shouldDismissKeyboardForTouch:(UITouch *)touch
{
    return ![touch.view isEqual:self.passwordField.actionButton];
}

@end
