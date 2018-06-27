//
//  EHIAboutEnterprisePlusFooterCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 12/23/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIAboutEnterprisePlusFooterCell.h"
#import "EHIAboutEnterprisePlusFooterViewModel.h"
#import "EHIButton.h"

@interface EHIAboutEnterprisePlusFooterCell ()
@property (strong, nonatomic) EHIAboutEnterprisePlusFooterViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIButton *detailsButton;
@end

@implementation EHIAboutEnterprisePlusFooterCell

# pragma mark - Reactions

- (void)registerReactions:(EHIAboutEnterprisePlusFooterViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title) : dest(self, .detailsButton.ehi_title)
    });
}

# pragma mark - Actions

- (IBAction)didTapButton:(UIButton *)sender
{
    [self.viewModel showDetails];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.detailsButton.frame) + EHILightPadding
    };
}

@end
