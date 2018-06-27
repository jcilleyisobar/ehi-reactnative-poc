//
//  EHIRewardsBenefitsPointsCell.m
//  Enterprise
//
//  Created by frhoads on 12/22/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIRewardsBenefitsPointsCell.h"
#import "EHIRewardsBenefitsPointsViewModel.h"

@interface EHIRewardsBenefitsPointsCell()
@property (weak, nonatomic) IBOutlet UILabel *pointsLabel;
@end

@implementation EHIRewardsBenefitsPointsCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIRewardsBenefitsPointsViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIRewardsBenefitsPointsViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.points) : dest(self, .pointsLabel.attributedText),
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.pointsLabel.frame) + 10
    };
}

@end
