//
//  EHIPaymentViewController.m
//  Enterprise
//
//  Created by Alex Koller on 1/13/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPaymentViewController.h"
#import "EHIPaymentViewModel.h"
#import "EHIPaymentInputView.h"
#import "EHIActionButton.h"
#import "EHILabel.h"
#import "EHIToggleButton.h"
#import "EHIRestorableConstraint.h"

@interface EHIPaymentViewController ()
@property (strong, nonatomic) EHIPaymentViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIScrollView *scrollView;
@property (weak  , nonatomic) IBOutlet EHIButton *scanButton;
@property (weak  , nonatomic) IBOutlet EHIPaymentInputView *paymentInputView;
@property (weak  , nonatomic) IBOutlet EHIActionButton *addButton;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *loadingIndicator;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *scrollBottomConstraint;
@end

@implementation EHIPaymentViewController

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIPaymentViewModel new];
    }
    
    return self;
}

- (void)updateWithAttributes:(NAVAttributes *)attributes
{
    [super updateWithAttributes:attributes];
    
    self.viewModel.handler = attributes.handler;
    self.viewModel.style   = [attributes.userObject integerValue];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    // pass driver name to input view
    self.paymentInputView.viewModel = self.viewModel.paymentInputViewModel;
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [self.view endEditing:YES];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [self.view endEditing:YES];
}

- (UIColor *)backgroundColor
{
    return [UIColor whiteColor];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIPaymentViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title)         : dest(self, .title),
        source(model.scanTitle)     : dest(self, .scanButton.ehi_title),
        source(model.addTitle)      : dest(self, .addButton.ehi_title),
        source(model.isLoading)     : dest(self, .loadingIndicator.isAnimating),
        source(model.missingField)  : dest(self, .addButton.isFauxDisabled),
    });
}

# pragma mark - Actions

- (IBAction)didTapScanButton:(id)sender
{
    [self.viewModel scanCard];
}

- (IBAction)didTapAddButton:(id)sender
{
    [self.viewModel addCard];
}

# pragma mark - Keyboard

- (BOOL)requiresKeyboardSupport
{
    return YES;
}

- (UIScrollView *)keyboardSupportedScrollView
{
    return self.scrollView;
}

# pragma mark - Security

- (BOOL)hasSecureContent
{
    return YES;
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    NSString *state = nil;
    switch (self.viewModel.style) {
        case EHIPaymentViewStyleProfile:
        case EHIPaymentViewStyleReservation:
            state = EHIScreenPayment;
            break;
        case EHIPaymentViewStyleSelectPayment:
            state = EHIScreenPaymentMethod;
            break;
    }
    
    [EHIAnalytics changeScreen:EHIScreenPayment state:state];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenPayment;
}

@end
