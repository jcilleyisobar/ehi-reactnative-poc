//
//  EHIFilterToggleCell.m
//  Enterprise
//
//  Created by mplace on 2/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFilterToggleCell.h"
#import "EHILocationFilterCellViewModel.h"
#import "EHIRestorableConstraint.h"
#import "EHIToggleButton.h"
#import "EHILabel.h"

@interface EHIFilterToggleCell ()
@property (strong, nonatomic) EHILocationFilterCellViewModel *viewModel;
@property (weak, nonatomic) IBOutlet EHILabel *titleLabel;
@property (weak, nonatomic) IBOutlet EHIToggleButton *filterSelectionButton;
@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *iconContainerWidth;

@end

@implementation EHIFilterToggleCell

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        // initialize view model
        self.viewModel = [EHILocationFilterCellViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    [self.iconImageView setTintColor:[UIColor blackColor]];
}

# pragma mark - Reactions

- (void)registerReactions:(EHILocationFilterCellViewModel *)viewModel
{
    [super registerReactions:viewModel];
    
    self.viewModel.bind.map(@{
        source(viewModel.title)                     : dest(self, .titleLabel.text),
        source(viewModel.titleColor)                : dest(self, .titleLabel.textColor),
        source(viewModel.shouldHideSelectionButton) : dest(self, .filterSelectionButton.hidden),
        source(viewModel.isSelected)                : dest(self, .filterSelectionButton.selected),
        source(viewModel.iconImageName)             : dest(self, .iconImageView.ehi_imageName),
        source(viewModel.shouldHideIconImage)       : dest(self, .iconContainerWidth.isDisabled)
    });
}

# pragma mark - Actions

- (void)toggleFilterSelection
{
    // update button state
    self.filterSelectionButton.selected = !self.filterSelectionButton.selected;
    // update view model state
    self.viewModel.isSelected = self.filterSelectionButton.selected;
}

# pragma mark - Layout

+ (EHILayoutMetrics *)metrics
{
    EHILayoutMetrics *metrics = [super defaultMetrics];
    metrics.fixedSize = (CGSize){ .width = EHILayoutValueNil, .height = 50.0f };
    return metrics;
}

@end
