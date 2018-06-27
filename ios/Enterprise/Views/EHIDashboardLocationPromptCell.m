//
//  EHIDashboardLocationPromptCell.m
//  Enterprise
//
//  Created by Marcelo Rodrigues on 21/06/2018.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIDashboardLocationPromptCell.h"
#import "EHIDashboardLocationPromptViewModel.h"
#import "EHIButton.h"
#import "EHIArrowBorderView.h"

@interface EHIDashboardLocationPromptCell ()
@property (strong, nonatomic) EHIDashboardLocationPromptViewModel *viewModel;

@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *bulletLabel;
@property (weak  , nonatomic) IBOutlet EHIButton *acceptButton;
@property (weak  , nonatomic) IBOutlet EHIButton *denyButton;
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@property (weak  , nonatomic) IBOutlet EHIArrowBorderView *arrowBorderView;
@end

@implementation EHIDashboardLocationPromptCell

# pragma mark - Subclassing

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIDashboardLocationPromptViewModel new];
    }

    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];

    self.arrowBorderView.side  = EHIArrowBorderLayerSideBottom;
    self.arrowBorderView.alpha = 0.25f;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIDashboardLocationPromptViewModel *)model
{
    [super registerReactions:model];

    model.bind.map(@{
        source(model.title)       : dest(self, .titleLabel.text),
        source(model.mainText)    : dest(self, .bulletLabel.text),
        source(model.acceptTitle) : dest(self, .acceptButton.ehi_title),
        source(model.denyTitle)   : dest(self, .denyButton.ehi_title),
    });
}

# pragma mark - Actions

- (IBAction)didTapAcceptButton:(id)sender
{
    [self.viewModel acceptLocation];
    [self didInteract];
}

- (IBAction)didTapDenyButton:(id)sender
{
    [self.viewModel denyLocation];
    [self didInteract];
}

- (void)didInteract
{
    [self ehi_performAction:@selector(dashboardLocationCellDidInteract:) withSender:self];
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
