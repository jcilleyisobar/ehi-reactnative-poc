//
//  EHIReservationExtraCell.m
//  Enterprise
//
//  Created by Alex Koller on 4/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationExtraCell.h"
#import "EHIReservationExtraViewModel.h"
#import "EHIReservationViewStyle.h"
#import "EHIRestorableConstraint.h"

@interface EHIReservationExtraCell ()
@property (strong, nonatomic) EHIReservationExtraViewModel *viewModel;
@property (assign, nonatomic) BOOL isConfirmation;
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@property (weak  , nonatomic) IBOutlet UIView *sectionTitleContainerView;
@property (weak  , nonatomic) IBOutlet UILabel *sectionTitleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *iconWidth;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *topDivider;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *bottomDivider;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *sectionViewTop;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *bottomSpaceHeight;
@property (assign, nonatomic) BOOL isPlaceholder;
@end

@implementation EHIReservationExtraCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIReservationExtraViewModel new];
    }
    
    return self;
}

- (void)updateWithModel:(id)model metrics:(EHILayoutMetrics *)metrics
{
    [super updateWithModel:model metrics:metrics];
    
    self.isPlaceholder  = [model isPlaceholder];
    self.isConfirmation = metrics.tag == EHIReservationViewStyleConfirmation;
}

# pragma mark - Accessibility

- (void)registerAccessibilityIdentifiers
{
    [super registerAccessibilityIdentifiers];
    
    if(self.viewModel.hasExtras) {
        self.accessibilityIdentifier = EHIReviewAddedExtrasRowKey;
    } else {
        self.accessibilityIdentifier = EHIReviewAddedExtrasEmptyKey;
    }
}

# pragma mark - Reactions

- (void)registerReactions:(EHIReservationExtraViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateIconWidth:)];
    [MTRReactor autorun:self action:@selector(invalidateSectionTitle:)];

    model.bind.map(@{
        source(model.title) : dest(self, .titleLabel.attributedText),
        source(model.sectionTitle) : dest(self, .sectionTitleLabel.text),
    });
}

- (void)invalidateSectionTitle:(MTRComputation *)computation
{
    MASLayoutPriority constraintPriority = self.viewModel.sectionTitle.length > 0 ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    [self.sectionTitleContainerView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
    }];
    
    BOOL hasTitle = self.viewModel.sectionTitle.length > 0;
    BOOL inConfirmation = self.isConfirmation;
    BOOL isPlaceholder  = self.isPlaceholder;
    self.topDivider.isDisabled      = !hasTitle;
    self.bottomDivider.isDisabled   = inConfirmation;
    self.bottomSpaceHeight.constant = !inConfirmation || isPlaceholder ? self.bottomSpaceHeight.constant : 8.0f;
    self.sectionViewTop.isDisabled  = inConfirmation && !hasTitle;
    
    [self setNeedsUpdateConstraints];
}

- (void)invalidateIconWidth:(MTRComputation *)computation
{
    self.iconWidth.isDisabled = self.isConfirmation || !self.viewModel.isEditable;
}

# pragma mark - Setters

- (void)setIsConfirmation:(BOOL)isConfirmation
{
    _isConfirmation = isConfirmation;
    
    [self invalidateIconWidth:nil];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    // 12 will sum up with the remaning 8.0f spacing we have
    CGFloat padding = self.viewModel.lastInSection ? 12.0f : 0.0f;
    return (CGSize){
        .width = EHILayoutValueNil,
        .height = CGRectGetHeight(self.containerView.bounds) + padding
    };
}

@end
