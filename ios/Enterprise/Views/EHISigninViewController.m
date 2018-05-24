//
//  EHISigninViewController.m
//  Enterprise
//
//  Created by Ty Cobb on 4/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHISigninViewController.h"
#import "EHISigninViewModel.h"
#import "EHISigninField.h"
#import "EHIBarButtonItem.h"
#import "EHIActionButton.h"
#import "EHILabel.h"
#import "EHIRestorableConstraint.h"
#import "EHIUserManager.h"

@interface EHISigninViewController () <EHISigninFieldActions>
@property (strong, nonatomic) EHISigninViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *navigationHeightConstraint;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UIScrollView *scrollView;
@property (weak  , nonatomic) IBOutlet UILabel *headerInfoLabel;
@property (weak  , nonatomic) IBOutlet UILabel *rememberLabel;
@property (weak  , nonatomic) IBOutlet UIButton *rememberButton;
@property (weak  , nonatomic) IBOutlet EHIButton *forgotUsernameButton;
@property (weak  , nonatomic) IBOutlet EHIButton *forgotPasswordButton;
@property (weak  , nonatomic) IBOutlet EHIButton *partialEnrollmentButton;
@property (weak  , nonatomic) IBOutlet EHIButton *joinNowButton;
@property (weak  , nonatomic) IBOutlet EHIButton *emeraldClubButton;
@property (weak  , nonatomic) IBOutlet EHIButton *closeButton;
@property (weak  , nonatomic) IBOutlet EHIActionButton *actionButton;
@property (weak  , nonatomic) IBOutlet UIView *usernameContainer;
@property (weak  , nonatomic) IBOutlet EHISigninField *usernameField;
@property (weak  , nonatomic) IBOutlet EHISigninField *passwordField;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *headerInfoTop;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *forgotNumberButtonHeight;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *arrowImageWidthConstraint;
@property (weak  , nonatomic) IBOutlet UIView *bottomContainer;
@property (strong, nonatomic) IBOutletCollection(EHISigninField) NSArray *fields;
// debug
@property (weak  , nonatomic) IBOutlet UISegmentedControl *signInOptionsSegmentControl;
@end

@implementation EHISigninViewController

- (void)updateWithAttributes:(NAVAttributes *)attributes
{
    [super updateWithAttributes:attributes];
    
    // if not provided by navigation, create our own
    if(!self.viewModel) {
        self.viewModel = [EHISigninViewModel new];
    }
    
    NSAssert([self.viewModel isKindOfClass:[EHISigninViewModel class]], @"navigation object must be a signin view model or nil!");
    
    // since this screen is used in serveral places, sometimes, when another screen is presented in front of it, modaly, the handler is overwritten with `nil`.
    if(!self.viewModel.signinHandler) {
        self.viewModel.signinHandler = attributes.handler;
    }
}

- (void)updateNavigationItem:(UINavigationItem *)item
{
    [super updateNavigationItem:item];
    
    BOOL inEnroll = self.viewModel.layout == EHISigninLayoutEnrollment;
    if(!inEnroll) {
        item.rightBarButtonItem = [EHIBarButtonItem buttonWithType:EHIButtonTypeClose target:self action:@selector(didTapCloseButton:)];
    }
}

# pragma mark - View Lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    // configure the close button
    self.closeButton.type = EHIButtonTypeClose;
    
    // configure join enterprise button
    [self.joinNowButton setType:EHIButtonTypeSecondary];
    
    self.emeraldClubButton.titleLabel.numberOfLines = 0;
   
    // populate the titles/placeholders for the fields
    self.fields = self.fields.sortBy(^(EHISigninField *field) {
        return field.tag;
    }).each(^(EHISigninField *field) {
        field.model = self.viewModel.fieldModels[field.tag];
    });
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveUserSignInOptionChangedNotification:) name:EHIUserSignInOptionChangedNotification object:nil];
    
    // hide debug elements in production build!
#if defined(DEBUG) || defined(UAT)
    self.signInOptionsSegmentControl.hidden = NO;
#endif
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

# pragma mark - Reactions

- (void)registerReactions:(EHISigninViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateHeaderInfoLabel:)];
    [MTRReactor autorun:self action:@selector(invalidateArrowImage:)];
    [MTRReactor autorun:self action:@selector(invalidateLayout:)];
    
    model.bind.map(@{
        source(model.title)                   : ^(NSString *title){
                                                    self.title = title;
                                                    self.titleLabel.text = title;
                                                },
		source(model.staySignedInTitle)       : dest(self, .rememberLabel.text),
        source(model.identification)          : dest(self, .usernameField.value),
        source(model.password)                : dest(self, .passwordField.value),
        source(model.actionTitle)             : dest(self, .actionButton.ehi_title),
        source(model.isLoading)               : ^(NSNumber *isLoading){
                                                    self.actionButton.isLoading = isLoading.boolValue;
                                                    self.actionButton.isFauxDisabled = isLoading.boolValue;
                                                },
        source(model.forgotUsernameTitle)     : dest(self, .forgotUsernameButton.ehi_title),
        source(model.forgotPasswordTitle)     : dest(self, .forgotPasswordButton.ehi_title),
        source(model.partialEnrollmentTitle)  : dest(self, .partialEnrollmentButton.ehi_title),
        source(model.joinNowTitle)            : dest(self, .joinNowButton.ehi_title),
        source(model.emeraldClubMembersTitle) : dest(self, .emeraldClubButton.ehi_title),
        source(model.hasValidCredentials)     : dest(self, .actionButton.enabled),
        source(model.remembersCredentials)    : dest(self, .rememberButton.selected),
    });
}

- (void)invalidateHeaderInfoLabel:(MTRComputation *)computation
{
    self.headerInfoLabel.text = self.viewModel.headerInfoText;
    self.headerInfoTop.isDisabled = !self.viewModel.headerInfoText.length;
}

- (void)invalidateArrowImage:(MTRComputation *)computation
{
    self.arrowImageWidthConstraint.isDisabled = self.viewModel.hideArrowImage;
}

- (void)invalidateLayout:(MTRComputation *)computation
{
    BOOL isEnroll = self.viewModel.layout == EHISigninLayoutEnrollment;
    
    self.navigationHeightConstraint.isDisabled = YES;
    self.forgotNumberButtonHeight.isDisabled   = isEnroll;
    
    MASLayoutPriority priority = isEnroll ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    
    [self.usernameContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0).priority(priority);
    }];
    
    [self.bottomContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0).priority(priority);
    }];
}

# pragma mark - Interface Actions

- (IBAction)fieldDidChangeValue:(EHISigninField *)field
{
    [self.viewModel setValue:field.value forFieldWithType:field.tag];
}

- (IBAction)didTapSigninButton:(UIButton *)button
{
    [self.view endEditing:YES];
    [self.viewModel initiateSignin];
}

- (IBAction)didTapCloseButton:(UIButton *)button
{
    [self.viewModel closeSignin];
}

- (IBAction)didTapRememberField:(UITapGestureRecognizer *)gesture
{
    [self.viewModel toggleRemembersCredentials];
}

- (IBAction)didTapForgotUsernameButton:(UIButton *)button
{
    [self.viewModel showRecoveryModalOrWebBrowserWithType:EHISigninRecoveryTypeUsername];
}

- (IBAction)didTapForgotPasswordButton:(UIButton *)button
{
    [self.viewModel showForgotPasswordScreen];
}

- (IBAction)didTapPartialEnrollmentButton:(UIButton *)button
{
    [self.viewModel showRecoveryModalOrWebBrowserWithType:EHISigninRecoveryTypePartialEnrollment];
}

- (IBAction)didTapJoinNowButton:(UIButton *)button
{
    [self.viewModel showJoinNowScreen];
}

- (IBAction)didTapEmeraldClubMembers:(UIButton *)button
{
    [self.viewModel didTapEmeraldClubMembers];
}


# pragma mark - EHISigninFieldActions

- (void)didReturnForSigninField:(EHISigninField *)signinField
{
    if(signinField == self.usernameField) {
        [self.passwordField becomeFirstResponder];
    } else {
        [signinField resignFirstResponder];
    }
}

# pragma mark - Keyboard

- (UIScrollView *)keyboardSupportedScrollView
{
    return self.scrollView;
}

# pragma mark - Debug

- (void)didReceiveUserSignInOptionChangedNotification:(NSNotification *)notification
{
    self.signInOptionsSegmentControl.selectedSegmentIndex = [EHIUserManager sharedInstance].signInOption;
}

- (IBAction)didChangeSignInOptionsSegmentControl:(UISegmentedControl *)sender
{
    [EHIUserManager sharedInstance].signInOption = sender.selectedSegmentIndex;
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenSignin state:EHIScreenSignin];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenSignin;
}

@end
