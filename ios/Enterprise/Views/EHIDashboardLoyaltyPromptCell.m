//
//  EHIDashboardLoyaltyPromptCell.m
//  Enterprise
//
//  Created by mplace on 5/18/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDashboardLoyaltyPromptCell.h"
#import "EHIDashboardLoyaltyPromptViewModel.h"
#import "EHIButton.h"

@interface EHIDashboardLoyaltyPromptCell ()
@property (strong, nonatomic) EHIDashboardLoyaltyPromptViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *contentContainer;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *detailsLabel;
@property (weak  , nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak  , nonatomic) IBOutlet EHIButton *actionButton;
@end

@implementation EHIDashboardLoyaltyPromptCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIDashboardLoyaltyPromptViewModel new];
    }
    
    return self;
}

# pragma mark - Reaction

- (void)registerReactions:(EHIDashboardLoyaltyPromptViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title)   : dest(self, .titleLabel.text),
        source(model.details) : dest(self, .detailsLabel.text),
        source(model.iconImageName)     : dest(self, .iconImageView.ehi_imageName),
        source(model.actionButtonTitle) : dest(self, .actionButton.ehi_title)
    });
}

# pragma mark - Actions

- (IBAction)didTapActionButton:(id)sender
{
    [self.viewModel joinEnterprisePlus];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize){
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.contentContainer.frame) + EHIHeaviestPadding
    };
}

@end
