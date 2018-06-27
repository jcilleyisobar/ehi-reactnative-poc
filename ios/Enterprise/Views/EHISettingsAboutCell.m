//
//  EHISettingsAboutCell.m
//  Enterprise
//
//  Created by Alex Koller on 6/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHISettingsAboutCell.h"
#import "EHISettingsAboutViewModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHISettingsAboutCell ()
@property (strong, nonatomic) EHISettingsAboutViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *detailLabel;
@property (weak  , nonatomic) IBOutlet UIImageView *arrow;
@end

@implementation EHISettingsAboutCell

# pragma mark - Reactions

- (void)registerReactions:(EHISettingsAboutViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title)       : dest(self, .titleLabel.text),
        source(model.detailTitle) : dest(self, .detailLabel.text),
        source(model.showsArrow)  : ^(NSNumber *showsArrow) {
            BOOL showArrow = [showsArrow boolValue];
        
            self.detailLabel.hidden = showArrow;
            self.arrow.hidden = !showArrow;
        },
    });
}

# pragma mark - Layout

+ (EHILayoutMetrics *)defaultMetrics
{
    EHILayoutMetrics *metrics = [super defaultMetrics];
    metrics.fixedSize = (CGSize){ .width = EHILayoutValueNil, .height = 54.0f };
    return metrics;
}

@end

NS_ASSUME_NONNULL_END