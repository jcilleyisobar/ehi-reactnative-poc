//
//  EHIPromotionDetailsBulletItemCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 3/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPromotionDetailsBulletItemCell.h"
#import "EHIPromotionDetailsBulletItemCellViewModel.h"

@interface EHIPromotionDetailsBulletItemCell ()
@property (strong, nonatomic) EHIPromotionDetailsBulletItemCellViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *bulletTitle;
@end

@implementation EHIPromotionDetailsBulletItemCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIPromotionDetailsBulletItemCellViewModel new];
    }

    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIPromotionDetailsBulletItemCellViewModel *)model
{
    [super registerReactions:model];

    model.bind.map(@{
        source(model.bulletTitle) : dest(self, .bulletTitle.text)
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetHeight(self.bulletTitle.frame)
    };
}

@end
