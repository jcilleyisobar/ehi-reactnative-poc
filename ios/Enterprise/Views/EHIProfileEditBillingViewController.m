//
//  EHIProfileEditBillingViewController.mViewController
//  Enterprise
//
//  Created by Rafael Ramos on 9/29/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIProfileEditBillingViewController.h"
#import "EHIProfileEditPaymentViewModel.h"
#import "EHITextField.h"
#import "EHIToggleButton.h"
#import "EHIBarButtonItem.h"
#import "EHIActivityIndicator.h"

@interface EHIProfileEditBillingViewController () <UITextFieldDelegate>
@property (strong, nonatomic) EHIProfileEditPaymentViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *loadingIndicator;
@property (weak  , nonatomic) IBOutlet UIScrollView *scrollView;
@property (weak  , nonatomic) IBOutlet UILabel *billingNameLabel;
@property (weak  , nonatomic) IBOutlet EHITextField *billingTextField;
@property (weak  , nonatomic) IBOutlet UILabel *billingNumberTitleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *billingNumberLabel;
@property (weak  , nonatomic) IBOutlet UILabel *warningLabel;
@property (weak  , nonatomic) IBOutlet UIView *makePreferredContainer;
@property (weak  , nonatomic) IBOutlet EHIToggleButton *togglePreferred;
@property (weak  , nonatomic) IBOutlet UILabel *togglePreferredLabel;

@property (weak  , nonatomic) IBOutlet UIView *preferredContainer;
@property (weak  , nonatomic) IBOutlet UILabel *preferredLabel;

@property (weak  , nonatomic) IBOutlet EHIButton *saveButton;
@end

@implementation EHIProfileEditBillingViewController

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIProfileEditPaymentViewModel new];
    }

    return self;
}

- (void)updateNavigationItem:(UINavigationItem *)item
{
    [super updateNavigationItem:item];
    
    item.rightBarButtonItem = [EHIBarButtonItem buttonWithType:EHIButtonTypeDelete target:self action:@selector(didTapDeleteButton:)];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.billingTextField.returnKeyType = UIReturnKeyDone;
    self.billingTextField.autocapitalizationType = UITextAutocapitalizationTypeWords;
}

- (UIColor *)backgroundColor
{
    return [UIColor whiteColor];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIProfileEditPaymentViewModel *)model
{
    [super registerReactions:model];

    [MTRReactor autorun:self action:@selector(invalidatePreferred:)];
    
    model.bind.map(@{
        source(model.title)                 : dest(self, .title),
        source(model.paymentAliasTitle)     : dest(self, .billingNameLabel.text),
        source(model.paymentNumberTitle)    : dest(self, .billingNumberTitleLabel.text),
        source(model.maskedNumber)          : dest(self, .billingNumberLabel.text),
        source(model.isPreferred)           : dest(self, .togglePreferred.selected),
        source(model.togglePreferredTitle)  : dest(self, .togglePreferredLabel.text),
        source(model.preferredTitle)        : dest(self, .preferredLabel.text),
        source(model.saveTitle)             : dest(self, .saveButton.ehi_title),
        source(model.alias)                 : dest(self, .billingTextField.text),
        source(model.isLoading)             : dest(self, .loadingIndicator.isAnimating)
    });
}

- (void)invalidatePreferred:(MTRComputation *)computation
{
    BOOL hideToggle = self.viewModel.hideTogglePrefereed;
    
    MASLayoutPriority priority = hideToggle ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    
    [self.makePreferredContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0).priority(priority);
    }];
    
    priority = hideToggle ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    
    [self.preferredContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0).priority(priority);
    }];
}

# pragma mark - Actions

- (IBAction)didTapSave:(id)sender
{
    [self.viewModel saveChanges];
}

- (void)didTapDeleteButton:(UIBarButtonItem *)sender
{
    [self.viewModel deletePayment];
}

- (IBAction)didTapToggleDefault:(UITapGestureRecognizer *)gesture
{
    [self.viewModel togglePreferred];
}

# pragma mark - UITextFieldDelegate

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    NSString *oldText = self.viewModel.alias ?: @"";
    
    // set the value without side effects, so that the reaction can update the field
    self.viewModel.alias = [oldText stringByReplacingCharactersInRange:range withString:string];
    
    // force reactions to run, this is necessary because iOS's quicktype cycle is faster than reactor's flush
    [[MTRReactor reactor] flush];
    
    return NO;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    return [textField resignFirstResponder];
}

# pragma mark - Keyboard

- (UIScrollView *)keyboardSupportedScrollView
{
    return self.scrollView;
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenProfileEditPaymentBilling state:EHIScreenProfileEditPaymentBilling];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenProfileEditPaymentBilling;
}

@end
