//
//  EHIDeliveryCollectionHeaderCell.m
//  Enterprise
//
//  Created by Alex Koller on 6/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDeliveryCollectionHeaderCell.h"
#import "EHIDeliveryCollectionHeaderViewModel.h"

@interface EHIDeliveryCollectionHeaderCell ()
@property (strong, nonatomic) EHIDeliveryCollectionHeaderViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *detailsLabel;
@property (weak  , nonatomic) IBOutlet UILabel *chargesLabel;
@end

@implementation EHIDeliveryCollectionHeaderCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIDeliveryCollectionHeaderViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIDeliveryCollectionHeaderViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title)        : dest(self, .titleLabel.text),
        source(model.detailsTitle) : dest(self, .detailsLabel.attributedText),
        source(model.chargesTitle) : dest(self, .chargesLabel.attributedText),
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize){
        .width = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.containerView.frame) + EHIMediumPadding
    };
}

@end
