//
//  EHIClassDetailsViewController.h
//  Enterprise
//
//  Created by mplace on 3/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIClassDetailsViewController.h"
#import "EHIClassDetailsViewModel.h"
#import "EHIClassDetailsAttributesCell.h"
#import "EHIClassDetailsPriceLineItemCell.h"
#import "EHIReservationRentalPriceTotalCell.h"
#import "EHIRedemptionPointsCell.h"
#import "EHICarClassCell.h"
#import "EHITermsAndConditionsCell.h"
#import "EHIListCollectionView.h"
#import "EHIActivityIndicatorCell.h"
#import "EHIActionButton.h"

@interface EHIClassDetailsViewController () <EHICarClassCellActions, EHIRedemptionPointsCellActions, UICollectionViewDelegate>
@property (strong, nonatomic) EHIClassDetailsViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@property (weak  , nonatomic) IBOutlet EHIActionButton *classSelectionButton;
@end

@implementation EHIClassDetailsViewController

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIClassDetailsViewModel new];
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self initializeCollectionViewSections];
}

- (void)initializeCollectionViewSections
{
    EHIListDataSourceSection *redemption = self.collectionView.sections[EHIClassDetailsSectionRedemption];
    redemption.klass = EHIRedemptionPointsCell.class;
    redemption.isDynamicallySized = YES;
    
    EHIListDataSourceSection *car = self.collectionView.sections[EHIClassDetailsSectionCarClass];
    car.klass = EHICarClassCell.class;
    car.isDynamicallySized = YES;
    
    EHIListDataSourceSection *loading = self.collectionView.sections[EHIClassDetailsSectionLoading];
    loading.klass = EHIActivityIndicatorCell.class;

    EHIListDataSourceSection *general = self.collectionView.sections[EHIClassDetailsSectionGeneralInfo];
    general.klass = EHIClassDetailsAttributesCell.class;
    general.isDynamicallySized = YES;

    EHIListDataSourceSection *price = self.collectionView.sections[EHIClassDetailsSectionPriceSummary];
    price.klass = EHIClassDetailsPriceLineItemCell.class;
    price.header.klass = EHISectionHeader.class;

    EHIListDataSourceSection *total = self.collectionView.sections[EHIClassDetailsSectionPriceTotal];
    total.klass = EHIReservationRentalPriceTotalCell.class;
    total.isDynamicallySized = YES;
    
    EHIListDataSourceSection *terms = self.collectionView.sections[EHIClassDetailsSectionTermsAndConditions];
    terms.klass = EHITermsAndConditionsCell.class;
    terms.isDynamicallySized = YES;
    
}

# pragma mark - Reactions

- (void)registerReactions:(EHIClassDetailsViewModel *)model
{
    [super registerReactions:model];
    
    EHIListDataSourceSection *total = self.collectionView.sections[EHIClassDetailsSectionPriceTotal];
    EHIListDataSourceSection *general = self.collectionView.sections[EHIClassDetailsSectionGeneralInfo];
    EHIListDataSourceSection *price = self.collectionView.sections[EHIClassDetailsSectionPriceSummary];
    EHIListDataSourceSection *car = self.collectionView.sections[EHIClassDetailsSectionCarClass];
    EHIListDataSourceSection *redemption = self.collectionView.sections[EHIClassDetailsSectionRedemption];
    EHIListDataSourceSection *loading = self.collectionView.sections[EHIClassDetailsSectionLoading];
    EHIListDataSourceSection *terms = self.collectionView.sections[EHIClassDetailsSectionTermsAndConditions];

    [MTRReactor autorun:^(MTRComputation *computation) {
        // if we are loading, pass in a model
        [MTRReactor autorun:^(MTRComputation *computation) {
            loading.model = self.viewModel.isLoading ? [EHIModel placeholder] : nil;
        }];
    }];
    
    [MTRReactor autorun:^(MTRComputation *computation) {
        // check to see that class details call has come back
        if(!model.hasLoadedCarClassDetails) {
            return;
        }
        
        // animate the update
        [self.collectionView performAnimated:!computation.isFirstRun batchUpdates:^{
            general.model = model.carClass;
            price.models  = model.priceLineItems;
            total.model   = model.totalPriceViewModel;
            terms.model   = model.termsModel;
        } completion:nil];
    }];
    
    model.bind.map(@{
        source(model.title)                 : dest(self,  .title),
        source(model.actionButtonTitle)     : dest(self,  .classSelectionButton.ehi_title),
        source(model.priceHeader)           : dest(price, .header.model),
        source(model.carClassViewModel)     : dest(car,   .model),
        source(model.redemptionModel)       : dest(redemption, .model),
    });
}

# pragma mark - EHICarClassCellActions

- (void)didTapPriceButtonForCarClassCell:(EHICarClassCell *)cell
{
    [self.viewModel selectClass];
}

# pragma mark - EHIRedemptionPointsCellActions

- (void)didTapActionButtonForRedemptionPointsCell:(EHIRedemptionPointsCell *)sender
{
    [self.collectionView ehi_invalidateLayoutAnimated:YES];
}

# pragma mark - UICollectionViewDelegate

- (BOOL)collectionView:(UICollectionView *)collectionView shouldSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    return [self.viewModel shouldSelectLineItemAtIndexPath:indexPath];
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    [self.viewModel selectLineItemAtIndexPath:indexPath];
}

# pragma mark - Actions

- (IBAction)didTapClassSelectionButton:(UIButton *)button
{
    [self.viewModel selectClass];
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenReservationClassSelect state:EHIScreenReservationClassDetails];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenReservationClassDetails;
}

@end
