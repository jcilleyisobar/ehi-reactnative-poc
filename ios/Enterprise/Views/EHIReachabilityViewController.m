//
//  EHIReachabilityViewController.m
//  Enterprise
//
//  Created by Ty Cobb on 6/15/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReachabilityViewController.h"
#import "EHIReachabilityViewModel.h"
#import "EHIActivityButton.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIReachabilityViewController ()
@property (strong, nonatomic) EHIReachabilityViewModel *viewModel;
@property (weak  , nonatomic, null_unspecified) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic, null_unspecified) IBOutlet UILabel *detailsLabel;
@property (weak  , nonatomic, null_unspecified) IBOutlet UIImageView *launchImageView;
@property (weak  , nonatomic, null_unspecified) IBOutlet EHIActivityButton *retryButton;
@property (weak  , nonatomic, null_unspecified) IBOutlet EHIActivityIndicator *delayIndicator;
@end

@implementation EHIReachabilityViewController

- (nullable instancetype)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHIReachabilityViewModel new];
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];

    self.retryButton.isDisabledWhileLoading = YES;
    
    // reuse the launch image so the transition on first load is seamless
    self.launchImageView.image = [UIImage launchImage];
}

 - (UIColor *)backgroundColor
{
    return [[UIColor ehi_grayColor6] colorWithAlphaComponent:0.7];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIReachabilityViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(animateSplashScreen:)];
    
    model.bind.map(@{
        source(model.title)      : dest(self, .titleLabel.text),
        source(model.details)    : dest(self, .detailsLabel.text),
        source(model.retryTitle) : dest(self, .retryButton.ehi_title),
        source(model.isLoading)  : dest(self, .retryButton.isLoading),
        source(model.isDelayed)  : dest(self, .delayIndicator.isAnimating),
    });
}

//
// Helpers
//

- (void)animateSplashScreen:(MTRComputation *)computation
{
    BOOL showsSplash = self.viewModel.showsSplash;
    // animate the launch image in / out when status is unknown
    UIView.animate(!showsSplash && !computation.isFirstRun).duration(0.25).transform(^{
        self.launchImageView.alpha = showsSplash ? 1.0f : 0.0f;
    }).start(nil);
}

# pragma mark - Interface Actions

- (IBAction)didTapRetryButton:(UIButton *)button
{
    [self.viewModel retryConnection];
}

# pragma mark - NAVViewController

+ (NSString *)storyboardName
{
    return @"EHIMainStoryboard";
}

@end

NS_ASSUME_NONNULL_END
