//
//  EHIAboutPointsRedeemCell.m
//  Enterprise
//
//  Created by frhoads on 1/12/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIAboutPointsRedeemCell.h"
#import "EHIAboutPointsRedeemViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIButton.h"

@interface EHIAboutPointsRedeemCell()
@property (strong, nonatomic) EHIAboutPointsRedeemViewModel *viewModel;

@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *subtitleLabel;
@property (weak, nonatomic) IBOutlet EHIButton *startReservationButton;
@end

@implementation EHIAboutPointsRedeemCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIAboutPointsRedeemViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIAboutPointsRedeemViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.titleText)    : dest(self, .titleLabel.text),
        source(model.subtitleText) : dest(self, .subtitleLabel.text),
        source(model.buttonText)   : dest(self, .startReservationButton.ehi_title)
    });
}

- (IBAction)didtapStartReservationButton:(id)sender
{
    [self.viewModel showStartReservation];
}

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.startReservationButton.frame) + EHIHeavyPadding
    };
}

@end
