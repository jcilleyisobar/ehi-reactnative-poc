//
//  EHIEmeraldClubSignInViewController.mViewController
//  Enterprise
//
//  Created by Rafael Ramos on 6/2/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIEmeraldClubSignInViewController.h"
#import "EHIEmeraldClubSignInViewModel.h"
#import "EHIButton.h"
#import "EHIActionButton.h"
#import "EHISigninField.h"
#import "EHIRestorableConstraint.h"

@interface EHIEmeraldClubSignInViewController ()
@property (strong, nonatomic) EHIEmeraldClubSignInViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *navigationHeightConstraint;
@property (weak  , nonatomic) IBOutlet UIScrollView *scrollView;
@property (weak  , nonatomic) IBOutlet UIView *usernameContainer;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *headerInfoLabel;
@property (weak  , nonatomic) IBOutlet UILabel *subheaderInfoLabel;
@property (weak  , nonatomic) IBOutlet UILabel *rememberLabel;
@property (weak  , nonatomic) IBOutlet UIButton *rememberButton;
@property (weak  , nonatomic) IBOutlet EHIButton *closeButton;
@property (weak  , nonatomic) IBOutlet EHIButton *forgotPasswordButton;
@property (weak  , nonatomic) IBOutlet EHIActionButton *addAccountButton;
@property (weak  , nonatomic) IBOutlet EHISigninField *usernameField;
@property (weak  , nonatomic) IBOutlet EHISigninField *passwordField;
@property (strong, nonatomic) IBOutletCollection(EHISigninField) NSArray *fields;
@end

@implementation EHIEmeraldClubSignInViewController

- (void)updateWithAttributes:(NAVAttributes *)attributes
{
    [super updateWithAttributes:attributes];
    
    // if not provided by navigation, create our own
    if(!self.viewModel) {
        self.viewModel = [EHIEmeraldClubSignInViewModel new];
    }
    
    // since this screen is used in serveral places, sometimes, when another screen is presented in front of it, modaly, the handler is overwritten with `nil`.
    if(!self.viewModel.signinHandler) {
        self.viewModel.signinHandler = attributes.handler;
    }
}

# pragma mark - View Lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    // configure the close button
    self.closeButton.type = EHIButtonTypeClose;
    
    // populate the titles/placeholders for the fields
    self.fields = self.fields.sortBy(^(EHISigninField *field) {
        return field.tag;
    }).each(^(EHISigninField *field) {
        field.model = self.viewModel.fieldModels[field.tag];
    });
}

# pragma mark - Reactions

- (void)registerReactions:(EHIEmeraldClubSignInViewModel *)model
{
    [super registerReactions:model];

    [MTRReactor autorun:self action:@selector(invalidateLayout:)];
    
    model.bind.map(@{
        source(model.title)                   : ^(NSString *title){
                                                    self.titleLabel.text = title;
                                                    self.title = title;
                                                },
        source(model.headerInfoText)          : dest(self, .headerInfoLabel.text),
        source(model.subheaderInfoText)       : dest(self, .subheaderInfoLabel.text),
        source(model.staySignedInTitle)       : dest(self, .rememberLabel.text),
        source(model.identification)          : dest(self, .usernameField.value),
        source(model.password)                : dest(self, .passwordField.value),
        source(model.addAccountTitle)         : dest(self, .addAccountButton.ehi_title),
        source(model.isLoading)               : ^(NSNumber *isLoading){
                                                    self.addAccountButton.isLoading = isLoading.boolValue;
                                                    self.addAccountButton.isFauxDisabled = isLoading.boolValue;
                                                },
        source(model.forgotPasswordTitle)     : dest(self, .forgotPasswordButton.ehi_title),
        source(model.hasValidCredentials)     : dest(self, .addAccountButton.enabled),
        source(model.remembersCredentials)    : dest(self, .rememberButton.selected),
    });
}

- (void)invalidateLayout:(MTRComputation *)computation
{
    BOOL isEnroll = self.viewModel.layout == EHISigninLayoutEnrollment;

    MASLayoutPriority priority = isEnroll ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    
    [self.usernameContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0).priority(priority);
    }];
}

# pragma mark - Actions

- (IBAction)fieldDidChangeValue:(EHISigninField *)field
{
    [self.viewModel setValue:field.value forFieldWithType:field.tag];
}

- (IBAction)didTapRememberField:(UITapGestureRecognizer *)gesture
{
    [self.viewModel toggleRemembersCredentials];
}

- (IBAction)didTapSigninButton:(UIButton *)button
{
    [self.view endEditing:YES];
    [self.viewModel initiateSignin];
}

- (IBAction)didTapForgotPassword:(UIButton *)button
{
    [self.view endEditing:YES];
    [self.viewModel didTapForgotPassword];
}

- (IBAction)didTapCloseButton:(UIButton *)button
{
    [self.viewModel cancelSignIn];
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

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenSigninEmerald state:EHIScreenSigninEmerald];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenSigninEmerald;
}

@end
