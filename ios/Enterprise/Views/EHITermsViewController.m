//
//  EHITermsViewController.m
//  Enterprise
//
//  Created by Ty Cobb on 6/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITermsViewController.h"
#import "EHITermsViewModel.h"
#import "EHIButton.h"
#import "EHILabel.h"
#import "EHIActivityIndicator.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHITermsViewController ()
@property (strong, nonatomic) EHITermsViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UIWebView *webView;
@property (weak  , nonatomic) IBOutlet EHIButton *acceptButton;
@property (weak  , nonatomic) IBOutlet EHIButton *closeButton;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *activityIndicator;

@end

@implementation EHITermsViewController

# pragma mark - View Lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.closeButton.type = EHIButtonTypeClose;
    self.titleLabel.disablesAutoShrink = YES;
}

- (BOOL)needsBottomLine
{
    return YES;
}

# pragma mark - Reactions

- (void)registerReactions:(EHITermsViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(animateIsLoading:)];
    
    model.bind.map(@{
         source(model.title) : dest(self, .titleLabel.text),
         source(model.acceptTitle) : dest(self, .acceptButton.ehi_title),
    });
}

- (void)animateIsLoading:(MTRComputation *)computation
{
    // load the content into the web view if we've got it
    if(self.viewModel.termsContent) {
        [self.webView loadHTMLString:self.viewModel.termsContent baseURL:nil];
    }
   
    // cross-fade the views based on the loading state
    BOOL isLoading = self.viewModel.isLoading;
    UIView.animate(!computation.isFirstRun).duration(0.25).transform(^{
        self.acceptButton.enabled = !isLoading;
        self.activityIndicator.isAnimating = isLoading;
        self.webView.alpha = isLoading ? 0.0f : 1.0f;
    }).start(nil);
}

# pragma mark - Actions

- (IBAction)didTapCloseButton:(EHIButton *)button
{
    [self.viewModel acceptTerms:NO];
}

- (IBAction)didTapAcceptButton:(EHIButton *)button
{
    [self.viewModel acceptTerms:YES];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenTerms;
}

@end

NS_ASSUME_NONNULL_END
