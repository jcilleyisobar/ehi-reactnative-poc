//
//  EHIReservationPriceSublistViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 7/14/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIReservationPriceSublistViewModel.h"
#import "EHICarClass.h"
#import "EHICarClassPriceSummary.h"
#import "EHIPriceFormatter.h"
#import "EHIReservationPriceItemViewModel.h"
#import "EHIReservationBuilder.h"

@interface EHIReservationPriceSublistViewModel ()
@property (assign, nonatomic) BOOL prepay;
@property (assign, nonatomic) BOOL skipPrePayCheck;

@property (strong, nonatomic) NSArray *rentalItems;
@property (strong, nonatomic) NSArray *adjustmentItems;
@property (strong, nonatomic) NSArray *extrasItems;
@property (strong, nonatomic) NSArray *taxesFeesItems;

@property (strong, nonatomic) NSArray *modelsForRentalItems;
@property (strong, nonatomic) NSArray *modelsForAdjustmentItems;
@property (strong, nonatomic) NSArray *modelsForExtrasItems;
@property (strong, nonatomic) NSArray *modelsForTaxesFeesItems;

@property (strong, nonatomic) NSMutableDictionary *itemsState;
@property (assign, nonatomic) EHIReservationPriceSublistSectionState defaultState;
@end

@implementation EHIReservationPriceSublistViewModel

- (instancetype)initWithCarClass:(EHICarClass *)carClass prepay:(BOOL)prepay
{
    if(self = [super init]) {
        self.prepay   = prepay;
        self.carClass = carClass;
        self.defaultState = EHIReservationPriceSublistSectionStateCollapsed;
    }
    
    return self;
}

- (instancetype)initWithRental:(EHIUserRental *)rental
{
    if(self = [super init]) {
        self.skipPrePayCheck = YES;
        self.defaultState    = EHIReservationPriceSublistSectionStateExpanded;
        self.carClass        = rental.carClassDetails;
    }
    
    return self;
}

- (void)setCarClass:(EHICarClass *)carClass
{
    _carClass = carClass;
    
    [self constructModels];
    [self constructItemsState];
}

- (void)constructModels
{
    // mileage
    self.mileageModel = [[EHIReservationPriceMileageViewModel alloc] initWithModel:self.carClass.mileage];
    
    self.adjustmentItems    = [self buildModelFromLineItems:self.buildAdjustmentItems];
    self.rentalItems        = [self buildModelFromLineItems:self.buildRentalModels];
    self.extrasItems        = [self buildModelFromLineItems:self.buildExtrasList];
    self.taxesFeesItems     = [self buildModelFromLineItems:self.buildTaxesFeesList];
}

- (void)constructItemsState
{
    EHIReservationPriceSublistSectionState defaultState = self.defaultState;
    NSDictionary *states = @{
        @(EHIReservationPriceSublistRental)        : @(defaultState),
        @(EHIReservationPriceSublistAdjustment)    : @(defaultState),
        @(EHIReservationPriceSublistExtras)        : @(defaultState),
        @(EHIReservationPriceSublistTaxesFees)     : @(defaultState)
    };
    
    self.itemsState = [states mutableCopy];
}

- (EHIPlacardViewModel *)placard
{
    BOOL showPlacard = self.carClass.isNegotiatedRate || self.carClass.isPromotionalRate;
    return showPlacard ? [[EHIPlacardViewModel alloc] initWithType:EHIPlacardTypePriceDetails carClass:self.carClass] : nil;
}

- (EHIReservationPriceViewModel *)modelForPriceSection:(EHIReservationPriceSublistSection)section
{
    NSString *title;
    EHIPrice *price;
    
    switch (section) {
        case EHIReservationPriceSublistRental: {
            title = EHILocalizedString(@"price_section_title_rental", @"Rental", @"");
            price = self.priceSummary.viewTotalVehicle;
            break;
        }
        case EHIReservationPriceSublistAdjustment: {
            title = EHILocalizedString(@"price_section_title_adjustments", @"Adjustments", @"");
            price = self.priceSummary.viewTotalSavings;
            break;
        }
        case EHIReservationPriceSublistExtras: {
            title = EHILocalizedString(@"price_section_title_extras", @"Extras", @"");
            price = self.priceSummary.viewTotalExtrasCoverage;
            break;
        }
        case EHIReservationPriceSublistTaxesFees: {
            title = EHILocalizedString(@"price_section_title_taxes_fees", @"Taxes and Fees", @"");
            price = self.priceSummary.viewTotalTaxesFees;
            break;
        }
        default: return nil;
    }
    
    BOOL shouldShowIncludeLabel = ![self areThereChargedItemsForSection:section];
    NSString *total = shouldShowIncludeLabel ? EHILocalizedString(@"payment_line_item_included", @"Included", @"") : [self formatPrice:price];
    
    return [[EHIReservationPriceViewModel alloc] initWithTitle:title total:total];
}

- (NSString *)formatPrice:(EHIPrice *)price
{
    return price ? [EHIPriceFormatter format:price].string : nil;
}

# pragma mark - Rental

- (NSArray *)buildRentalModels
{
    NSArray *vehicleRates = (self.priceSummary.lineItems ?: @[]).select(^(EHICarClassPriceLineItem *item){
        return item.type == EHIReservationLineItemTypeVehicleRate;
    });
    
    return @[vehicleRates].flatten;
}

# pragma mark - Adjustments

- (NSArray *)buildAdjustmentItems
{
    return (self.priceSummary.lineItems ?: @[]).select(^(EHICarClassPriceLineItem *lineItem) {
        return lineItem.type == EHIReservationLineItemTypeSavings || lineItem.type == EHIReservationLineItemTypeRedemption;
    }).sortBy(^(EHICarClassPriceLineItem *price) {
        return price.type;
    });
}

# pragma mark - Extras

- (NSArray *)buildExtrasList
{
    NSArray *equiptmentItems = (self.priceSummary.lineItems ?: @[]).select(^(EHICarClassPriceLineItem *item){
        return item.type == EHIReservationLineItemTypeEquipment;
    });
    
    NSArray *coverageItems = (self.priceSummary.lineItems ?: @[]).select(^(EHICarClassPriceLineItem *item){
        return item.type == EHIReservationLineItemTypeCoverage;
    });
    
    return @[equiptmentItems, coverageItems].flatten;
}

# pragma mark - Taxes and Fees

- (NSArray *)buildTaxesFeesList
{
    NSArray *taxesFees = (self.priceSummary.lineItems ?: @[])
    .select(^(EHICarClassPriceLineItem *lineItem){
        return lineItem.type == EHIReservationLineItemTypeFee;
    });
    
    // append learn more if necessary
    if(taxesFees.count > 0) {
        EHIReservationLineItem * learnMore = [EHIReservationLineItem lineItemForLearnMoreButton];
        taxesFees = [taxesFees ehi_safelyAppend:learnMore];
    }
    
    return taxesFees;
}

# pragma mark - Summary and Rates

- (EHICarClassPriceSummary *)priceSummary
{
    return self.vehicleRate.priceSummary;
}

- (EHICarClassVehicleRate *)vehicleRate
{
    // skip prepay means that there's no info about the payment.
    // since services returns a slightly different object from the invoice response, we have to add this logic
    EHICarClassVehicleRate *vehicleRate;
    if(self.skipPrePayCheck) {
        vehicleRate = self.carClass.vehicleRates.firstObject;
    } else {
        vehicleRate = [self.carClass vehicleRateForPrepay:self.prepay];
    }
    
    return vehicleRate;
}

# pragma mark - Generator

- (NSArray *)buildModelFromLineItems:(NSArray *)lineItems
{
    NSInteger size = lineItems.count;
    return (lineItems ?: @[]).map(^(id<EHIReservationLineItemRenderable> item, int index){
        EHIReservationPriceItemViewModel *model = [[EHIReservationPriceItemViewModel alloc] initWithModel:item];
        model.isLastInSection = index == size - 1;
        
        return model;
    });
}

- (NSArray *)itemsForSection:(EHIReservationPriceSublistSection)section
{
    switch (section) {
        case EHIReservationPriceSublistRentalItems: {
            return self.rentalItems;
        }
        case EHIReservationPriceSublistAdjustmentItems: {
            return self.adjustmentItems;
        }
        case EHIReservationPriceSublistExtrasItems: {
            return self.extrasItems;
        }
        case EHIReservationPriceSublistTaxesFeesItems: {
            return self.taxesFeesItems;
        }
        default: return nil;
    }
}

- (BOOL)areThereChargedItemsForSection:(EHIReservationPriceSublistSection)section
{
    NSArray *items;
    switch (section) {
        case EHIReservationPriceSublistRental: {
            items = self.rentalItems;
            break;
        }
        case EHIReservationPriceSublistAdjustment: {
            items = self.adjustmentItems;
            break;
        }
        case EHIReservationPriceSublistExtras: {
            items = self.extrasItems;
            break;
        }
        case EHIReservationPriceSublistTaxesFees: {
            items = self.taxesFeesItems;
            break;
        }
        default: return false;
    }
    
    return (items ?: @[]).find(^(EHIReservationPriceItemViewModel *lineItem) {
        return lineItem.isCharged;
    }) != nil;
}

# pragma mark - Expand/Collapse

- (EHIReservationPriceSublistSectionState)stateForSection:(EHIReservationPriceSublistSection)section
{
    return [self.itemsState[@(section)] integerValue];
}

- (void)expandCollapseSection:(EHIReservationPriceSublistSection)section
{
    EHIReservationPriceSublistSectionState state = [self stateForSection:section];
    switch (state) {
        case EHIReservationPriceSublistSectionStateCollapsed: {
            [self expandSection:section];
            break;
        }
        case EHIReservationPriceSublistSectionStateExpanded: {
            [self collapseSection:section];
            break;
        }
    }
}

- (void)collapseSection:(EHIReservationPriceSublistSection)section
{
    self.itemsState[@(section)] = @(EHIReservationPriceSublistSectionStateCollapsed);
    
    switch (section) {
        case EHIReservationPriceSublistRental:
            self.modelsForRentalItems = nil;
            break;
        case EHIReservationPriceSublistAdjustment:
            self.modelsForAdjustmentItems = nil;
            break;
        case EHIReservationPriceSublistExtras:
            self.modelsForExtrasItems = nil;
            break;
        case EHIReservationPriceSublistTaxesFees:
            self.modelsForTaxesFeesItems = nil;
            break;
        default:
            break;
    }
}

- (void)expandSection:(EHIReservationPriceSublistSection)section
{
    self.itemsState[@(section)] = @(EHIReservationPriceSublistSectionStateExpanded);
    
    switch (section) {
        case EHIReservationPriceSublistRental:
            self.modelsForRentalItems = self.rentalItems;
            break;
        case EHIReservationPriceSublistAdjustment:
            self.modelsForAdjustmentItems = self.adjustmentItems;
            break;
        case EHIReservationPriceSublistExtras:
            self.modelsForExtrasItems = self.extrasItems;
            break;
        case EHIReservationPriceSublistTaxesFees:
            self.modelsForTaxesFeesItems = self.taxesFeesItems;
            break;
        default:
            break;
    }
}

@end
