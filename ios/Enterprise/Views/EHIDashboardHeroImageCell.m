//
//  EHIDashboardHeroImageCell.m
//  Enterprise
//
//  Created by Ty Cobb on 5/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDashboardHeroImageCell.h"
#import "EHIDashboardHeroImageViewModel.h"

@interface EHIDashboardHeroImageCell()
@property (strong, nonatomic) EHIDashboardHeroImageViewModel *viewModel;

@property (weak, nonatomic) IBOutlet UIImageView *heroImage;
@property (weak, nonatomic) IBOutlet UILabel *headerLabel;
@property (weak, nonatomic) IBOutlet UILabel *locationLabel;
@property (weak, nonatomic) IBOutlet UIView *containerView;
@end

@implementation EHIDashboardHeroImageCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIDashboardHeroImageViewModel new];
    }
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.containerView.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.5f];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIDashboardHeroImageViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.headerText)   : dest(self, .headerLabel.text),
        source(model.locationText) : dest(self, .locationLabel.text),
        source(model.imageName)    : dest(self, .heroImage.ehi_imageName)
    });
}

+ (EHILayoutMetrics *)defaultMetrics
{
    EHILayoutMetrics *metrics = [super defaultMetrics];
    metrics.fixedSize = (CGSize){ .width = EHILayoutValueNil, .height = 300.0f };
    return metrics;
}

@end
