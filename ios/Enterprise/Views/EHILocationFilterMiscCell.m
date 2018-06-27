//
//  EHILocationFilterMiscCell.m
//  Enterprise
//
//  Created by mplace on 2/27/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationFilterMiscCell.h"
#import "EHILocationFilterMiscCellViewModel.h"

@interface EHILocationFilterMiscCell ()
@property (weak, nonatomic) IBOutlet UIView *container;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@end

@implementation EHILocationFilterMiscCell

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        // initialize view model
        self.viewModel = [EHILocationFilterMiscCellViewModel new];
    }
    
    return self;
}

- (void)registerReactions:(EHILocationFilterMiscCellViewModel *)viewModel
{
    viewModel.bind.map(@{
        source(viewModel.title) : dest(self, .titleLabel.text),
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    CGFloat padding = 15;
    CGFloat height = self.container.frame.size.height + (2 * padding);
    return CGSizeMake(EHILayoutValueNil, height);
}

+ (EHILayoutMetrics *)metrics
{
    EHILayoutMetrics *metrics = [super defaultMetrics];
    metrics.fixedSize = (CGSize){ .width = EHILayoutValueNil, .height = 40.0f };
    return metrics;
}

@end
