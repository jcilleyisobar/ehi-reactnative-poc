//
//  EHILocationNearbyCell.m
//  Enterprise
//
//  Created by mplace on 1/30/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationNearbyCell.h"
#import "EHILocationNearbyViewModel.h"

@interface EHILocationNearbyCell ()
@property (weak, nonatomic) IBOutlet UIImageView *locationIcon;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (strong, nonatomic) EHILocationNearbyViewModel *viewModel;
@end

@implementation EHILocationNearbyCell

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    // Initialize view model
    self.viewModel = [EHILocationNearbyViewModel new];
    
    // Bind view model to interface elements
    self.viewModel.bind.map(@{
        source(self.viewModel.title) : dest(self, .titleLabel.text),
    });
}

# pragma mark - Layout

+ (EHILayoutMetrics *)defaultMetrics
{
    EHILayoutMetrics *metrics = [super defaultMetrics];
    metrics.fixedSize = (CGSize){ .width = EHILayoutValueNil, .height = 60.0f };
    return metrics;
}

@end
