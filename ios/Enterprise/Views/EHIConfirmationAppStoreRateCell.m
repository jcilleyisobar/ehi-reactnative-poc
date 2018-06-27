//
//  EHIConfirmationAppStoreRateCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 6/16/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIConfirmationAppStoreRateCell.h"
#import "EHIConfirmationAppStoreRateViewModel.h"
#import "EHIButton.h"

@interface EHIConfirmationAppStoreRateCell ()
@property (strong, nonatomic) EHIConfirmationAppStoreRateViewModel *viewModel;
@property (weak, nonatomic) IBOutlet UIView *containerView;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *subtitleLabel;
@property (weak, nonatomic) IBOutlet EHIButton *rateButton;
@property (weak, nonatomic) IBOutlet EHIButton *dismissButton;
@end

@implementation EHIConfirmationAppStoreRateCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIConfirmationAppStoreRateViewModel new];
    }

    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    [self.dismissButton setType:EHIButtonTypeSecondary];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIConfirmationAppStoreRateViewModel *)model
{
    [super registerReactions:model];

    model.bind.map(@{
        source(model.title)              : dest(self, .titleLabel.text),
        source(model.subtitle)           : dest(self, .subtitleLabel.text),
        source(model.rateButtonTile)     : dest(self, .rateButton.ehi_title),
        source(model.dismissButtontitle) : dest(self, .dismissButton.ehi_title),
    });
}

#pragma mark - Actions

- (IBAction)didTapRate:(EHIButton *)sender {
    [self ehi_performAction:@selector(appStoreRateCellDidTapRate) withSender:self];
}

- (IBAction)didTapDismiss:(EHIButton *)sender {
    [self ehi_performAction:@selector(appStoreRateCellDidTapDismiss) withSender:self];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.containerView.frame) + EHIMediumPadding
    };
}

@end
