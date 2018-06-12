//
//  EHISigninMemberInfoRecoveryViewController.m
//  Enterprise
//
//  Created by Michael Place on 5/7/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHISigninRecoveryViewController.h"
#import "EHISigninRecoveryViewModel.h"
#import "EHIActionButton.h"
#import "EHILabel.h"

@interface EHISigninRecoveryViewController ()
@property (strong, nonatomic) EHISigninRecoveryViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *contentContainer;
@property (weak  , nonatomic) IBOutlet EHILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *detailsLabel;
@property (weak  , nonatomic) IBOutlet EHIActionButton *actionButton;
@property (weak  , nonatomic) IBOutlet EHIActionButton *cancelButton;

@end

@implementation EHISigninRecoveryViewController

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHISigninRecoveryViewModel new];
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    // style the cancel button
    [self.cancelButton setBackgroundColor:[UIColor ehi_grayColor4]];
    [self.cancelButton setBackgroundColor:[UIColor ehi_grayColor4] forState:UIControlStateNormal];

}

- (void)willBecomeReady
{
    [super willBecomeReady];
    
    [self.view setNeedsUpdateConstraints];
    [self.view layoutIfNeeded];
    
    [self.view mas_updateConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(self.preferredContentSize.height)).with.priorityMedium();
    }];
}

- (UIColor *)backgroundColor
{
    return [UIColor whiteColor];
}

# pragma mark - Reactions

- (void)registerReactions:(EHISigninRecoveryViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title)             : dest(self, .titleLabel.text),
        source(model.details)           : dest(self, .detailsLabel.attributedText),
        source(model.cancelButtonTitle) : dest(self, .cancelButton.ehi_title),
        source(model.actionButtonTitle) : dest(self, .actionButton.ehi_title),
    });
}

# pragma mark - Actions

- (IBAction)didTapContactUsButton:(id)sender
{
    [self.viewModel performAction];
}

- (IBAction)didTapCancelButton:(id)sender
{
    [self.viewModel dismiss];
}

# pragma mark - EHIViewController

- (EHIModalTransitionStyle)customModalTransitionStyle
{
    return EHIModalTransitionStyleOverlay;
}

- (CGSize)preferredContentSize
{
    CGRect frame = [self.contentContainer convertRect:self.contentContainer.bounds toView:self.view];
    
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetHeight(frame) + EHIMediumPadding
    };
}

# pragma mark - NAVViewController

+ (NSString *)storyboardName
{
    return @"EHISigninInfoRecoveryModalsStoryboard";
}

+ (NSString *)screenName
{
    return EHIScreenSigninRecovery;
}

@end
