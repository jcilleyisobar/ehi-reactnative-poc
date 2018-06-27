//
//  EHIRewardsBenefitsHeaderCell.m
//  Enterprise
//
//  Created by frhoads on 12/22/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIRewardsBenefitsHeaderCell.h"
#import "EHIRewardsBenefitsHeaderViewModel.h"

const CGFloat EHIRewardsBenefitsHeaderThreshold = EHILightPadding;

@interface EHIRewardsBenefitsHeaderCell()
@property (strong, nonatomic) EHIRewardsBenefitsHeaderViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *headerTitleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *headerSubtitleLabel;
@property (weak  , nonatomic) IBOutlet UIImageView *imageView;
@end

@implementation EHIRewardsBenefitsHeaderCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIRewardsBenefitsHeaderViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.backgroundColor = [UIColor ehi_greenColor];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIRewardsBenefitsHeaderViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.headerTitle)     : dest(self, .headerTitleLabel.text),
        source(model.headerSubtitle)  : dest(self, .headerSubtitleLabel.text),
        source(model.headerImageName) : dest(self, .imageView.ehi_imageName),
    });
}

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.imageView.frame) + EHILightPadding
    };
}

@end
