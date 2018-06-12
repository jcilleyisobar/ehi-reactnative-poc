//
//  EHIAboutPointsHeaderCell.m
//  Enterprise
//
//  Created by frhoads on 1/12/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIAboutPointsHeaderCell.h"
#import "EHIAboutPointsHeaderViewModel.h"

@interface EHIAboutPointsHeaderCell()
@property (weak, nonatomic) IBOutlet UIImageView *imageView;
@property (weak, nonatomic) IBOutlet UILabel *pointsNumberLabel;
@property (weak, nonatomic) IBOutlet UILabel *pointsTextLabel;
@property (weak, nonatomic) IBOutlet UIView *containerView;
@end

@implementation EHIAboutPointsHeaderCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIAboutPointsHeaderViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIAboutPointsHeaderViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.points)     : dest(self, .pointsNumberLabel.text),
        source(model.pointsText) : dest(self, .pointsTextLabel.text),
    });
}

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.pointsTextLabel.frame) + EHIHeavyPadding
    };
}

@end
