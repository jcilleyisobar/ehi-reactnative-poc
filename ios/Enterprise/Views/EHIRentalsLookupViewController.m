//
//  EHIRentalsLookupViewController.m
//  Enterprise
//
//  Created by fhu on 5/7/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIRentalsLookupViewController.h"
#import "EHIRentalsLookupViewModel.h"
#import "EHIButton.h"
#import "EHIActionButton.h"
#import "EHIRestorableConstraint.h"
#import "EHIActivityIndicator.h"
#import "EHIUserManager+Analytics.h"
#import "EHIRequiredInfoView.h"

@interface EHIRentalsLookupViewController () <UITextFieldDelegate>
@property (strong, nonatomic) EHIRentalsLookupViewModel *viewModel;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *firstNameLabel;
@property (weak, nonatomic) IBOutlet UILabel *lastNameLabel;
@property (weak, nonatomic) IBOutlet UILabel *confirmationLabel;
@property (weak, nonatomic) IBOutlet UILabel *errorTitle;
@property (weak, nonatomic) IBOutlet UILabel *errorDetails;
@property (weak, nonatomic) IBOutlet UITextField *firstNameField;
@property (weak, nonatomic) IBOutlet UITextField *lastNameField;
@property (weak, nonatomic) IBOutlet UITextField *confirmationField;
@property (weak, nonatomic) IBOutlet EHIButton *closeButton;
@property (weak, nonatomic) IBOutlet EHIButton *contactButton;
@property (weak, nonatomic) IBOutlet EHIActionButton *findRentalButton;
@property (weak, nonatomic) IBOutlet EHIActivityIndicator *activityIndicator;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *errorContainerTop;
@property (weak, nonatomic) IBOutlet UIView *errorContainer;
@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;
@property (weak, nonatomic) IBOutlet EHIRequiredInfoView *requiredInfoWarningContainer;
@end

@implementation EHIRentalsLookupViewController

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHIRentalsLookupViewModel new];
    }
    
    return self;
}

# pragma mark - View Lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.contactButton.layer.borderColor = [UIColor ehi_greenColor].CGColor;
    self.contactButton.layer.borderWidth = 1.0f;
    self.contactButton.imageHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
    self.closeButton.type = EHIButtonTypeClose;
    self.requiredInfoWarningContainer.viewModel = self.viewModel.requiredFieldsModel;
}

# pragma mark - Accessibility

- (void)registerAccessibilityIdentifiers
{
    [super registerAccessibilityIdentifiers];
    
    self.confirmationField.accessibilityIdentifier = EHILookupRentalConfirmationKey;
    self.firstNameField.accessibilityIdentifier    = EHILookupRentalFirstNameKey;
    self.lastNameField.accessibilityIdentifier     = EHILookupRentalLastNameKey;
    self.findRentalButton.accessibilityIdentifier  = EHILookupRentalFindRentalKey;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIRentalsLookupViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateError:)];
    [MTRReactor autorun:self action:@selector(invalidateButton:)];
    
    model.bind.map(@{
        source(model.title)             : dest(self, .titleLabel.text),
        source(model.confirmation)      : dest(self, .confirmationField.text),
        source(model.confirmationTitle) : dest(self, .confirmationLabel.attributedText),
        source(model.firstName)         : dest(self, .firstNameField.text),
        source(model.firstNameTitle)    : dest(self, .firstNameLabel.text),
        source(model.lastName)          : dest(self, .lastNameField.text),
        source(model.lastNameTitle)     : dest(self, .lastNameLabel.text),
        source(model.actionTitle)       : dest(self, .findRentalButton.ehi_title),
        source(model.callButtonTitle)   : dest(self, .contactButton.ehi_title),
        source(model.isLoading)         : dest(self, .activityIndicator.isAnimating),
    });
}

- (void)invalidateError:(MTRComputation *)computation
{
    NSError *error = self.viewModel.error;
 
    // update error labels
    self.errorTitle.attributedText   = [self titleForLabel:self.errorTitle text:error.userInfo[EHIErrorTitleKey]];
    self.errorDetails.attributedText = [self titleForLabel:self.errorDetails text:error.userInfo[EHIErrorDetailsKey]];
   
    // show the error if we have anything
    [self.errorContainer layoutIfNeeded];
    [self.errorContainerTop setConstant:error.localizedDescription ? 0.0f : -self.errorContainer.bounds.size.height];
}

- (void)invalidateButton:(MTRComputation *)computation
{
    BOOL isEnabled = self.viewModel.confirmation.length && self.viewModel.firstName.length && self.viewModel.lastName.length;
    
    self.findRentalButton.enabled = isEnabled;
}

//
// Helpers
//

- (NSAttributedString *)titleForLabel:(UILabel *)label text:(NSString *)text
{
    return !text ? nil : [EHIAttributedStringBuilder new].text(text)
        .font(label.font).color(label.textColor)
        .lineSpacing(5.0).string;
}

# pragma mark - Interface Actions

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    NSString *newText = [textField.text stringByReplacingCharactersInRange:range withString:string];

    // set appropriate driverInfo property
    if([textField isEqual:self.confirmationField]) {
        self.viewModel.confirmation = newText;
    } else if ([textField isEqual:self.firstNameField]) {
        self.viewModel.firstName = newText;
    } else if ([textField isEqual:self.lastNameField]) {
        self.viewModel.lastName = newText;
    }
        
    
    // force reactions to run, this is necessary because iOS's quicktype cycle is faster than reactor's flush
    [[MTRReactor reactor] flush];
    
    // let viewModel update via reactions
    return NO;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    if([textField isEqual:self.confirmationField]) {
        [self.firstNameField becomeFirstResponder];
    } else if([textField isEqual:self.firstNameField]) {
        [self.lastNameField becomeFirstResponder];
    } else if([textField isEqual:self.lastNameField]) {
        [textField resignFirstResponder];
    }
    
    return NO;
}

- (IBAction)didSelectClose:(id)sender
{
    [self.view endEditing:YES];
    [self.viewModel closeRental];
}

- (IBAction)didSelectFindRental:(id)sender
{
    [self.view endEditing:YES];
    [self.viewModel findRental];
}

- (IBAction)didSelectContactButton:(UIButton *)button
{
    [self.viewModel callContactNumber];
}

# pragma mark - EHIKeyboardResponder

- (UIScrollView *)keyboardSupportedScrollView
{
    return self.scrollView;
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenRentals state:EHIScreenRentalLookup];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenRentalLookup;
}

@end
