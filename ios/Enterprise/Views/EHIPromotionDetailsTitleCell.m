//
//  EHIPromotionDetailsTitleCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 3/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPromotionDetailsTitleCell.h"
#import "EHIPromotionDetailsTitleCellViewModel.h"

@interface EHIPromotionDetailsTitleCell ()
@property (strong, nonatomic) EHIPromotionDetailsTitleCellViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *promotionTitle;
@end

@implementation EHIPromotionDetailsTitleCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIPromotionDetailsTitleCellViewModel new];
    }

    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIPromotionDetailsTitleCellViewModel *)model
{
    [super registerReactions:model];

    model.bind.map(@{
        source(model.promotionTitle) : dest(self, .promotionTitle.text)
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetHeight(self.promotionTitle.frame) + EHIMediumPadding
    };
}

@end
