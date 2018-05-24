//
//  EHIInvoiceViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 7/20/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIInvoiceViewModel.h"
#import "EHIUserRental.h"
#import "EHIServices+Rentals.h"
#import "EHIRentalsCondensedReceiptView.h"

@interface EHIInvoiceViewModel ()
@property (strong, nonatomic) EHIUserRental *rental;
@end

@implementation EHIInvoiceViewModel

- (instancetype)initFetchingRental:(EHIUserRental *)rental
{
    if(self = [super init]) {
        _title  = EHILocalizedString(@"invoice_title", @"Receipt", @"");
        [self fetchRental:rental];
    }
    
    return self;
}

- (void)fetchRental:(EHIUserRental *)rental
{
    self.isLoading = YES;
    
    [[EHIServices sharedInstance] fetchInvoiceDetails:rental handler:^(EHIUserRental *newRental, EHIServicesError *error) {
        self.isLoading = NO;
        if(!error.hasFailed) {
            [self updateWithUserRental:newRental];
            [self.footerModel updateWithInvoiceNumber:rental.invoiceNumber ?: @""];
        }
    }];
}

- (void)updateWithModel:(id)model
{
    if([model isKindOfClass:[EHIUserRental class]]) {
        [self updateWithUserRental:(EHIUserRental *)model];
    }
}

- (void)updateWithUserRental:(EHIUserRental *)rental
{
    self.rental         = rental;
    self.rentalInfo     = [[EHIInvoiceRentalInfoViewModel alloc] initWithModel:rental];
    self.tripSummary    = [[EHIInvoiceTripSummaryViewModel alloc] initWithModel:rental];
    self.priceDetails   = [[EHIInvoiceSublistViewModel alloc] initWithModel:rental type:EHIInvoiceSublistTypePriceDetails];
    
    self.estimatedTotal = [[EHIReservationRentalPriceTotalViewModel alloc] initWithModel:rental.priceSummary
                                                                          prepaySelected:NO
                                                                              paidAmount:nil
                                                                            actualAmount:nil
                                                                         showOtherOption:NO
                                                                                  layout:EHIReservationRentalPriceTotalLayoutInvoice
                                                                            isSecretRate:NO];
    
    self.sublistModel   = [[EHIInvoiceSublistViewModel alloc] initWithModel:rental type:EHIInvoiceSublistTypeAdditionalInfo];
    self.footerModel    = [[EHIInvoiceFooterViewModel alloc] initWithModel:rental];
    
    NSString *pickupCountry  = rental.pickupLocation.countryCode;
    NSString *currentCountry = [NSLocale ehi_country].code;
    BOOL showBanner = ![pickupCountry ehi_isEqualToStringIgnoringCase:currentCountry];
    if(showBanner) {
        EHICarClassVehicleRate *rates = rental.carClassDetails.vehicleRates.firstObject;
        self.currencyModel = [[EHICurrencyDiffersViewModel alloc] initWithModel:rates.priceSummary];
    }
}

- (EHIInvoiceSectionHeaderViewModel *)headerTitleForSection:(EHIInvoiceSection)section
{
    EHIInvoiceSectionHeaderViewModel *model = [EHIInvoiceSectionHeaderViewModel new];
    NSString *title       = nil;
    NSString *actionTitle = nil;
    switch (section) {
        case EHIInvoiceSectionTripSummary: {
            title       = EHILocalizedString(@"trip_summary_title", @"TRIP SUMMARY", @"");
            actionTitle = EHILocalizedString(@"trip_summary_save_photo_button", @"SAVE TO PHOTOS", @"");
            break;
        }
        case EHIInvoiceSectionPriceDetails: {
            title = EHILocalizedString(@"price_section_title_rental_cost", @"RENTAL COST", @"");
            break;
        }
        case EHIInvoiceSectionAdditionalInformation: {
            title = EHILocalizedString(@"additional_info_section_title", @"ADDITIONAL INFORMATION", @"");
            break;
        }
        default: return nil;
    }
    
    model.actionTitle = actionTitle;
    model.title       = title;
    
    return model;
}

- (EHIModel *)dividerModelForSection:(EHIInvoiceSection)section
{
    return section != EHIInvoiceSectionFooter ? [EHIModel placeholder] : nil;
}

- (void)saveRentalAsPhoto
{
    [EHIAnalytics trackAction:EHIAnalyticsReceiptActionSaveToPhotos type:EHIAnalyticsActionTypeTap handler:nil];
    
    [EHIRentalsCondensedReceiptView captureReceiptWithRental:self.rental];
}

- (void)dismiss
{
    self.router.transition.dismiss.start(nil);
}

@end
