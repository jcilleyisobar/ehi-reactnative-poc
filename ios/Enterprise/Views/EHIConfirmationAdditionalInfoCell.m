//
//  EHIConfirmationAdditionalInfoCell.m
//  Enterprise
//
//  Created by Alex Koller on 7/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIConfirmationAdditionalInfoCell.h"
#import "EHIConfirmationAdditionalInfoViewModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIConfirmationAdditionalInfoCell ()
@property (strong, nonatomic) EHIConfirmationAdditionalInfoViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@property (weak  , nonatomic) IBOutlet UIView *titleContainer;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *nameLabel;
@property (weak  , nonatomic) IBOutlet UILabel *valueLabel;
@end

@implementation EHIConfirmationAdditionalInfoCell

- (nullable instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIConfirmationAdditionalInfoViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIConfirmationAdditionalInfoViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateTitle:)];
    
    model.bind.map(@{
        source(model.title) : dest(self, .titleLabel.text),
        source(model.name)  : dest(self, .nameLabel.text),
        source(model.value) : dest(self, .valueLabel.attributedText),
    });
}

- (void)invalidateTitle:(MTRComputation *)computation
{
    BOOL showTitle = self.viewModel.shouldShowSectionTitle;

    MASLayoutPriority priority = showTitle ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    
    [self.titleContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0f).priority(priority);
    }];
}

# pragma mark - Layout

- (void)setIsLastInSection:(BOOL)isLastInSection
{
    [super setIsLastInSection:isLastInSection];
    
    self.divider.hidden = !isLastInSection;
}

- (CGSize)intrinsicContentSize
{
    CGFloat padding = self.viewModel.isLastInSection ? EHIMediumPadding : 0.0f;
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.containerView.frame) + padding
    };
}

@end

NS_ASSUME_NONNULL_END