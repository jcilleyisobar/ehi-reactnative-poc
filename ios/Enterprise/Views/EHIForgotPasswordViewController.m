//
//  EHIForgotPasswordViewController.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/14/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIForgotPasswordViewController.h"
#import "EHIForgotPasswordViewModel.h"
#import "EHIActionButton.h"
#import "EHITextField.h"
#import "EHIRequiredInfoView.h"

@interface EHIForgotPasswordViewController () <UITextFieldDelegate>
@property (strong, nonatomic) EHIForgotPasswordViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *navigationTitle;
@property (weak  , nonatomic) IBOutlet UILabel *instructionsLabel;
@property (weak  , nonatomic) IBOutlet UILabel *firstNameLabel;
@property (weak  , nonatomic) IBOutlet UILabel *lastNameLabel;
@property (weak  , nonatomic) IBOutlet UILabel *emailAddressLabel;

@property (weak  , nonatomic) IBOutlet EHITextField *firstNameField;
@property (weak  , nonatomic) IBOutlet EHITextField *lastNameField;
@property (weak  , nonatomic) IBOutlet EHITextField *emailAddressField;

@property (weak  , nonatomic) IBOutlet EHIRequiredInfoView *requiredInfoWaringContainer;

@property (weak  , nonatomic) IBOutlet EHIActionButton *submitButton;
@property (weak  , nonatomic) IBOutlet EHIButton *closeButton;

@property (weak  , nonatomic) IBOutlet UIScrollView *scrollView;
@end

@implementation EHIForgotPasswordViewController

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIForgotPasswordViewModel new];
    }
    
    return self;
}

# pragma  mark - View Lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.closeButton.type = EHIButtonTypeClose;
    self.firstNameField.alertBorderColor    = [UIColor ehi_redColor];
    self.lastNameField.alertBorderColor     = [UIColor ehi_redColor];
    self.emailAddressField.alertBorderColor = [UIColor ehi_redColor];

    self.requiredInfoWaringContainer.viewModel = self.viewModel.requiredInfoModel;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIForgotPasswordViewModel *)model
{
    [super registerReactions:model];

    model.bind.map(@{
        source(model.title)                 : dest(self, .navigationTitle.text),
        source(model.instructionsTitle)     : dest(self, .instructionsLabel.text),
        source(model.firstNameTitle)        : dest(self, .firstNameLabel.text),
        source(model.lastNameTitle)         : dest(self, .lastNameLabel.text),
        source(model.emailAddressTitle)     : dest(self, .emailAddressLabel.text),
        source(model.submitTitle)           : dest(self, .submitButton.ehi_title),
        source(model.isLoading)             : dest(self, .submitButton.isLoading),
        
        source(model.firstName)             : dest(self, .firstNameField.text),
        source(model.lastName)              : dest(self, .lastNameField.text),
        source(model.emailAddress)          : dest(self, .emailAddressField.text),
        source(model.hasErrorFirstName)     : dest(self, .firstNameField.showsAlertBorder),
        source(model.hasErrorLastName)      : dest(self, .lastNameField.showsAlertBorder),
        source(model.hasErrorEmail)         : dest(self, .emailAddressField.showsAlertBorder),
        source(model.hasErrors)             : dest(self, .submitButton.isFauxDisabled),
        
    });
}

# pragma mark - UITextFieldDelegate

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    NSString *newText = [textField.text stringByReplacingCharactersInRange:range withString:string];
    
    if([textField isEqual:self.firstNameField]) {
        self.viewModel.firstName = newText;
    } else if([textField isEqual:self.lastNameField]) {
        self.viewModel.lastName = newText;
    } else if([textField isEqual:self.emailAddressField]) {
        self.viewModel.emailAddress = newText;
    }
    
    // force reactions to run, this is necessary because iOS's quicktype cycle is faster than reactor's flush
    [[MTRReactor reactor] flush];
    
    return NO;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    if([textField isEqual:self.firstNameField]) {
        [self.lastNameField becomeFirstResponder];
    } else if([textField isEqual:self.lastNameField]) {
        [self.emailAddressField becomeFirstResponder];
    } else if([textField isEqual:self.emailAddressField]) {
        [textField resignFirstResponder];
    }
    
    return NO;
}

# pragma mark - Actions

- (IBAction)didTapCloseButton:(UIButton *)button
{
    [self.viewModel dismiss];
}

- (IBAction)didTapSubmit:(id)sender
{
    [self.view endEditing:YES];
    [self.viewModel submit];
}

# pragma mark - EHIKeyboardResponder

- (UIScrollView *)keyboardSupportedScrollView
{
    return self.scrollView;
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenSignin state:EHIScreenForgotPassword];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenForgotPassword;
}

@end
