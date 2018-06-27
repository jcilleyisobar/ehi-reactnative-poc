//
//  EHIFormFieldActionButtonCell.m
//  Enterprise
//
//  Created by Rafael Machado on 8/4/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIFormFieldActionButtonCell.h"
#import "EHIFormFieldActionButtonViewModel.h"
#import "EHIActionButton.h"

@interface EHIFormFieldActionButtonCell ()
@property (strong, nonatomic) EHIFormFieldActionButtonViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIActionButton *button;
@end

@implementation EHIFormFieldActionButtonCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIFormFieldActionButtonViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.button.titleLabel.numberOfLines = 0;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIFormFieldActionButtonViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title)          : dest(self, .button.ehi_title),
        source(model.isFauxDisabled) : dest(self, .button.isFauxDisabled)
    });
}

# pragma mark - Actions

- (IBAction)didTap:(id)sender
{
    [self.viewModel performButtonAction];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.button.frame) + EHIMediumPadding
    };
}

@end
