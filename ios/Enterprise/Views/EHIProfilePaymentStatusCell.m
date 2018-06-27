//
//  EHIProfilePaymentStatusCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 9/23/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIProfilePaymentStatusCell.h"
#import "EHIProfilePaymentStatusViewModel.h"
#import "EHIRestorableConstraint.h"

@interface EHIProfilePaymentStatusCell ()
@property (strong, nonatomic) EHIProfilePaymentStatusViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *topDivider;
@property (weak  , nonatomic) IBOutlet UIView *titleContainer;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *subtitleLabel;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *subtitleTopSpaceConstraint;
@end

@implementation EHIProfilePaymentStatusCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIProfilePaymentStatusViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)updateWithModel:(EHIProfilePaymentStatusViewModel *)model metrics:(EHILayoutMetrics *)metrics
{
    [super updateWithModel:model metrics:metrics];
    
    BOOL excceded = self.viewModel.type == EHIProfilePaymentStatusNumbersOfCardsExcceded;
    
    NSTextAlignment alignment = excceded ? NSTextAlignmentCenter : NSTextAlignmentLeft;
    self.titleLabel.textAlignment    = alignment;
    self.subtitleLabel.textAlignment = alignment;
    
    self.subtitleTopSpaceConstraint.constant = excceded ? 5.0f : self.subtitleTopSpaceConstraint.constant;
}

- (void)registerReactions:(EHIProfilePaymentStatusViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateTitle:)];
    
    model.bind.map(@{
        source(model.title)       : dest(self, .titleLabel.attributedText),
        source(model.subtitle)    : dest(self, .subtitleLabel.attributedText),
        source(model.hideDivider) : dest(self, .topDivider.hidden),
    });
}

- (void)invalidateTitle:(MTRComputation *)computation
{
    BOOL hasTitle = self.viewModel.title.length > 0;
    
    MASLayoutPriority priority = hasTitle ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    
    [self.titleContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0).priority(priority);
    }];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return(CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.subtitleLabel.frame) + EHIMediumPadding
    };
}

@end
