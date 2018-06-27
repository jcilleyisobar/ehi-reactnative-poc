//
//  EHIRewardsLearnMoreViewController.m
//  Enterprise
//
//  Created by Alex Koller on 6/15/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIRewardsLearnMoreViewController.h"
#import "EHIRewardsLearnMoreViewModel.h"
#import "EHIOnboardingViewController.h"
#import "EHIActionButton.h"
#import "EHIRestorableConstraint.h"

@interface EHIRewardsLearnMoreViewController ()
@property (strong, nonatomic) EHIRewardsLearnMoreViewModel *viewModel;
@property (strong, nonatomic) EHIOnboardingViewController *onboardingViewController;
@property (weak  , nonatomic) IBOutlet EHIActionButton *joinButton;
@property (weak  , nonatomic) IBOutlet EHIActionButton *signInButton;
@property (weak  , nonatomic) IBOutlet EHIButton *learnMoreButton;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet EHIButton *closeButton;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *navigationBarHeight;
@end

@implementation EHIRewardsLearnMoreViewController

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIRewardsLearnMoreViewModel new];
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
        
    self.closeButton.type = EHIButtonTypeClose;
    self.joinButton.type  = EHIButtonTypeSecondary;
    self.learnMoreButton.ehi_titleColor = [UIColor ehi_greenColor];
    self.learnMoreButton.hidden = YES;
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    [super prepareForSegue:segue sender:sender];
    
    if([segue.destinationViewController isKindOfClass:[EHIOnboardingViewController class]]) {
        self.onboardingViewController = (EHIOnboardingViewController *)segue.destinationViewController;
        self.onboardingViewController.viewModel = self.viewModel.onboardingViewModel;
    }
}

# pragma mark - Reactions

- (void)registerReactions:(EHIRewardsLearnMoreViewModel *)model
{
    [super registerReactions:model];

    [MTRReactor autorun:self action:@selector(invalidateLayout:)];
    [MTRReactor autorun:self action:@selector(invalidateButtons:)];
    
    model.bind.map(@{
        source(model.title)                 : ^(NSString *title) {
                                                self.title = title;
                                                self.titleLabel.text = title;
                                            },
        source(model.signInButtonTitle)     : dest(self, .signInButton.ehi_title),
        source(model.joinButtonTitle)       : dest(self, .joinButton.ehi_title),
        source(model.learnMoreButtonTitle)  : dest(self, .learnMoreButton.ehi_title),
        source(model.onboardingViewModel)   : dest(self, .onboardingViewController.viewModel)
    });
}

- (void)invalidateLayout:(MTRComputation *)computation
{
    BOOL hideNavigation = self.viewModel.layout == EHIRewardsLearnMoreLayoutMenu;
    
    self.navigationBarHeight.isDisabled = hideNavigation;
}

- (void)invalidateButtons:(MTRComputation *)computation
{
    self.signInButton.hidden = self.viewModel.authenticated;
    self.joinButton.hidden = self.viewModel.authenticated;
    self.learnMoreButton.hidden = self.viewModel.hideLearnMoreButton;
}

# pragma mark - Actions

- (IBAction)didTapJoinButton:(id)sender
{
    [self.viewModel joinEnterprisePlus];
}

- (IBAction)didTapSignInButton:(id)sender
{
    [self.viewModel signIn];
}

- (IBAction)didTapLearnMoreButton:(id)sender
{
    [self.viewModel learnMore];
}

- (IBAction)didTapCloseButton:(id)sender
{
    [self.viewModel close];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenRewardsLearnMore;
}

- (void)dealloc
{
    [self.onboardingViewController unload];
    [self.onboardingViewController removeFromParentViewController];
    self.onboardingViewController = nil;
}

@end
