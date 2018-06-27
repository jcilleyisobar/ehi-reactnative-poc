//
//  EHIInfoModalViewController.m
//  Enterprise
//
//  Created by Ty Cobb on 5/11/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIInfoModalViewController.h"
#import "EHILabel.h"
#import "EHIButton.h"
#import "UIView+Unarchiving.h"
#import "EHIRestorableConstraint.h"
#import "EHIView.h"

@interface EHIInfoModalViewController ()
@property (strong, nonatomic) EHIInfoModalViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *detailsLabel;
@property (weak  , nonatomic) IBOutlet EHIButton *actionButton;
@property (weak  , nonatomic) IBOutlet EHIButton *dismissButton;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *closeButtonWidth;
@property (weak  , nonatomic) IBOutlet UIScrollView *scrollView;
@property (weak  , nonatomic) IBOutlet UIView *headerContainer;
@property (weak  , nonatomic) IBOutlet UIView *detailsContainer;
@end

@implementation EHIInfoModalViewController

# pragma mark - View Lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    // trash the action button if we don't need it
    if(self.viewModel.hidesActionButton) {
        [self.actionButton removeFromSuperview];
    }
    
    // customize the title based on existence of alernate view nib
    if(self.viewModel.headerNibName) {
        self.headerView = [UIView ehi_instanceFromNibWithName:self.viewModel.headerNibName];
    }
    // customize the details based on existence of alternate view nib
    if(self.viewModel.detailsNibName) {
        self.detailsView = [UIView ehi_instanceFromNibWithName:self.viewModel.detailsNibName];
    }
    
    // shrink title if that's all we're showing
    if(!self.viewModel.details && !self.viewModel.headerNibName && !self.viewModel.detailsNibName) {
        self.titleLabel.font = [UIFont ehi_fontWithStyle:EHIFontStyleLight size:18.0f];
    }
    
    if(self.viewModel.buttonLayout == EHIInfoModalButtonLayoutSecondaryDismiss) {
        [self.dismissButton setType:EHIButtonTypeSecondary];
    }
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    [self.view setNeedsUpdateConstraints];
    [self.view layoutIfNeeded];
}

- (UIColor *)backgroundColor
{
    return [UIColor whiteColor];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIInfoModalViewModel *)model
{
    [super registerReactions:model];

    model.bind.map(@{
        source(model.title)              : dest(self, .titleLabel.text),
        source(model.details)            : dest(self, .detailsLabel.text),
        source(model.firstButtonTitle)   : dest(self, .actionButton.ehi_title),
        source(model.secondButtonTitle)  : dest(self, .dismissButton.ehi_title),
        source(model.hidesCloseButton)   : dest(self, .closeButtonWidth.isDisabled)
    });
}

# pragma mark - Interface Actions

- (IBAction)didTapButton:(UIButton *)sender
{
    [self.viewModel performActionForIndex:sender.tag];
}

- (IBAction)didTapCloseButton:(UIButton *)closeButton
{
    [self.viewModel cancel];
}

#pragma mark - Setters

- (void)setHeaderView:(UIView *)view
{
    [self.titleLabel removeFromSuperview];
    [self.headerContainer addSubview:view];
    
    [view mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.mas_equalTo(UIEdgeInsetsZero);
    }];
}

- (void)setDetailsView:(UIView *)view
{
    if ([view isKindOfClass:[EHIView class]]) {
        [(EHIView *)view updateWithModel:self.viewModel];
        [(EHIView *)view registerReactions:self.viewModel];
    }

    [self.detailsLabel removeFromSuperview];
    [self.scrollView addSubview:view];
    
    [view mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.mas_equalTo(self.scrollView);
        make.width.mas_equalTo(self.scrollView);
    }];
}

# pragma mark - EHIViewController

- (CGSize)preferredContentSize
{
    CGSize result = self.view.bounds.size;
    
    // replace the scroll view height with its content height
    result.height -= self.scrollView.bounds.size.height;
    // add scroll view's content height
    result.height += self.scrollView.contentSize.height;
    
    return result;
}

- (EHIModalTransitionStyle)customModalTransitionStyle
{
    return EHIModalTransitionStyleOverlay;
}

# pragma mark - Auto dismissal

- (BOOL)needsAutoDismiss
{
    return [self.viewModel needsAutoDismiss];
}

- (void)overlayTransitionDidTapOverlayContainer:(EHIModalOverlayTransitioningDelegate *)delegate
{
    [self.viewModel cancel];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenInfoModal;
}
@end

