//
//  EHIReservationPriceCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 7/14/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReservationPriceCell.h"
#import "EHIReservationPriceViewModel.h"
#import "EHIListCollectionView.h"
#import "EHIReservationPriceItemCell.h"
#import "EHIRestorableConstraint.h"

@interface EHIReservationPriceCell () <EHIListCollectionViewDelegate>
@property (strong, nonatomic) EHIReservationPriceViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *totalLabel;
@property (weak  , nonatomic) IBOutlet UIView *middleDivider;
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@property (weak  , nonatomic) IBOutlet UIImageView *arrowImage;
@end

@implementation EHIReservationPriceCell

# pragma mark - Reactions

- (void)registerReactions:(EHIReservationPriceViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title) : dest(self, .titleLabel.text),
        source(model.total) : dest(self, .totalLabel.text),
    });
}

- (void)setIsExpanded:(BOOL)isExpanded
{
    _isExpanded = isExpanded;
    
    double rads = isExpanded ? M_PI : 0;
    CGAffineTransform transform = CGAffineTransformRotate(CGAffineTransformIdentity, rads);
    
    UIView.animate(YES).duration(0.4).transform(^{
        self.arrowImage.transform = transform;
    }).start(nil);
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    CGFloat height = CGRectGetHeight(self.containerView.frame);

    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = height + EHIHeavyPadding
    };
}

@end
