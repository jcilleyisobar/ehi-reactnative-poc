//
//  EHIAboutEnterprisePlusPointsCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 12/23/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIAboutEnterprisePlusPointsCell.h"
#import "EHIAboutEnterprisePlusPointsViewModel.h"

@interface EHIAboutEnterprisePlusPointsCell ()
@property (strong, nonatomic) EHIAboutEnterprisePlusPointsViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *headerLabel;
@property (weak  , nonatomic) IBOutlet UIView *headerView;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *detailLabel;
@property (weak  , nonatomic) IBOutlet UIImageView *iconImageView;
@end

@implementation EHIAboutEnterprisePlusPointsCell

# pragma mark - Reactions

- (void)registerReactions:(EHIAboutEnterprisePlusPointsViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateHeader:)];
    
    model.bind.map(@{
        source(model.header)        : dest(self, .headerLabel.text),
        source(model.title)         : dest(self, .titleLabel.text),
        source(model.detail)        : dest(self, .detailLabel.text),
        source(model.iconImageName) : dest(self, .iconImageView.ehi_imageName)
    });
}

- (void)invalidateHeader:(MTRComputation *)computation
{
    BOOL hideHeader = self.viewModel.header == nil;
    
    MASLayoutPriority priority = hideHeader ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    
    [self.headerView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0).priority(priority);
    }];
}

# pragma mark - Layout

-(CGSize)intrinsicContentSize
{
    CGFloat padding = self.viewModel.isLast ? EHIMediumPadding : 0.0f;
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.detailLabel.frame) + padding
    };
}

@end
