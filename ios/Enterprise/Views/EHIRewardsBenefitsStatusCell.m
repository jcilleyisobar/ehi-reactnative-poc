//
//  EHIRewardsBenefitsStatusCell.m
//  Enterprise
//
//  Created by frhoads on 12/22/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIRewardsBenefitsStatusCell.h"
#import "EHIRewardsBenefitsStatusViewModel.h"

@interface EHIRewardsBenefitsStatusCell()
@property (strong, nonatomic) EHIRewardsBenefitsStatusViewModel *viewModel;
@property (weak, nonatomic) IBOutlet UILabel *tierLabel;
@end

@implementation EHIRewardsBenefitsStatusCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIRewardsBenefitsStatusViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIRewardsBenefitsStatusViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.tierStatus) : dest(self, .tierLabel.attributedText),
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height =  CGRectGetMaxY(self.tierLabel.frame) + 10
    };
}

@end
