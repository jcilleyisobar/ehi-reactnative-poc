//
//  EHIInvoiceViewController.mViewController
//  Enterprise
//
//  Created by Rafael Ramos on 7/20/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIInvoiceViewController.h"
#import "EHIInvoiceViewModel.h"
#import "EHIListCollectionView.h"
#import "EHICurrencyDiffersCell.h"
#import "EHIInvoiceRentalInfoCell.h"
#import "EHIInvoiceTripSummaryCell.h"
#import "EHIReservationPriceSublistCell.h"
#import "EHIInvoiceSublistCell.h"
#import "EHIInvoiceFooterCell.h"
#import "EHIInvoiceSectionHeader.h"
#import "EHIReservationRentalPriceTotalCell.h"
#import "EHIActivityIndicator.h"
#import "EHISectionDivider.h"
#import "EHIButton.h"
#import "EHILabel.h"

@interface EHIInvoiceViewController () <EHIInvoiceSectionHeaderActions>
@property (strong, nonatomic) EHIInvoiceViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *loadingIndicator;
@property (weak  , nonatomic) IBOutlet EHIButton *closeButton;
@property (weak  , nonatomic) IBOutlet EHILabel *titleLabel;
@end

@implementation EHIInvoiceViewController

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIInvoiceViewModel new];
    }

    return self;
}

# pragma mark - View lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];

    self.closeButton.type = EHIButtonTypeClose;
    
    [self.collectionView.sections construct:@{
        @(EHIInvoiceSectionCurrencyDiffers)       : EHICurrencyDiffersCell.class,
        @(EHIInvoiceSectionRental)                : EHIInvoiceRentalInfoCell.class,
        @(EHIInvoiceSectionTripSummary)           : EHIInvoiceTripSummaryCell.class,
        @(EHIInvoiceSectionPriceDetails)          : EHIInvoiceSublistCell.class,
        @(EHIInvoiceSectionEstimatedTotal)        : EHIReservationRentalPriceTotalCell.class,
        @(EHIInvoiceSectionAdditionalInformation) : EHIInvoiceSublistCell.class,
        @(EHIInvoiceSectionFooter)                : EHIInvoiceFooterCell.class
    }];
    
    for(EHIListDataSourceSection *section in self.collectionView.sections) {
        
        section.isDynamicallySized = YES;
        
        section.header.klass = EHIInvoiceSectionHeader.class;
        section.header.model = [self.viewModel headerTitleForSection:section.index];
        
        BOOL shouldAddFooter = section.index != EHIInvoiceSectionPriceDetails;
        if(shouldAddFooter) {
            section.footer.klass = EHISectionDivider.class;
            section.footer.model = [self.viewModel dividerModelForSection:section.index];
        }
    }
}

# pragma mark - Reactions

- (void)registerReactions:(EHIInvoiceViewModel *)model
{
    [super registerReactions:model];

    EHIListDataSourceSection *currency    = self.collectionView.sections[EHIInvoiceSectionCurrencyDiffers];
    EHIListDataSourceSection *rental      = self.collectionView.sections[EHIInvoiceSectionRental];
    EHIListDataSourceSection *tripSummary = self.collectionView.sections[EHIInvoiceSectionTripSummary];
    EHIListDataSourceSection *price       = self.collectionView.sections[EHIInvoiceSectionPriceDetails];
    EHIListDataSourceSection *total       = self.collectionView.sections[EHIInvoiceSectionEstimatedTotal];
    EHIListDataSourceSection *sublist     = self.collectionView.sections[EHIInvoiceSectionAdditionalInformation];
    EHIListDataSourceSection *footer      = self.collectionView.sections[EHIInvoiceSectionFooter];
    
    model.bind.map(@{
        source(model.title)          : dest(self, .titleLabel.text),
        source(model.currencyModel)  : dest(currency, .model),
        source(model.rentalInfo)     : dest(rental, .model),
        source(model.tripSummary)    : dest(tripSummary, .model),
        source(model.priceDetails)   : dest(price, .model),
        source(model.estimatedTotal) : dest(total, .model),
        source(model.sublistModel)   : dest(sublist, .model),
        source(model.footerModel)    : dest(footer, .model),
        source(model.isLoading)      : dest(self, .loadingIndicator.isAnimating),
    });
}

# pragma mark - Actions

- (IBAction)didTapDismiss:(id)sender
{
    [self.viewModel dismiss];
}

# pragma mark - EHISectionHeaderActions

- (void)invoiceSectionHeaderDidTapActionButton:(EHIInvoiceSectionHeader *)sectionHeader;
{
    [self.viewModel saveRentalAsPhoto];
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenInvoiceDetails state:EHIScreenInvoiceDetails];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenInvoiceDetails;
}

@end
