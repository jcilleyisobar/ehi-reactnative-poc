//
//  EHIDashboardNotificationsCell.m
//  Enterprise
//
//  Created by Marcelo Rodrigues on 12/06/2018.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIDashboardNotificationsCell.h"
#import "EHIDashboardNotificationsPromptViewModel.h"
#import "EHIButton.h"

@interface EHIDashboardNotificationsCell ()
@property (strong, nonatomic) EHIDashboardNotificationsPromptViewModel *viewModel;

@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *bulletLabel;
@property (weak  , nonatomic) IBOutlet EHIButton *acceptButton;
@property (weak  , nonatomic) IBOutlet EHIButton *denyButton;
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@end

@implementation EHIDashboardNotificationsCell

# pragma mark - Subclassing

- (void)awakeFromNib
{
    [super  awakeFromNib];

    self.denyButton.type = EHIButtonTypeSecondary;
}

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIDashboardNotificationsPromptViewModel new];
    }

    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIDashboardNotificationsPromptViewModel *)model
{
    [super registerReactions:model];

    model.bind.map(@{
       source(model.title)       : dest(self, .titleLabel.text),
       source(model.bullet)      : dest(self, .bulletLabel.text),
       source(model.acceptTitle) : dest(self, .acceptButton.ehi_title),
       source(model.denyTitle)   : dest(self, .denyButton.ehi_title),
    });
}

# pragma mark - Actions

- (IBAction)didTapAcceptButton:(id)sender
{
    [self.viewModel acceptNotifications];
    [self didInteract];
}

- (IBAction)didTapDenyButton:(id)sender
{
    [self.viewModel denyNotifications];
    [self didInteract];
}

- (void)didInteract
{
    [self ehi_performAction:@selector(dashboardNotificationsCellDidInteract:) withSender:self];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.containerView.frame)
    };
}

@end
