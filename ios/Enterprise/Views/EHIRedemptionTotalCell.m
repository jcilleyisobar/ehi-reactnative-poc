//
//  EHIRedemptionTotalCell.m
//  Enterprise
//
//  Created by mplace on 8/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIRedemptionTotalCell.h"
#import "EHIRedemptionTotalViewModel.h"
#import "EHILabel.h"

@interface EHIRedemptionTotalCell ()
@property (strong, nonatomic) EHIRedemptionTotalViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *contentContainer;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *actionLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *valueLabel;
@property (weak  , nonatomic) IBOutlet UIImageView *arrowImageView;
@end

@implementation EHIRedemptionTotalCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIRedemptionTotalViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];

    // Temporarily removing this feature until we resolve some bugs with the price breakdown
//    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapCell:)];
//    [self.contentView addGestureRecognizer:tap];
    
    // apply the strikethrough to the value label
    self.valueLabel.appliesStrikethrough = YES;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIRedemptionTotalViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateSelectionState:)];
    
    model.bind.map(@{
        source(model.title)       : dest(self, .titleLabel.text),
        source(model.actionTitle) : dest(self, .actionLabel.text),
        source(model.value)       : dest(self, .valueLabel.attributedText),
    });
}

- (void)invalidateSelectionState:(MTRComputation *)computation
{
    CGFloat angle = self.viewModel.showsLineItems ? M_PI : 0;
    
    UIView.animate(!computation.isFirstRun).duration(0.33).transform(^{
        self.arrowImageView.layer.transform = CATransform3DMakeRotation(angle, 0.0, 0.0, 1.0);
    }).start(nil);
}

# pragma mark - Actions

- (void)didTapCell:(UIGestureRecognizer *)recognizer
{
    self.viewModel.showsLineItems = !self.viewModel.showsLineItems;
    [self ehi_performAction:@selector(didToggleSelectedStateForCell:) withSender:self];
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
