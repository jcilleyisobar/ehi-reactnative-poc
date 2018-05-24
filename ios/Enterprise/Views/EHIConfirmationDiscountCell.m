//
//  EHIConfirmationDiscountCell.m
//  Enterprise
//
//  Created by fhu on 6/8/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIConfirmationDiscountCell.h"
#import "EHIConfirmationDiscountViewModel.h"
#import "EHIContractDiscountView.h"

@interface EHIConfirmationDiscountCell()
@property (strong, nonatomic) EHIConfirmationDiscountViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIContractDiscountView  *discountView;
@end

@implementation EHIConfirmationDiscountCell

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHIConfirmationDiscountViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.contentView.backgroundColor = [UIColor ehi_tanColor];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIConfirmationDiscountViewModel *)model
{
    [super registerReactions:model];

    model.bind.map(@{
        source(model.discountModel) : dest(self, .discountView.viewModel)
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize){
        .width  = EHILayoutValueNil,
        .height = CGRectGetHeight(self.discountView.frame)
    };
}

@end
