//
//  EHIClassSelectEnterprisePlusCell.m
//  Enterprise
//
//  Created by Alex Koller on 4/10/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIClassSelectEnterprisePlusCell.h"
#import "EHIClassSelectEnterprisePlusViewModel.h"
#import "EHILabel.h"

@interface EHIClassSelectEnterprisePlusCell ()
@property (strong, nonatomic) EHIClassSelectEnterprisePlusViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHILabel *titleLabel;
@end

@implementation EHIClassSelectEnterprisePlusCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIClassSelectEnterprisePlusViewModel new];
    }
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    // for animating content view out
    self.clipsToBounds = NO;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIClassSelectEnterprisePlusViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title) : dest(self, .titleLabel.text)
    });
}

# pragma mark - Layout

+ (EHILayoutMetrics *)defaultMetrics
{
    EHILayoutMetrics *metrics = [super defaultMetrics];
    metrics.fixedSize = (CGSize) {
        .width = EHILayoutValueNil,
        .height = 100.0f
    };
    
    return metrics;
}

@end
