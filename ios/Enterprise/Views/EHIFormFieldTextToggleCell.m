//
//  EHIFormFieldTextToggleCell.m
//  Enterprise
//
//  Created by Alex Koller on 5/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldTextToggleCell.h"
#import "EHIFormFieldTextToggleViewModel.h"
#import "EHIToggleButton.h"
#import "EHILabel.h"

@interface EHIFormFieldTextToggleCell ()
@property (strong, nonatomic) EHIFormFieldTextToggleViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *toggleContainer;
@property (weak  , nonatomic) IBOutlet UIView *confirmationContainer;
@property (weak  , nonatomic) IBOutlet EHIToggleButton *toggleButton;
@property (weak  , nonatomic) IBOutlet EHILabel *toggleLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *confirmationLabel;
@end

@implementation EHIFormFieldTextToggleCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIFormFieldTextToggleViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    // add control responsiveness to whole containers (both toggles are disenabled)
    UITapGestureRecognizer *tapToggle = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapToggleContainer:)];
    [self.toggleContainer addGestureRecognizer:tapToggle];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIFormFieldTextToggleViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateConfirmationConstraint:)];
    [MTRReactor autorun:self action:@selector(invalidateToggleTitle:)];
    
    model.bind.map(@{
        source(model.toggleEnabled)     : dest(self, .toggleButton.selected),
        source(model.confirmationTitle) : dest(self, .confirmationLabel.text),
    });
}

- (void)invalidateConfirmationConstraint:(MTRComputation *)computation
{
    MASLayoutPriority constraintPriority = self.viewModel.showsConfirmationTitle ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    
    [self.confirmationContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
    }];
    
    UIView.animate(!computation.isFirstRun).duration(0.4f).transform(^{
        [self layoutIfNeeded];
    }).start(nil);
}

- (void)invalidateToggleTitle:(MTRComputation *)computation
{
    NSString *title = self.viewModel.toggleTitle;
    NSAttributedString *attributedTitle = self.viewModel.toggleAttributesTitle;
    
    if(title) {
        self.toggleLabel.text = title;
    }
    
    if(attributedTitle) {
        self.toggleLabel.attributedText = attributedTitle;
    }
}

# pragma mark - Actions

- (void)didTapToggleContainer:(id)sender
{
    self.viewModel.toggleEnabled = !self.toggleButton.selected;
}

@end
