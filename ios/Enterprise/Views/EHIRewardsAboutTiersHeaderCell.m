//
//  EHIRewardsAboutTiersHeaderCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/16/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIRewardsAboutTiersHeaderCell.h"
#import "EHIRewardsAboutTiersHeaderViewModel.h"

@interface EHIRewardsAboutTiersHeaderCell ()
@property (strong, nonatomic) EHIRewardsAboutTiersHeaderViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *subtitleLabel;
@end

@implementation EHIRewardsAboutTiersHeaderCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIRewardsAboutTiersHeaderViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIRewardsAboutTiersHeaderViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
	    source(model.title)    : dest(self, .titleLabel.text),
	    source(model.subtitle) : dest(self, .subtitleLabel.text)
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    UIView *view = self.viewModel.subtitle != nil ? self.subtitleLabel : self.titleLabel;
    return (CGSize) {
	    .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(view.frame) + EHILightPadding
    };
}

@end
