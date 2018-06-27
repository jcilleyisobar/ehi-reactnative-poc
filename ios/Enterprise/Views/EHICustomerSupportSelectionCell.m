//
//  EHICustomerSupportSelectionCell.m
//  Enterprise
//
//  Created by fhu on 6/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICustomerSupportSelectionCell.h"
#import "EHIRestorableConstraint.h"
#import "EHICustomerSupportSelectionViewModel.h"

@interface EHICustomerSupportSelectionCell()
@property (strong, nonatomic) EHICustomerSupportSelectionViewModel *viewModel;
@property (weak, nonatomic) IBOutlet UILabel *headerLabel;
@property (weak, nonatomic) IBOutlet UILabel *detailsLabel;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *iconWidthConstraint;
@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;
@end

@implementation EHICustomerSupportSelectionCell

# pragma mark - Reactions

- (void)registerReactions:(EHICustomerSupportSelectionViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(updateIconImage:)];
    
    model.bind.map(@{
        source(model.headerAttributedString) : dest(self, .headerLabel.attributedText),
        source(model.detailsText)            : dest(self, .detailsLabel.text)
    });
}

- (void)updateIconImage:(MTRComputation *)computation
{
    NSString *iconImageName = self.viewModel.iconImageName;
    
    self.iconImageView.ehi_imageName = iconImageName;
    self.iconWidthConstraint.isDisabled = !self.iconImageView.image;
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize){
        .width = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.detailsLabel.frame) + EHILightPadding
    };
}

@end
