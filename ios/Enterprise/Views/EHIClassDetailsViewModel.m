//
//  EHIClassDetailsViewModel.m
//  Enterprise
//
//  Created by mplace on 3/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_CountrySpecific.h"
#import "EHIReservationStepViewModel_Subclass.h"
#import "EHIClassDetailsViewModel.h"
#import "EHICarClass.h"
#import "EHIExtrasViewModel.h"
#import "EHIInfoModalViewModel.h"
#import "EHIReservationSublistViewModel.h"
#import "EHIReservationBuilder+Analytics.h"
#import "EHIServices+Reservation.h"
#import "EHIUserManager.h"
#import "EHIPaymentOptionViewModel.h"

@interface EHIClassDetailsViewModel ()
@property (strong, nonatomic) NSArray *fees;
@property (strong, nonatomic) EHICarClassPriceSummary *priceSummary;
@end

@implementation EHIClassDetailsViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title = EHILocalizedString(@"reservation_class_details_navigation_title", @"Class Details", @"navigation bar title for a car class details screen");
        _priceHeader = [EHISectionHeaderModel modelWithTitle:EHILocalizedString(@"car_class_details_whats_included_text", @"WHATS INCLUDED IN THE PRICE?", @"")];
        _actionButtonTitle = EHILocalizedString(@"reservation_class_details_select_button_title", @"SELECT THIS CLASS", @"title for a button that allows a user to select a car class");
        _redemptionModel = !self.builder.hideRedemption ? [EHIRedemptionPointsViewModel modelWithType:EHIRedemptionBannerTypeClassDetails] : nil;
    }
    
    return self;
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHICarClass class]]) {
        // populate the view with what we have so far
        self.carClass = model;
        // fetch the details for the car class
        [self fetchDetailsForCarClass:model];
    }
}

# pragma mark - Selection

- (void)selectClass
{
    [EHIAnalytics trackAction:EHIAnalyticsResActionSelectClass handler:^(EHIAnalyticsContext *context) {
        [self.builder encodeClassSelection:self.carClass context:context];
    }];
    
    // alert user to call location if required
    if(self.carClass.requiresCallForAvailability) {
        [self promptCallForAvailability];
    }
    // just show car class extras screen
    else {
        [self showNextScreenWithCarClass:self.carClass];
    }
}

//
// Helpers
//

- (void)promptCallForAvailability
{
    EHIAlertViewBuilder *alertView = EHIAlertViewBuilder.new
        .title(EHILocalizedString(@"reservation_call_for_availability_prompt_call_title", @"Call?", @""))
        .message(EHILocalizedString(@"reservation_call_for_availability_prompt_call_message", @"Call this location to learn more about availability for this car class.", @""))
        .cancelButton(EHILocalizedString(@"standard_button_cancel", @"Cancel", @"Standard cancel button title"))
        .button(EHILocalizedString(@"standard_button_call", @"Call", @""));
    
    alertView.show(^(NSInteger index, BOOL canceled) {
        if(!canceled) {
            [UIApplication ehi_promptPhoneCall:self.carClass.availabilityPhoneNumber];
        }
    });
}

# pragma mark - Selection

- (BOOL)shouldSelectLineItemAtIndexPath:(NSIndexPath *)indexPath
{
    // disable selection outside price summary
    if (indexPath.section == EHIClassDetailsSectionTermsAndConditions) {
        return YES;
    } else if (indexPath.section != EHIClassDetailsSectionPriceSummary) {
        return NO;
    }
   
    // select fee summary items or those with extras
    EHICarClassPriceLineItem *item = [self lineItemAtIndex:indexPath.item];
    return item.type == EHIReservationLineItemTypeFeeSummary
        || item.extra != nil;
}

- (void)selectLineItemAtIndexPath:(NSIndexPath *)indexPath
{
    EHICarClassPriceLineItem *lineItem = [self lineItemAtIndex:indexPath.item];
    
    // show the summary modal if fees were tapped
    if(lineItem.type == EHIReservationLineItemTypeFeeSummary) {
        [EHIAnalytics trackAction:EHIAnalyticsResActionFees handler:nil];
        
        self.router.transition
            .present(EHIScreenReservationFees).object(self.fees).start(nil);
    } else if (indexPath.section == EHIClassDetailsSectionTermsAndConditions) {
        self.router.transition.present(EHIScreenTermsAndConditions).object(self.builder.reservation).start(nil);
    } // show the extra modal if we've got one
    else if(lineItem.extra) {
        
        [EHIAnalytics trackAction:EHIAnalyticsActionShowModal handler:^(EHIAnalyticsContext *context) {
            context[EHIAnalyticsModalSubjectKey] = lineItem.extra.code;
        }];

        EHIInfoModalViewModel *infoModal = [[EHIInfoModalViewModel alloc] initWithModel:lineItem.extra];
        infoModal.secondButtonTitle = EHILocalizedString(@"standard_close_button", @"CLOSE", @"");
        [infoModal present:nil];
    }
}

# pragma mark - Setters

- (void)setCarClass:(EHICarClass *)carClass
{
    _carClass = carClass;
    
    self.priceSummary = [self vehicleRatesForCarClass:carClass].priceSummary;

    self.priceContext = self.priceSummary;
    BOOL usePrepay    = [self usePrepay:carClass];
    BOOL isSecretRate = self.carClass.isSecretRate;
    
    self.totalPriceViewModel = [[EHIReservationRentalPriceTotalViewModel alloc] initWithModel:self.carClass
                                                                               prepaySelected:usePrepay
                                                                                   paidAmount:nil
                                                                                 actualAmount:nil
                                                                              showOtherOption:NO
                                                                                       layout:EHIReservationRentalPriceTotalLayoutReview
                                                                                 isSecretRate:isSecretRate];

    // update the exposed view model
    EHICarClassViewModel *viewModel = [[EHICarClassViewModel alloc] initWithModel:carClass];
    viewModel.layout = EHICarClassLayoutClassDetails;
    viewModel.showSecretRate = isSecretRate;

    self.carClassViewModel = viewModel;
}

# pragma mark - Getters

- (NSArray *)priceLineItems
{
    if(!_priceLineItems) {
        _priceLineItems = @[].concat(@[
            self.nonFeeNonZeroItems,
            self.priceSummary.feeSummmary,
            self.additionalExtras,
            self.carClassExtras,
            self.mileage,
        ]).flatten;
    }
    
    return _priceLineItems;
}

- (NSArray<EHICarClassPriceLineItem *> *)nonFeeNonZeroItems
{
    // filter down the line items to the visible types (non-fee, non-zero line items)
    return (self.priceSummary.lineItems ?: @[]).select(^(EHICarClassPriceLineItem *lineItem) {
        return lineItem.type != EHIReservationLineItemTypeFee
        && lineItem.total.amount != 0.0f
        && lineItem.extra.status != EHICarClassExtraStatusOptional
        && lineItem.extra.status != EHICarClassExtraStatusWaived;
    });
}

- (NSArray<EHICarClassPriceLineItem *> *)additionalExtras
{
    NSArray *extras = (self.priceSummary.lineItems ?: @[]);
    
    NSArray *mandatory = (self.priceSummary.lineItems ?: @[]).select(^(EHICarClassPriceLineItem *lineItem){
        return lineItem.extra.isMandatory;
    });
    
    NSArray *selected = extras.select(^(EHICarClassPriceLineItem *lineItem){
        return lineItem.extra.status == EHICarClassExtraStatusOptional
            || lineItem.extra.status == EHICarClassExtraStatusWaived;
    }).select(^(EHICarClassPriceLineItem *lineItem){
        return lineItem.extra.selectedQuantity > 0;
    });
    
    return @[ mandatory, selected ].flatten;
}

- (NSArray<EHICarClassPriceLineItem *> *)carClassExtras
{
    return [EHIReservationSublistViewModel lineItemExtrasForClassClass:self.carClass prepay:self.carClass.supportsPrepay]
        ?: @[];
}

- (NSArray <EHICarClassPriceLineItem *> *)mileage
{
    return @[
        [EHICarClassPriceLineItem lineItemForMileage:self.carClass.mileage]
    ] ?: @[];
}

- (NSArray *)fees
{
    if(!_fees) {
        // extract the feets manually from the line items
        _fees = (self.priceSummary.lineItems ?: @[]).select(^(EHICarClassPriceLineItem *lineItem) {
            return lineItem.type == EHIReservationLineItemTypeFee;
        });
    }
    
    return _fees;
}

- (EHIModel *)termsModel
{
    if(!_termsModel) {
        _termsModel = [EHIModel placeholder];
    }
    
    return _termsModel;
}

# pragma mark - Services

- (void)fetchDetailsForCarClass:(EHICarClass *)carClass
{
    // if we've already fetched, then no need to do anything
    self.hasLoadedCarClassDetails = [carClass vehicleRateForPrepay:NO] != nil;
    if(self.hasLoadedCarClassDetails) {
        return;
    }
    
    // let our view know that we are loading
    self.isLoading = YES;
    
    EHIReservationHandler handler = ^(EHIReservation *reservation, EHIServicesError *error) {
        // no longer loading
        self.isLoading = NO;
        // set the car class if there wasn't an error
        if(!error.hasFailed) {
            self.carClass = reservation.selectedCarClass;
            self.hasLoadedCarClassDetails = YES;
        }
    };
    
    [[EHIServices sharedInstance] selectCarClass:carClass
                                     reservation:self.builder.reservation
                                        inModify:self.isModify
                                    selectPrepay:self.isPrepay
                                         handler:handler];
}

# pragma mark - Accessors

- (EHICarClassPriceLineItem *)lineItemAtIndex:(NSInteger)index
{
    return index < self.priceLineItems.count ? self.priceLineItems[index] : nil;
}

# pragma mark - MTRReactive

+ (NSArray *)nonreactiveProperties:(EHIClassDetailsViewModel *)model
{
    return @[
        @key(model.fees),
    ];
}

@end
