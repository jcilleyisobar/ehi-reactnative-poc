//
//  EHITimePickerActiveTimeCell.m
//  Enterprise
//
//  Created by mplace on 3/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITimePickerActiveTimeCell.h"
#import "EHITimePickerTimeViewModel.h"
#import "EHILabel.h"

@interface EHITimePickerActiveTimeCell ()
@property (strong, nonatomic) EHITimePickerTimeViewModel *viewModel;
@property (weak, nonatomic) IBOutlet EHILabel *titleLabel;
@end

@implementation EHITimePickerActiveTimeCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHITimePickerTimeViewModel new];
    }
    
    return self;
}

#pragma mark - Reactions

- (void)registerReactions:(EHITimePickerTimeViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateSelectableStyle:)];
    
    model.bind.map(@{
        source(model.title) : dest(self, .titleLabel.text),
    });
}

- (void)invalidateSelectableStyle:(MTRComputation *)computation
{
    BOOL isSelectable = !self.viewModel.isClosed || self.viewModel.isAfterHours;
    
    self.titleLabel.textColor = [UIColor whiteColor];
    self.backgroundColor      = isSelectable ? [UIColor ehi_greenColor] : [UIColor ehi_grayColor4];
}

# pragma mark - Layout

+ (EHILayoutMetrics *)defaultMetrics
{
    EHILayoutMetrics *metrics = [super defaultMetrics];
    metrics.fixedSize = (CGSize){ .width = EHILayoutValueNil, .height = 50.0f };
    return metrics;
}

@end
