//
//  EHIRewardsBenefitsFooterCell.m
//  Enterprise
//
//  Created by frhoads on 1/5/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIRewardsBenefitsFooterCell.h"
#import "EHIRewardsBenefitsFooterViewModel.h"
#import "EHIButton.h"

@interface EHIRewardsBenefitsFooterCell()
@property (strong, nonatomic) EHIRewardsBenefitsFooterViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIButton *programDetailsButton;
@end

@implementation EHIRewardsBenefitsFooterCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super initWithCoder:aDecoder]) {
        self.viewModel  = [EHIRewardsBenefitsFooterViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.programDetailsButton.imageHorizontalAlignment = UIControlContentHorizontalAlignmentRight | UIControlContentHorizontalAlignmentCenter;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIRewardsBenefitsFooterViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title) : dest(self, .programDetailsButton.ehi_title)
    });
}

# pragma mark - Actions

- (IBAction)didTapDetails:(EHIButton *)sender
{
    [self.viewModel showAboutEnterprisePlus];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.programDetailsButton.frame) + EHILightPadding
    };
}

@end
