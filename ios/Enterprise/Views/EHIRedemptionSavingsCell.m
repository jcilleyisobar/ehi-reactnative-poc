//
//  EHIRedemptionSavingsCell.m
//  Enterprise
//
//  Created by mplace on 8/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIRedemptionSavingsCell.h"
#import "EHIRedemptionSavingsViewModel.h"

@interface EHIRedemptionSavingsCell ()
@property (strong, nonatomic) EHIRedemptionSavingsViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *contentContainer;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *subtitleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *valueLabel;
@end

@implementation EHIRedemptionSavingsCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIRedemptionSavingsViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIRedemptionSavingsViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title)    : dest(self, .titleLabel.text),
        source(model.subtitle) : dest(self, .subtitleLabel.text),
        source(model.value)    : dest(self, .valueLabel.text),
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    CGRect frame = [self.contentContainer convertRect:self.contentContainer.bounds toView:self];
    
    return (CGSize) {
        .width = EHILayoutValueNil,
        .height = CGRectGetMaxY(frame) + EHIMediumPadding
    };
}

@end
