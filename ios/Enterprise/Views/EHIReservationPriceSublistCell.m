//
//  EHIReservationPriceSublistCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 7/14/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReservationPriceSublistCell.h"
#import "EHIReservationPriceSublistViewModel.h"
#import "EHIListCollectionView.h"
#import "EHIReservationPriceMileageCell.h"
#import "EHIPlacardCell.h"
#import "EHIReservationPriceCell.h"
#import "EHIReservationPriceItemCell.h"

@interface EHIReservationPriceSublistCell () <EHIListCollectionViewDelegate>
@property (strong, nonatomic) EHIReservationPriceSublistViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@end

@implementation EHIReservationPriceSublistCell

- (void)updateWithModel:(EHIReservationPriceSublistViewModel *)model metrics:(EHILayoutMetrics *)metrics
{
    [super updateWithModel:model metrics:metrics];

    [self.collectionView.sections construct:@{
        @(EHIReservationPriceSublistMileage)            : EHIReservationPriceMileageCell.class,
        @(EHIReservationPriceSublistRate)               : EHIPlacardCell.class,
        
        @(EHIReservationPriceSublistRental)             : EHIReservationPriceCell.class,
        @(EHIReservationPriceSublistAdjustment)         : EHIReservationPriceCell.class,
        @(EHIReservationPriceSublistExtras)             : EHIReservationPriceCell.class,
        @(EHIReservationPriceSublistTaxesFees)          : EHIReservationPriceCell.class,
        
        @(EHIReservationPriceSublistRentalItems)        : EHIReservationPriceItemCell.class,
        @(EHIReservationPriceSublistAdjustmentItems)    : EHIReservationPriceItemCell.class,
        @(EHIReservationPriceSublistExtrasItems)        : EHIReservationPriceItemCell.class,
        @(EHIReservationPriceSublistTaxesFeesItems)     : EHIReservationPriceItemCell.class,
    }];
    
    EHIListDataSourceSection *mileage = self.collectionView.sections[EHIReservationPriceSublistMileage];
    EHIListDataSourceSection *rate    = self.collectionView.sections[EHIReservationPriceSublistRate];
    
    mileage.model = model.mileageModel;
    rate.model    = model.placard;
    
    // set category models
    [self buildSection:EHIReservationPriceSublistRental itemsSection:EHIReservationPriceSublistRentalItems];
    [self buildSection:EHIReservationPriceSublistAdjustment itemsSection:EHIReservationPriceSublistAdjustmentItems];
    [self buildSection:EHIReservationPriceSublistExtras itemsSection:EHIReservationPriceSublistExtrasItems];
    [self buildSection:EHIReservationPriceSublistTaxesFees itemsSection:EHIReservationPriceSublistTaxesFeesItems];

    for(EHIListDataSourceSection *section in self.collectionView.sections) {
        section.isDynamicallySized = YES;
    }
}

- (void)buildSection:(EHIReservationPriceSublistSection)section itemsSection:(EHIReservationPriceSublistSection)itemsSection
{
    BOOL hasItems = [self.viewModel itemsForSection:itemsSection].count > 0;
    EHIListDataSourceSection *priceSection = self.collectionView.sections[section];
    priceSection.model = hasItems ? [self.viewModel modelForPriceSection:section] : nil;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIReservationPriceSublistViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateRentalItems:)];
    [MTRReactor autorun:self action:@selector(invalidateAdjustmentItems:)];
    [MTRReactor autorun:self action:@selector(invalidateExtrasItems:)];
    [MTRReactor autorun:self action:@selector(invalidateTaxesItems:)];
}

- (void)invalidateRentalItems:(MTRComputation *)computation
{
    NSArray *rentalItems = self.viewModel.modelsForRentalItems;
    
    __weak typeof(self) welf = self;
    [self.collectionView performAnimated:!computation.isFirstRun batchUpdates:^{
        [welf buildSectionItems:EHIReservationPriceSublistRentalItems items:rentalItems];
    } completion:^(BOOL completed) {
        [welf ehi_performAction:@selector(didSelectExpandedReservationSublistCell:) withSender:welf];
    }];
}

- (void)invalidateAdjustmentItems:(MTRComputation *)computation
{
    NSArray *miscItems = self.viewModel.modelsForAdjustmentItems;

    __weak typeof(self) welf = self;
    [self.collectionView performAnimated:!computation.isFirstRun batchUpdates:^{
        [welf buildSectionItems:EHIReservationPriceSublistAdjustmentItems items:miscItems];
    } completion:^(BOOL completed) {
        [welf ehi_performAction:@selector(didSelectExpandedReservationSublistCell:) withSender:welf];
    }];
}

- (void)invalidateExtrasItems:(MTRComputation *)computation
{
    NSArray *extrasItems = self.viewModel.modelsForExtrasItems;
    
    __weak typeof(self) welf = self;
    [self.collectionView performAnimated:!computation.isFirstRun batchUpdates:^{
        [welf buildSectionItems:EHIReservationPriceSublistExtrasItems items:extrasItems];
    } completion:^(BOOL completed) {
        [welf ehi_performAction:@selector(didSelectExpandedReservationSublistCell:) withSender:welf];
    }];
}

- (void)invalidateTaxesItems:(MTRComputation *)computation
{
    NSArray *taxesItems = self.viewModel.modelsForTaxesFeesItems;

    __weak typeof(self) welf = self;
    [self.collectionView performAnimated:!computation.isFirstRun batchUpdates:^{
        [welf buildSectionItems:EHIReservationPriceSublistTaxesFeesItems items:taxesItems];
    } completion:^(BOOL completed) {
        [welf ehi_performAction:@selector(didSelectExpandedReservationSublistCell:) withSender:welf];
    }];
}

- (void)buildSectionItems:(EHIReservationPriceSublistSection)section items:(NSArray *)items
{
    self.collectionView.sections[section].models = items;
}

# pragma mark - UICollectionViewDelegate

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    [self.viewModel expandCollapseSection:indexPath.section];
    

    EHICollectionViewCell *cell = (EHICollectionViewCell *)[collectionView cellForItemAtIndexPath:indexPath];
    
    // update cell expanded state (not working through view model reactions)
    if([cell isKindOfClass:[EHIReservationPriceCell class]]) {
        EHIReservationPriceCell *priceCell = (EHIReservationPriceCell *)cell;
        priceCell.isExpanded = !priceCell.isExpanded;
        return;
    }
    
    if([cell isKindOfClass:[EHIReservationPriceItemCell class]]) {
        EHIReservationPriceItemCell *priceItemCell = (EHIReservationPriceItemCell *)cell;
        [priceItemCell showDetailsIfNeeded];
        return;
    }
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = self.collectionView.contentSize.height
    };
}

@end
