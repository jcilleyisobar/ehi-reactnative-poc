//
//  EHIRedemptionViewController.m
//  Enterprise
//
//  Created by mplace on 8/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIRedemptionViewController.h"
#import "EHIRedemptionViewModel.h"
#import "EHIListCollectionView.h"
#import "EHIReservationConfirmationFooter.h"
#import "EHIRedemptionPickerCell.h"
#import "EHIRedemptionTotalCell.h"
#import "EHIRedemptionSavingsCell.h"
#import "EHIClassDetailsPriceLineItemCell.h"
#import "EHIRedemptionPointsCell.h"
#import "EHIActivityIndicator.h"

@interface EHIRedemptionViewController () <UICollectionViewDelegate, EHIRedemptionPickerCellActions>
@property (strong, nonatomic) EHIRedemptionViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@property (weak  , nonatomic) IBOutlet EHIReservationConfirmationFooter *confirmationFooter;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *loadingIndicator;
@end

@implementation EHIRedemptionViewController

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIRedemptionViewModel new];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self configureCollectionViewSections];
}

- (void)configureCollectionViewSections
{
    [self.collectionView.sections construct:@{
        @(EHIRedemptionSectionHeader)               : EHIRedemptionPointsCell.class,
        @(EHIRedemptionSectionPointsPicker)         : EHIRedemptionPickerCell.class,
        @(EHIRedemptionSectionTotal)                : EHIRedemptionTotalCell.class,
        @(EHIRedemptionSectionLineItems)            : EHIClassDetailsPriceLineItemCell.class,
        @(EHIRedemptionSectionSavings)              : EHIRedemptionSavingsCell.class,
    }];
    
    
    // common section configuration
    for(EHIListDataSourceSection *section in self.collectionView.sections) {
        section.isDynamicallySized = [self isDynamicallySizedForSection:section.index];
    }
}

- (BOOL)isDynamicallySizedForSection:(EHIRedemptionSection)section
{
    switch (section) {
        case EHIRedemptionSectionHeader:
        case EHIRedemptionSectionPointsPicker:
        case EHIRedemptionSectionTotal:
        case EHIRedemptionSectionSavings:
            return YES;
        default: return NO;
    }
}

# pragma mark - Reactions

- (void)registerReactions:(EHIRedemptionViewModel *)model
{
    [super registerReactions:model];

    EHIListDataSourceSection *header  = self.collectionView.sections[EHIRedemptionSectionHeader];
    EHIListDataSourceSection *points  = self.collectionView.sections[EHIRedemptionSectionPointsPicker];
    EHIListDataSourceSection *total   = self.collectionView.sections[EHIRedemptionSectionTotal];
    EHIListDataSourceSection *savings = self.collectionView.sections[EHIRedemptionSectionSavings];

    [MTRReactor autorun:self action:@selector(invalidateLineItemsSection:)];
    [MTRReactor autorun:self action:@selector(invalidateFooterLoading:)];
    
    model.bind.map(@{
        source(model.title)              : dest(self, .title),
        source(model.headerModel)        : dest(header, .model),
        source(model.pointsModel)        : dest(points, .model),
        source(model.totalModel)         : dest(total, .model),
        source(model.savingsModel)       : dest(savings, .model),
        source(model.footerModel)        : dest(self, .confirmationFooter.price),
        source(model.footerTitle)        : dest(self, .confirmationFooter.title),
        source(model.footerSubtitleType) : dest(self, .confirmationFooter.priceSubtitleType),
        source(model.isCommitting)        : dest(self, .loadingIndicator.isAnimating)
    });
}

- (void)invalidateLineItemsSection:(MTRComputation *)computation
{
    EHIListDataSourceSection *lineItems = self.collectionView.sections[EHIRedemptionSectionLineItems];
    NSArray *models = self.viewModel.lineItemsModel;
    
    if(models == lineItems.models) {
        return;
    }
    
    [self.collectionView performAnimated:!computation.isFirstRun batchUpdates:^{
        // determine change using count and fixed height of line items
        CGFloat lineItemHeight = [EHIClassDetailsPriceLineItemCell defaultMetrics].fixedSize.height;
        BOOL shouldHide        = models == nil;
        NSUInteger count       = shouldHide ? lineItems.models.count : models.count;
        NSInteger modifier     = shouldHide ? -1 : 1;
        CGFloat deltaY         = count * lineItemHeight * modifier;
        CGPoint updatedOffsetY = CGPointOffset(self.collectionView.contentOffset, 0, deltaY);
        
        // don't go higher than top of scroll view
        if(shouldHide) {
            updatedOffsetY.y = MAX(0, updatedOffsetY.y);
        }
        // account for any existing empty space when adding to current offset
        else {
            CGFloat emptySpace = MAX(0, self.collectionView.bounds.size.height - self.collectionView.contentSize.height);
            updatedOffsetY.y  -= emptySpace;
        }
        
        [lineItems setModels:models];
        [self.collectionView setContentOffset:updatedOffsetY animated:!computation.isFirstRun];
        
    } completion:nil];
}

- (void)invalidateFooterLoading:(MTRComputation *)computation
{
    BOOL isLoading = self.viewModel.isLoading;
    BOOL isCommitting = self.viewModel.isCommitting;

    self.confirmationFooter.enabled   = !(isLoading || isCommitting);
    self.confirmationFooter.isLoading = isLoading;
}

# pragma mark - EHIRedemptionPickerCellActions

- (void)redemptionPickerDidUpdateDaysRedeemed:(EHIRedemptionPickerCell *)cell
{
    [self.viewModel updateReservationWithDaysRedeemed];
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    [self.viewModel selectLineItemAtIndexPath:indexPath];
}

# pragma mark - UICollectionViewDelegateFlowLayout

- (CGFloat)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout minimumLineSpacingForSectionAtIndex:(NSInteger)section
{
    return 0.f;
}

# pragma mark - Actions

- (void)didToggleSelectedStateForCell:(EHIRedemptionTotalCell *)cell
{
    [self.viewModel toggleLineItems];
}

- (IBAction)didTapConfirmationFooter:(id)sender
{
    [self.viewModel commitRedemption];
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenReservationRedemption state:EHIScreenReservationRedemption];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenReservationRedemption;
}

@end
