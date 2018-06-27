//
//  EHIReservationPriceItemCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 7/14/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReservationPriceItemCell.h"
#import "EHIReservationPriceItemViewModel.h"
#import "EHIButton.h"

@interface EHIReservationPriceItemCell ()
@property (strong, nonatomic) EHIReservationPriceItemViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *accessoryLabel;
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@property (weak  , nonatomic) IBOutlet UILabel *learnMoreLabel;
@property (weak  , nonatomic) IBOutlet UIView *learnMoreContainer;
@end

@implementation EHIReservationPriceItemCell

# pragma mark - Reactions

- (void)registerReactions:(EHIReservationPriceItemViewModel *)model
{
    [MTRReactor autorun:self action:@selector(invalidateContainer:)];
    
    [super registerReactions:model];

    model.bind.map(@{
        source(model.title)          : ^(NSAttributedString *title) {
                                           self.titleLabel.attributedText = title;
                                           self.learnMoreLabel.text       = title.string;
                                      },
        source(model.accessoryTitle) : dest(self, .accessoryLabel.attributedText),
    });
}

- (void)invalidateContainer:(MTRComputation *)computation
{
    BOOL isLearnMore = self.viewModel.isLearnMore;
    
    MASLayoutPriority constraintPriority = isLearnMore ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    [self.learnMoreContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
    }];
    
    constraintPriority = !isLearnMore ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    [self.containerView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
    }];
}

- (void)setIsLastInSection:(BOOL)isLastInSection
{
    [super setIsLastInSection:isLastInSection];
    
    self.divider.hidden = !isLastInSection;
}

- (void)showDetailsIfNeeded
{
    [self.viewModel showDetail];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    UIView *targetView = self.viewModel.isLearnMore ? self.learnMoreContainer : self.containerView;
    CGFloat padding = self.viewModel.isLastInSection ? EHILightPadding : 0.0f;
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(targetView.frame) + padding
    };
}

@end
