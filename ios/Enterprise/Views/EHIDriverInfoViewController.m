//
//  EHIDriverInfoViewController.m
//  Enterprise
//
//  Created by Alex Koller on 4/8/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDriverInfoViewController.h"
#import "EHIDriverInfoViewModel.h"
#import "EHILabel.h"
#import "EHITextField.h"
#import "EHIButton.h"
#import "EHIToggleButton.h"
#import "EHIActionButton.h"
#import "EHIRestorableConstraint.h"
#import "EHIRequiredInfoView.h"
#import "EHIRequiredInfoFootnoteView.h"

@interface EHIDriverInfoViewController () <UITextFieldDelegate>
@property (strong, nonatomic) EHIDriverInfoViewModel *viewModel;

@property (weak  , nonatomic) IBOutlet UIScrollView *scrollView;

@property (weak  , nonatomic) IBOutlet UIView *signinContainer;
@property (weak  , nonatomic) IBOutlet EHILabel *signInTitle;
@property (weak  , nonatomic) IBOutlet EHIActionButton *signinButton;

@property (weak  , nonatomic) IBOutlet EHILabel *nameLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *displayNameLabel;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *nameFieldsContainerHeight;
@property (weak  , nonatomic) IBOutlet EHITextField *firstNameTextField;
@property (weak  , nonatomic) IBOutlet EHITextField *lastNameTextField;

@property (weak  , nonatomic) IBOutlet EHILabel *phoneLabel;
@property (weak  , nonatomic) IBOutlet EHITextField *phoneTextField;

@property (weak  , nonatomic) IBOutlet EHILabel *emailLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *emailDescriptionLabel;
@property (weak  , nonatomic) IBOutlet EHITextField *emailTextField;
@property (weak  , nonatomic) IBOutlet UIView *emailSignupContainer;
@property (weak  , nonatomic) IBOutlet EHIToggleButton *emailToggle;
@property (weak  , nonatomic) IBOutlet EHILabel *emailSignupLabel;

@property (weak  , nonatomic) IBOutlet UIView *confirmationContainer;
@property (weak  , nonatomic) IBOutlet EHILabel *confirmationLabel;

@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *saveContainerHeight;
@property (weak  , nonatomic) IBOutlet UIView *saveContainer;
@property (weak  , nonatomic) IBOutlet EHILabel *saveLabel;
@property (weak  , nonatomic) IBOutlet EHIToggleButton *saveToggle;
@property (weak  , nonatomic) IBOutlet EHIActionButton *doneButton;

@property (weak  , nonatomic) IBOutlet UIView *outsideVendorContainer;
@property (weak  , nonatomic) IBOutlet EHILabel *outsideVendorLabel;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *loadingIndicator;

@property (weak  , nonatomic) IBOutlet EHIRequiredInfoView *requiredInfoWarningContainer;
@property (weak  , nonatomic) IBOutlet EHIRequiredInfoFootnoteView *footnoteView;

@property (strong, nonatomic) IBOutletCollection(EHITextField) NSArray *textFields;
@end

@implementation EHIDriverInfoViewController

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIDriverInfoViewModel new];
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    // add next button to phone field's number pad keyboard
    self.phoneTextField.usesDoneToolbar = YES;
    
    // order text fields array by how they appear on screen
    self.textFields = self.textFields.sortBy(^(EHITextField *textField) { return textField.tag; });
    
    self.firstNameTextField.sensitive = YES;
    self.lastNameTextField.sensitive  = YES;
    self.phoneTextField.sensitive     = YES;

    self.requiredInfoWarningContainer.viewModel = self.viewModel.requiredFieldsModel;
    self.footnoteView.viewModel = self.viewModel.footnoteModel;
}

- (BOOL)needsBottomLine
{
    return NO;
}

# pragma mark - Accessibility

- (void)registerAccessibilityIdentifiers
{
    [super registerAccessibilityIdentifiers];
    
    self.firstNameTextField.accessibilityIdentifier = EHIDriverInfoFirstNameKey;
    self.lastNameTextField.accessibilityIdentifier  = EHIDriverInfoLastNameKey;
    self.phoneTextField.accessibilityIdentifier     = EHIDriverInfoPhoneNumberKey;
    self.emailTextField.accessibilityIdentifier     = EHIDriverInfoEmailKey;
    self.doneButton.accessibilityIdentifier         = EHIReservationFlowContinueKey;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIDriverInfoViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateLayout:)];
    [MTRReactor autorun:self action:@selector(invalidateFormErrors:)];
    [MTRReactor autorun:self action:@selector(invalidateConfirmationConstraint:)];

    model.bind.map(@{
        source(model.title)                 : dest(self, .title),
        
        source(model.nameTitle)             : dest(self, .nameLabel.text),
        source(model.displayNameTitle)      : dest(self, .displayNameLabel.text),
        source(model.firstName)             : dest(self, .firstNameTextField.text),
        source(model.firstNamePlaceholder)  : dest(self, .firstNameTextField.attributedPlaceholder),
        source(model.lastName)              : dest(self, .lastNameTextField.text),
        source(model.lastNamePlaceholder)   : dest(self, .lastNameTextField.attributedPlaceholder),

        source(model.signInBannerTitle)     : dest(self, .signInTitle),
        source(model.signInButtonTitle)     : dest(self, .signinButton.ehi_title),
        
        source(model.phoneTitle)            : dest(self, .phoneLabel.text),
        source(model.formattedPhone)        : dest(self, .phoneTextField.phoneModel),
        source(model.phonePlaceholder)      : dest(self, .phoneTextField.attributedPlaceholder),
        
        source(model.emailTitle)            : dest(self, .emailLabel.text),
        source(model.email)                 : dest(self, .emailTextField.text),
        source(model.emailPlaceholder)      : dest(self, .emailTextField.attributedPlaceholder),
        source(model.emailHelpTitle)        : dest(self, .emailDescriptionLabel.text),
        source(model.emailToggleTitle)      : dest(self, .emailSignupLabel.text),
        
        source(model.saveToggleTitle)       : dest(self, .saveLabel.attributedText),
        source(model.shouldSaveDriverInfo)  : dest(self, .saveToggle.selected),
        
        source(model.actionButtonTitle)     : dest(self, .doneButton.ehi_title),
       
        source(model.invalidDriverInfo)       : dest(self, .doneButton.isFauxDisabled),
        source(model.wantsEmailNotifications) : dest(self, .emailToggle.selected),
        source(model.confirmationTitle)       : dest(self, .confirmationLabel.text),
        
        source(model.outsideVendorMessage)    : dest(self, .outsideVendorLabel.text),
        
        source(model.isLoading)               : dest(self, .loadingIndicator.isAnimating),
    });
}

- (void)invalidateLayout:(MTRComputation *)computation
{
    BOOL hideNameContainer     = self.viewModel.hideNameContainer;
    BOOL hideSaveInfoContainer = self.viewModel.hideSaveInfoContainer;

    self.nameFieldsContainerHeight.isDisabled  = hideNameContainer;
    self.saveContainerHeight.isDisabled        = hideSaveInfoContainer;

    BOOL showSignInContainer = self.viewModel.showSignInContainer;
    BOOL showOutsideVendor   = self.viewModel.showOutsideVendor;
    BOOL showEmailOptIn      = !self.viewModel.hideEmailOptIn;

    MASLayoutPriority signInPriority        = showSignInContainer ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    MASLayoutPriority outsideVendorPriority = showOutsideVendor   ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    MASLayoutPriority emailSignupPriority   = showEmailOptIn      ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    
    [self.outsideVendorContainer mas_makeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0f).priority(outsideVendorPriority);
        [self.view layoutIfNeeded];
    }];

    [self.signinContainer mas_makeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0f).priority(signInPriority);
        [self.view layoutIfNeeded];
    }];

    [self.emailSignupContainer mas_makeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0f).priority(emailSignupPriority);
        [self.view layoutIfNeeded];
    }];
}

- (void)invalidateConfirmationConstraint:(MTRComputation *)computation
{
    MASLayoutPriority constraintPriority = self.viewModel.showConfirmationTitle ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    
    [self.confirmationContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
    }];
    
    UIView.animate(!computation.isFirstRun).duration(0.4f).transform(^{
        [self.view layoutIfNeeded];
    }).start(nil);
}

- (void)invalidateFormErrors:(MTRComputation *)computation
{
    UIColor *errorColor = [UIColor ehi_redColor];
    
    self.firstNameTextField.borderColor = self.viewModel.showFirstNameError ? errorColor : nil;
    self.lastNameTextField.borderColor  = self.viewModel.showLastNameError ? errorColor : nil;
    self.phoneTextField.borderColor     = self.viewModel.showPhoneError ? errorColor : nil;
    self.emailTextField.borderColor     = self.viewModel.showEmailError ? errorColor : nil;
}

# pragma mark - UITextFieldDelegate

- (BOOL)textFieldShouldClear:(UITextField *)textField
{
    // set appropriate driverInfo property
    if([textField isEqual:self.firstNameTextField]) {
        self.viewModel.firstName = nil;
    } else if ([textField isEqual:self.lastNameTextField]) {
        self.viewModel.lastName = nil;
    } else if ([textField isEqual:self.phoneTextField]) {
        self.viewModel.phone = nil;
    } else if ([textField isEqual:self.emailTextField]) {
        self.viewModel.email = nil;
    }
    
    // let viewModel update via reactions
    return NO;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    // find next empty text field
    EHITextField *newTextField = self.textFields.find(^(EHITextField *newTextField) {
        return newTextField.tag > textField.tag && newTextField.text.length == 0;
    });

    if(newTextField) {
        [newTextField becomeFirstResponder];
    } else {
        [textField resignFirstResponder];
    }
    
    return YES;
}

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    NSString *newText = [textField.text stringByReplacingCharactersInRange:range withString:string];
    
    // filter empty strings
    if(newText.length == 0) {
        newText = nil;
    }
    
    // set appropriate driverInfo property
    if([textField isEqual:self.firstNameTextField]) {
        self.viewModel.firstName = newText;
    } else if ([textField isEqual:self.lastNameTextField]) {
        self.viewModel.lastName = newText;
    } else if ([textField isEqual:self.phoneTextField]) {
        self.viewModel.phone = newText;
    } else if ([textField isEqual:self.emailTextField]) {
        self.viewModel.email = newText;
    }
    
    // force reactions to run, this is necessary because iOS's quicktype cycle is faster than reactor's flush
    [[MTRReactor reactor] flush];
    
    // let viewModel update via reactions
    return NO;
}

# pragma mark - Actions

- (IBAction)didTapEmailToggle:(id)sender
{
    [self.viewModel toggleEmailNotifications];
}

- (IBAction)didTapSaveToggle:(id)sender
{
    self.viewModel.shouldSaveDriverInfo = !self.saveToggle.selected;
}

- (IBAction)didTapDoneButton:(id)sender
{
    [self.viewModel commitDriverInfo];
}

- (IBAction)didTapSignInButton:(id)sender
{
    [self.viewModel presentSignIn];
}

# pragma mark - Keyboard

- (UIScrollView *)keyboardSupportedScrollView
{
    return self.scrollView;
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenReservationReview state:EHIScreenReservationDriverInfo];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenReservationDriverInfo;
}
@end
