//
//  EHINotificationSettingsOptionCell.m
//  Enterprise
//
//  Created by Alex Koller on 11/30/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHINotificationSettingsOptionCell.h"
#import "EHINotificationSettingsOptionViewModel.h"

@interface EHINotificationSettingsOptionCell ()
@property (strong, nonatomic) EHINotificationSettingsOptionViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UIImageView *checkmark;
@end

@implementation EHINotificationSettingsOptionCell

# pragma mark - Reactions

- (void)registerReactions:(EHINotificationSettingsOptionViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateSelected:)];
    
    model.bind.map(@{
        source(model.title) : dest(self, .titleLabel.text),
    });
}

- (void)invalidateSelected:(MTRComputation *)computation
{
    BOOL selected = self.viewModel.selected;
    
    UIView.animate(!computation.isFirstRun).duration(0.3).option(UIViewAnimationOptionBeginFromCurrentState).transform(^{
        self.checkmark.alpha = selected ? 1.0 : 0.0;
    }).start(nil);
}

# pragma mark - Layout

+ (EHILayoutMetrics *)defaultMetrics
{
    EHILayoutMetrics *metrics = [super defaultMetrics];
    metrics.fixedSize = (CGSize){ .width = EHILayoutValueNil, .height = 54.0 };
    return metrics;
}

@end