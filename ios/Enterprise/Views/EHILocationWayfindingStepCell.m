//
//  EHITerminalDirectionsStepCell.m
//  Enterprise
//
//  Created by Pawel Bragoszewski on 02.03.2015.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationWayfindingStepCell.h"
#import "EHINetworkImageView.h"

@interface EHILocationWayfindingStepCell ()
@property (weak, nonatomic) IBOutlet UILabel *textLabel;
@property (weak, nonatomic) IBOutlet EHINetworkImageView *iconImageView;
@end

@implementation EHILocationWayfindingStepCell

- (void)prepareForReuse
{
    [super prepareForReuse];
    
    [self.iconImageView prepareForReuse];
}

# pragma mark - Updating

- (void)updateWithModel:(EHILocationWayfinding *)model metrics:(EHILayoutMetrics *)metrics
{
    [super updateWithModel:model metrics:metrics];
    
    self.textLabel.text     = model.text;
    self.iconImageView.path = model.iconUrl;
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    CGFloat height = CGRectGetMaxY([self.textLabel convertRect:self.textLabel.bounds toView:self.contentView]);
    
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = height + EHILightPadding
    };
}

@end
