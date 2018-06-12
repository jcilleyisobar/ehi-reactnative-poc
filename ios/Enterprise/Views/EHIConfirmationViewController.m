//
//  EHIConfirmationViewController.m
//  Enterprise
//
//  Created by Alex Koller on 4/21/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIConfirmationViewController.h"
#import "EHIConfirmationViewModel.h"
#import "EHIReservationViewStyle.h"
#import "EHIInformationBannerCell.h"
#import "EHIConfirmationHeaderCell.h"
#import "EHIConfirmationAssistanceCell.h"
#import "EHIConfirmationLocationCell.h"
#import "EHIConfirmationCarClassCell.h"
#import "EHIConfirmationActionsCell.h"
#import "EHIConfirmationActionsViewModel.h"
#import "EHIConfirmationDiscountCell.h"
#import "EHIConfirmationAdditionalInfoCell.h"
#import "EHIReservationDriverInfoCell.h"
#import "EHIReservationExtraCell.h"
#import "EHIReservationScheduleCell.h"
#import "EHIReservationPoliciesCell.h"
#import "EHIReservationRentalPriceTotalCell.h"
#import "EHIFlightCell.h"
#import "EHIDeliveryCollectionCell.h"
#import "EHIConfirmationPaymentOptionCell.h"
#import "EHIListCollectionView.h"
#import "EHIBarButtonItem.h"
#import "EHIActivityIndicator.h"
#import "EHIConfirmationAppStoreRateCell.h"
#import "EHIReviewSectionHeader.h"
#import "EHIReservationPriceSublistCell.h"
#import "EHITermsAndConditionsCell.h"
#import "EHIConfirmationManageReservationCell.h"

@interface EHIConfirmationViewController () <EHIConfirmationAssistanceCellActions, EHIConfirmationAppStoreRateCellActions, EHIReservationPriceSublistCellActions, EHIConfirmationManageReservationCellActions, EHIConfirmationActions, UICollectionViewDelegate>
@property (strong, nonatomic) EHIConfirmationViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *activityIndicator;
@end

@implementation EHIConfirmationViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self.collectionView.sections construct:@{
        @(EHIConfirmationSectionDiscount)           : EHIConfirmationDiscountCell.class,
        @(EHIConfirmationSectionHeader)             : EHIConfirmationHeaderCell.class,
        @(EHIConfirmationSectionRentalHeader)       : EHIReviewSectionHeader.class,
        @(EHIConfirmationSectionBanner)             : EHIInformationBannerCell.class,
        @(EHIConfirmationSectionAssistance)         : EHIConfirmationAssistanceCell.class,
        @(EHIConfirmationManageReservation)         : EHIConfirmationManageReservationCell.class,
        @(EHIConfirmationSectionAppStoreRate)       : EHIConfirmationAppStoreRateCell.class,
        @(EHIConfirmationSectionSchedule)           : EHIReservationScheduleCell.class,
        @(EHIConfirmationSectionPickupReturn)       : EHIConfirmationLocationCell.class,
        @(EHIConfirmationSectionPickup)             : EHIConfirmationLocationCell.class,
        @(EHIConfirmationSectionReturn)             : EHIConfirmationLocationCell.class,
        @(EHIConfirmationSectionCarClass)           : EHIConfirmationCarClassCell.class,
        @(EHIConfirmationSectionMandatoryExtras)    : EHIReservationExtraCell.class,
        @(EHIConfirmationSectionIncludedExtras)     : EHIReservationExtraCell.class,
        @(EHIConfirmationSectionOptionalExtras)     : EHIReservationExtraCell.class,
        @(EHIConfirmationSectionDriverInfo)         : EHIReservationDriverInfoCell.class,
        @(EHIConfirmationSectionFlightDetails)      : EHIFlightCell.class,
        @(EHIConfirmationSectionAdditionalInfo)     : EHIConfirmationAdditionalInfoCell.class,
        @(EHIConfirmationSectionPriceDetails)       : EHIReservationPriceSublistCell.class,
        @(EHIConfirmationSectionPriceTotal)         : EHIReservationRentalPriceTotalCell.class,
        @(EHIConfirmationSectionPaymentOption)      : EHIConfirmationPaymentOptionCell.class,
        @(EHIConfirmationSectionDeliveryCollection) : EHIDeliveryCollectionCell.class,
        @(EHIConfirmationSectionActions)            : EHIConfirmationActionsCell.class,
        @(EHIConfirmationSectionPolicies)           : EHIReservationPoliciesCell.class,
        @(EHIConfirmationSectionTermsAndConditions) : EHITermsAndConditionsCell.class,
    }];
    
    // common section configuration
    for(EHIListDataSourceSection *section in self.collectionView.sections) {
        section.isDynamicallySized = YES;
        
        EHIViewModel *model = [self.viewModel headerForSection:section.index];
        // ignore the rental header, we already built it above
        if(section.index != EHIConfirmationSectionRentalHeader) {
            if(model) {
                [section.header setIsDynamicallySized:YES];
                
                section.header.klass = EHIReviewSectionHeader.class;
                section.header.model = model;
            }
        } else {
            section.model = model;
        }
        
        section.metrics = [[section.klass metrics] copy];
        section.metrics.tag = EHIReservationViewStyleConfirmation;
    }
}

# pragma mark - Reactions

- (void)registerReactions:(EHIConfirmationViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateAppStoreRateSection:)];
    
    EHIListDataSourceSection *discount          = self.collectionView.sections[EHIConfirmationSectionDiscount];
    EHIListDataSourceSection *header            = self.collectionView.sections[EHIConfirmationSectionHeader];
    EHIListDataSourceSection *banner            = self.collectionView.sections[EHIConfirmationSectionBanner];
    EHIListDataSourceSection *manageReservation = self.collectionView.sections[EHIConfirmationManageReservation];
    EHIListDataSourceSection *assistance        = self.collectionView.sections[EHIConfirmationSectionAssistance];
    EHIListDataSourceSection *schedule          = self.collectionView.sections[EHIConfirmationSectionSchedule];
    EHIListDataSourceSection *pickup            = self.collectionView.sections[EHIConfirmationSectionPickup];
    EHIListDataSourceSection *returnSection     = self.collectionView.sections[EHIConfirmationSectionReturn];
    EHIListDataSourceSection *pickupReturn      = self.collectionView.sections[EHIConfirmationSectionPickupReturn];
    EHIListDataSourceSection *carClass          = self.collectionView.sections[EHIConfirmationSectionCarClass];
    EHIListDataSourceSection *mandatoryExtras   = self.collectionView.sections[EHIConfirmationSectionMandatoryExtras];
    EHIListDataSourceSection *includedExtras    = self.collectionView.sections[EHIConfirmationSectionIncludedExtras];
    EHIListDataSourceSection *optionalExtras    = self.collectionView.sections[EHIConfirmationSectionOptionalExtras];
    EHIListDataSourceSection *driverInfo        = self.collectionView.sections[EHIConfirmationSectionDriverInfo];
    EHIListDataSourceSection *flightDetails     = self.collectionView.sections[EHIConfirmationSectionFlightDetails];
    EHIListDataSourceSection *additionalInfo    = self.collectionView.sections[EHIConfirmationSectionAdditionalInfo];
    EHIListDataSourceSection *rentalDetails     = self.collectionView.sections[EHIConfirmationSectionPriceDetails];
    EHIListDataSourceSection *total             = self.collectionView.sections[EHIConfirmationSectionPriceTotal];
    EHIListDataSourceSection *deliveryCollection = self.collectionView.sections[EHIConfirmationSectionDeliveryCollection];
    EHIListDataSourceSection *policies          = self.collectionView.sections[EHIConfirmationSectionPolicies];
    EHIListDataSourceSection *actions           = self.collectionView.sections[EHIConfirmationSectionActions];
    EHIListDataSourceSection *payment           = self.collectionView.sections[EHIConfirmationSectionPaymentOption];
    EHIListDataSourceSection *terms             = self.collectionView.sections[EHIConfirmationSectionTermsAndConditions];
    
    model.bind.map(@{
        source(model.discount)                     : dest(discount, .model),
        source(model.title)                        : dest(self, .title),
        source(model.totalPriceViewModel)          : dest(total, .model),
        source(model.manageReservationModel)       : dest(manageReservation, .model),
        source(model.carClass)                     : dest(carClass, .model),
        source(model.driverInfo)                   : dest(driverInfo, .model),
        source(model.airline)                      : dest(flightDetails, .model),
        source(model.additionalInfos)              : dest(additionalInfo, .models),
        source(model.scheduleViewModel)            : dest(schedule, .model),
        source(model.policiesViewModel)            : dest(policies, .model),
        source(model.mandatoryExtras)              : dest(mandatoryExtras, .models),
        source(model.includedExtras)               : dest(includedExtras, .models),
        source(model.optionalExtras)               : dest(optionalExtras, .models),
        source(model.priceSublistModel)            : dest(rentalDetails, .model),
        source(model.bannerModel)                  : dest(banner, .model),
        source(model.pickupSectionModel)           : dest(pickup, .model),
        source(model.deliveryCollectionViewModels) : dest(deliveryCollection, .models),
        source(model.returnSectionModel)           : dest(returnSection, .model),
        source(model.pickupReturnSectionModel)     : dest(pickupReturn, .model),
        source(model.assistanceModel)              : dest(assistance, .model),
        source(model.paymentMethod)                : dest(payment, .model),
        source(model.isLoading)                    : dest(self, .activityIndicator.isAnimating),
        source(model.reservation)                  : dest(header, .model),
        source(model.termsModel)                   : dest(terms, .model),
        source(model.defaultActionsModel)          : dest(actions, .model),
    });
}

- (void)invalidateAppStoreRateSection:(MTRComputation *)computation
{
    EHIViewModel *rateModel = self.viewModel.appStoreRateModel;
    
    [self.collectionView performAnimated:!computation.isFirstRun batchUpdates:^{
        self.collectionView.sections[EHIConfirmationSectionAppStoreRate].model = rateModel;
    } completion:nil];
}

# pragma mark - EHIConfirmationManageReservationCellActions

- (void)didExpandManageReservationCell:(EHIConfirmationManageReservationCell *)cell
{
    [self.collectionView ehi_invalidateLayoutAnimated:YES];
}

# pragma mark - EHIConfirmationAssistanceCellActions

- (void)didTapQuickPickupButtonForAssistanceCell:(EHIConfirmationAssistanceCell *)sender
{
    [self.viewModel showQuickPickup];
}

# pragma mark - EHIConfirmationAppStoreRateCellActions

- (void)appStoreRateCellDidTapRate
{
    [self.viewModel promptAppleStoreRate];
}

- (void)appStoreRateCellDidTapDismiss
{
    [self.viewModel presentedAppleStoreRate];
}

# pragma mark - EHIReservationPriceSublistCellActions

- (void)didSelectExpandedReservationSublistCell:(EHIReservationPriceSublistCell *)cell
{
    [self.collectionView ehi_invalidateLayoutAnimated:YES];
}

# pragma mark - EHIConfirmationActions

- (void)confirmationCellDidTapReturnToDashboard:(EHIConfirmationActionsCell *)cell
{
    [self.viewModel dismissConfirmation];
}

# pragma mark - Interface Actions

- (IBAction)didSelectCloseButton:(id)sender
{
    [self.viewModel dismissConfirmation];
}

# pragma mark - UICollectionViewDelegate

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    [self.viewModel selectItemAtIndexPath:indexPath];
}

# pragma mark - EHIViewController

- (void)updateNavigationItem:(UINavigationItem *)item
{
    [super updateNavigationItem:item];
    
    // replace left back button with right close buttom
    item.leftBarButtonItems  = nil;
    item.rightBarButtonItem = [EHIBarButtonItem buttonWithType:EHIButtonTypeClose target:self.viewModel action:@selector(dismissConfirmation)];
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenReservationReview state:EHIScreenConfirmation];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenConfirmation;
}

@end
