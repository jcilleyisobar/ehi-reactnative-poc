//
//  EHIFormFieldCheckboxCell.m
//  Enterprise
//
//  Created by Alex Koller on 6/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldToggleCell.h"
#import "EHIFormFieldToggleViewModel.h"
#import "EHIToggleButton.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIFormFieldToggleCell ()
@property (strong, nonatomic) EHIFormFieldToggleViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIToggleButton *toggleButton;
@end

@implementation EHIFormFieldToggleCell

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    // add control responsiveness to whole view (control disabled in IB)
    UITapGestureRecognizer *tapToggle = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapToggleContainer:)];
    [self addGestureRecognizer:tapToggle];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIFormFieldToggleViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.toggleValue)   : dest(self, .toggleButton.selected),
    });
}

# pragma mark - Actions

- (void)didTapToggleContainer:(id)sender
{
    self.viewModel.toggleValue = !self.toggleButton.selected;
}

@end

NS_ASSUME_NONNULL_END