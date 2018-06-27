//
//  EHIReservationFeeCell.m
//  Enterprise
//
//  Created by Ty Cobb on 4/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationFeeCell.h"
#import "EHIReservationFeeViewModel.h"

@interface EHIReservationFeeCell ()
@property (strong, nonatomic) EHIReservationFeeViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *collapsedContainer;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *priceLabel;
@property (weak  , nonatomic) IBOutlet UILabel *detailsLabel;
@end

@implementation EHIReservationFeeCell

# pragma mark - Reactions

- (void)registerReactions:(EHIReservationFeeViewModel *)model
{
    [super registerReactions:model];

    [MTRReactor autorun:self action:@selector(invalidateSelection:)];
    [MTRReactor autorun:self action:@selector(invalidateTitle:)];
    
    model.bind.map(@{
        source(model.priceText)   : dest(self, .priceLabel.text),
        source(model.detailsText) : dest(self, .detailsLabel.attributedText),
    });
}

- (void)invalidateTitle:(MTRComputation *)computation
{
    BOOL shouldHighlight = self.viewModel.isLearnMore;
    
    EHIAttributedStringBuilder *title = self.titleLabel.attributedText.rebuild
        .text(self.viewModel.title)
        .attributes(@{
            NSForegroundColorAttributeName : shouldHighlight ? [UIColor ehi_greenColor]  : [UIColor ehi_blackColor],
            NSUnderlineStyleAttributeName  : shouldHighlight ? @(NSUnderlineStyleSingle) : @(NSUnderlineStyleNone),
        });
    
    self.titleLabel.attributedText = title.string;
}

- (void)invalidateSelection:(MTRComputation *)computation
{
    BOOL isSelected = self.viewModel.isSelected;
    UIView.animate(!computation.isFirstRun).duration(0.15).transform(^{
        self.detailsLabel.alpha = isSelected ? 1.0f : 0.0f;
    }).start(nil);
}

# pragma mark - Accessors

- (CGFloat)collapsedHeight
{
    return self.collapsedContainer.bounds.size.height;
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width = EHILayoutValueNil,
        .height = [self intrinsicContentHeight]
    };
}

- (CGFloat)intrinsicContentHeight
{
    // if we're not selected, return the collapsed height
    if(!self.viewModel.isSelected) {
        return self.collapsedContainer.bounds.size.height;
    }
    // otherwise, compute the full dynamic height
    else {
        CGRect detailsFrame = [self.detailsLabel convertRect:self.detailsLabel.bounds toView:self.contentView];
        return CGRectGetMaxY(detailsFrame) + EHILightPadding;
    }
}

@end
