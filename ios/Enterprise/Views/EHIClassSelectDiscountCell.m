//
//  EHIClassSelectDiscountCell.m
//  Enterprise
//
//  Created by Ty Cobb on 5/1/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIClassSelectDiscountCell.h"
#import "EHIClassSelectDiscountViewModel.h"
#import "EHIContractDiscountView.h"

@interface EHIClassSelectDiscountCell ()
@property (strong, nonatomic) EHIClassSelectDiscountViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIContractDiscountView *discountView;
@end

@implementation EHIClassSelectDiscountCell

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHIClassSelectDiscountViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    // for animating content view out
    self.clipsToBounds = NO;
    
    // set the content view's background color so that it animates properly
    self.contentView.backgroundColor = [UIColor ehi_tanColor];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIClassSelectDiscountViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.discountModel) : dest(self, .discountView.viewModel),
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
