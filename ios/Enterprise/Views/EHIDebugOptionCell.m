//
//  EHIDebugOptionCell.m
//  Enterprise
//
//  Created by Alex Koller on 11/24/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIDebugOptionCell.h"
#import "EHIDebugOptionViewModel.h"

@interface EHIDebugOptionCell ()
@property (strong, nonatomic) EHIDebugOptionViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *subtitleLabel;
@end

@implementation EHIDebugOptionCell

# pragma mark - Reactions

- (void)registerReactions:(EHIDebugOptionViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title)         : dest(self, .titleLabel.text),
        source(model.subtitle)      : dest(self, .subtitleLabel.text),
    });
}

# pragma mark - Layout

+ (EHILayoutMetrics *)defaultMetrics
{
    EHILayoutMetrics *metrics = [super defaultMetrics];
    metrics.fixedSize = (CGSize){ .width = EHILayoutValueNil, .height = 76.0f };
    return metrics;
}

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.containerView.frame) + EHIMediumPadding
    };
}

@end
