//
//  EHIReservationCarClassUpgradeCell.m
//  Enterprise
//
//  Created by Alex Koller on 11/19/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIReservationCarClassUpgradeCell.h"
#import "EHIReservationCarClassUpgradeViewModel.h"
#import "EHINetworkImageView.h"
#import "EHIButton.h"
#import "EHIArrowBorderLayer.h"

@interface EHIReservationCarClassUpgradeCell ()
@property (strong, nonatomic) EHIReservationCarClassUpgradeViewModel *viewModel;
@property (strong, nonatomic) EHIArrowBorderLayer *borderLayer;
@property (strong, nonatomic) EHIArrowBorderLayer *maskLayer;
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@property (weak  , nonatomic) IBOutlet UIView *containerBackground;
@property (weak  , nonatomic) IBOutlet UILabel *detailsLabel;
@property (weak  , nonatomic) IBOutlet EHINetworkImageView *vehicleImage;
@property (weak  , nonatomic) IBOutlet EHIButton *upgradeButton;
@end

@implementation EHIReservationCarClassUpgradeCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIReservationCarClassUpgradeViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    // draw duplicate layers for masking and border
    self.borderLayer = [self insertArrowBorderLayer:YES];
    self.maskLayer   = [self insertArrowBorderLayer:NO];

    // mask content to top arrow border
    [self.containerView.layer setMask:self.maskLayer];
}

- (void)layoutSubviews
{
    [super layoutSubviews];
    
    self.maskLayer.frame = self.containerView.bounds;
    self.borderLayer.frame = self.containerView.bounds;
}

//
// Helpers
//

- (EHIArrowBorderLayer *)insertArrowBorderLayer:(BOOL)clearFill
{
    EHIArrowBorderLayer *borderLayer = [EHIArrowBorderLayer new];

    borderLayer.side = EHIArrowBorderLayerSideTop;
    borderLayer.strokeColor = [UIColor ehi_lightGreenColor].CGColor;
    borderLayer.fillColor   = clearFill ? [UIColor clearColor].CGColor : [UIColor whiteColor].CGColor;
    
    [self.containerView.layer addSublayer:borderLayer];
    
    return borderLayer;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIReservationCarClassUpgradeViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.detailsTitle) : dest(self , .detailsLabel.attributedText),
        source(model.vehicleImage) : dest(self , .vehicleImage.imageModel),
        source(model.buttonTitle)  : dest(self , .upgradeButton.ehi_title),
    });
}

# pragma mark - Actions

- (IBAction)didTapUpgradeButton:(id)sender
{
    [self ehi_performAction:@selector(didTapActionButtonForCarClassUpgradeCell:) withSender:self];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.containerView.frame) + EHILightPadding
    };
}

@end
