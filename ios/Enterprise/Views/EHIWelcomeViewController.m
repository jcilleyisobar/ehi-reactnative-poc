//
//  EHIWelcomeViewController.m
//  Enterprise
//
//  Created by Pawel Bragoszewski on 09.03.2015.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIWelcomeViewController.h"
#import "EHIWelcomeViewModel.h"
#import "EHIListCollectionView.h"
#import "EHIOnboardingViewController.h"
#import "EHIActionButton.h"
#import "EHISettings.h"

@interface EHIWelcomeViewController () <EHIListCollectionViewDelegate>
@property (strong, nonatomic) EHIWelcomeViewModel *viewModel;
@property (strong, nonatomic) EHIOnboardingViewController *onboardingViewController;
@property (weak  , nonatomic) IBOutlet EHIButton *signinButton;
@property (weak  , nonatomic) IBOutlet EHIButton *joinButton;
@property (weak  , nonatomic) IBOutlet EHIButton *continueAsGuestButton;

@end

@implementation EHIWelcomeViewController

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHIWelcomeViewModel new];
    }
    
    return self;
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    if([segue.destinationViewController isKindOfClass:[EHIOnboardingViewController class]]) {
        self.onboardingViewController = segue.destinationViewController;
    }
}

- (void)viewDidLoad
{
    [super viewDidLoad];
        
    self.signinButton.ehi_title = self.viewModel.signinTitle;
    self.continueAsGuestButton.ehi_attributedTitle = self.viewModel.continueTitle;
    
    self.joinButton.type = EHIButtonTypeSecondary;
    self.joinButton.ehi_title = self.viewModel.joinTitle;
    self.joinButton.ehi_titleColor = [UIColor ehi_greenColor];
}

- (UIColor *)backgroundColor
{
    return [UIColor whiteColor];
}

- (BOOL)prefersStatusBarHidden
{
    return YES;
}

# pragma mark - User interaction

- (IBAction)didSelectSignin:(EHIActionButton *)button
{
    [self.viewModel selectSignIn];
    [self.delegate welcomeViewControllerDidSelectSignin:self];
}

- (IBAction)didSelectContinueButton:(UIButton *)button
{
    [self.viewModel selectSkip];
    [self.delegate welcomeViewControllerDidSelectContinue:self];
}

- (IBAction)didSelectJoinButton:(UIButton *)button
{
    [self.viewModel selectJoin];
    [self.delegate welcomeViewControllerDidSelectJoin:self];
}
# pragma mark - Analytics

- (BOOL)automaticallyInvalidatesAnalyticsContext
{
    return [EHISettings sharedInstance].isFirstRun;
}

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    context.screen = EHIAnalyticsWelcomeScreen;
    context.state  = EHIAnalyticsWelcomeState;
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

#pragma mark - Memory Management

- (void)dealloc
{
    [self.onboardingViewController unload];
    [self.onboardingViewController removeFromParentViewController];
    self.onboardingViewController = nil;
}

@end
