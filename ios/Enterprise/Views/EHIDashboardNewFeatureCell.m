//
//  EHIDashboardNotificationsCell.m
//  Enterprise
//
//  Created by Alex Koller on 12/28/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIDashboardNewFeatureCell.h"
#import "EHIDashboardNewFeatureViewModel.h"
#import "EHIButton.h"

@interface EHIDashboardNewFeatureCell ()
@property (strong, nonatomic) EHIDashboardNewFeatureViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *centeringView;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *subtitleLabel;
@property (weak  , nonatomic) IBOutlet EHIButton *acceptButton;
@property (weak  , nonatomic) IBOutlet EHIButton *denyButton;
@property (weak  , nonatomic) IBOutlet UIButton *closeButton;
@end

@implementation EHIDashboardNewFeatureCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIDashboardNewFeatureViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIDashboardNewFeatureViewModel *)model
{
    [super registerReactions:model];

    model.bind.map(@{
        source(model.title)       : dest(self, .titleLabel.text),
        source(model.subtitle)    : dest(self, .subtitleLabel.text),
        source(model.acceptTitle) : dest(self, .acceptButton.ehi_title),
        source(model.denyTitle)   : dest(self, .denyButton.ehi_title),
    });
}

# pragma mark - Actions

- (IBAction)didTapAcceptButton:(id)sender
{
    [self ehi_performAction:@selector(didTapAcceptButtonForDashboardNewFeatureCell:) withSender:self];
}

- (IBAction)didTapDenyButton:(id)sender
{
    [self ehi_performAction:@selector(didTapDenyButtonForDashboardNewFeatureCell:) withSender:self];
}

- (IBAction)didTapCloseButton:(id)sender
{
    [self ehi_performAction:@selector(didTapCloseButtonForDashboardNewFeatureCell:) withSender:self];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.centeringView.frame) + 10.0,
    };
}

@end
