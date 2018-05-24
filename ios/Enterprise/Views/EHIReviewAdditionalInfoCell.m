//
//  EHIReviewAdditionalInfoCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 8/2/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReviewAdditionalInfoCell.h"
#import "EHIReviewAdditionalInfoViewModel.h"
#import "EHIButton.h"
#import "EHIListCollectionView.h"
#import "EHIRestorableConstraint.h"
#import "EHIReviewAdditionalInfoAddCell.h"
#import "EHIReviewAdditionalInfoItemCell.h"

@interface EHIReviewAdditionalInfoCell ()
@property (strong, nonatomic) EHIReviewAdditionalInfoViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *titleContainer;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *arrowWidthConstraint;
@end

@implementation EHIReviewAdditionalInfoCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIReviewAdditionalInfoViewModel new];
    }
    
    return self;
}

- (void)updateWithModel:(EHIReviewAdditionalInfoViewModel *)model metrics:(EHILayoutMetrics *)metrics
{
    [self.collectionView prepareForReuse];
    
    [super updateWithModel:model metrics:metrics];
    
    EHIListDataSourceSection *add = self.collectionView.sections[EHIReviewAdditionalSectionAddInfo];
    add.klass = EHIReviewAdditionalInfoAddCell.class;
    add.model = model.addModel;
    add.isDynamicallySized = YES;
    
    EHIListDataSourceSection *items = self.collectionView.sections[EHIReviewAdditionalSectionItems];
    items.klass  = EHIReviewAdditionalInfoItemCell.class;
    items.models = model.itemModels;
    items.isDynamicallySized = YES;
    
    // if it's showing the add cell, hide the arrow image
    BOOL hideArrow = model.hideArrow;
    self.arrowWidthConstraint.isDisabled = hideArrow;
}

- (void)registerReactions:(EHIReviewAdditionalInfoViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title) : dest(self, .titleLabel.text),
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    CGFloat height = CGRectGetMaxY(self.titleContainer.frame) + self.collectionView.contentSize.height;
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = height + EHIMediumPadding
    };
}

@end
