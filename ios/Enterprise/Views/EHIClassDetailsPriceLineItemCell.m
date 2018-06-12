//
//  EHIClassDetailsPriceLineItemCell.m
//  Enterprise
//
//  Created by mplace on 3/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIClassDetailsPriceLineItemCell.h"
#import "EHIClassDetailsPriceLineItemViewModel.h"
#import "EHIRestorableConstraint.h"
#import "EHILabel.h"

@interface EHIClassDetailsPriceLineItemCell ()
@property (strong, nonatomic) EHIClassDetailsPriceLineItemViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *accessoryLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *rateLabel;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *iconContainerWidth;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *rateTextTrailing;
@property (weak  , nonatomic) IBOutlet UIImageView *iconImageVIew;

@end

@implementation EHIClassDetailsPriceLineItemCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIClassDetailsPriceLineItemViewModel new];
    }
    
    return self;
}

- (void)updateConstraints
{
    [super updateConstraints];

    self.rateTextTrailing.isDisabled = !self.viewModel.rateString;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIClassDetailsPriceLineItemViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title)          : dest(self, .titleLabel.text),
        source(model.accessoryTitle) : dest(self, .accessoryLabel.text),
        source(model.rateString)     : dest(self, .rateLabel.text),
        source(model.hideIcon)       : dest(self, .iconContainerWidth.isDisabled),
        source(model.hasDetails)     : ^(NSNumber *hasDetails) {
            self.titleLabel.textColor = hasDetails.boolValue ? [UIColor ehi_greenColor] : [UIColor ehi_blackColor];
        },
    });
}

# pragma mark - Layout

+ (EHILayoutMetrics *)defaultMetrics
{
    EHILayoutMetrics *metrics = [super defaultMetrics];
    metrics.fixedSize = (CGSize) { .width = EHILayoutValueNil, .height = 50.0f };
    return metrics;
}

@end
