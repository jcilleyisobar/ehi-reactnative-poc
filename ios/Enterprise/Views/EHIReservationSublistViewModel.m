//
//  EHIReservationConfirmationExtrasViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 4/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIReservationSublistViewModel.h"
#import "EHICarClassExtra.h"
#import "EHICarClassMileage.h"
#import "EHICarClassPriceLineItem.h"
#import "EHIReservationLineItem.h"
#import "EHIInfoModalViewModel.h"
#import "EHIPriceRateType.h"
#import "EHIReservationLineItem.h"

@implementation EHIReservationSublistViewModel

# pragma mark - Selection

- (void)selectItemAtIndexPath:(NSIndexPath *)indexPath
{
    EHIReservationSublistSection *section = self.sections[indexPath.section];
    id<EHIReservationLineItemRenderable> model = section.models[indexPath.item];
    
    if (model.hasDetails) {
        if ([model isKindOfClass:[EHICarClassPriceLineItem class]]) {
            [self showExtraForPriceLine:(EHICarClassPriceLineItem *)model];
        } else {
            if ([model respondsToSelector:@selector(action)]) {
                ehi_call(model.action)();
            }
        }
    }
}

//
// Helpers
//

- (void)showExtraForPriceLine:(EHICarClassPriceLineItem *)lineItem
{
    if([lineItem respondsToSelector:@selector(longDetails)] && lineItem.extra.longDetails) {
        [EHIAnalytics trackAction:EHIAnalyticsActionShowModal handler:^(EHIAnalyticsContext *context) {
            context[EHIAnalyticsModalSubjectKey] = lineItem.extra.code;
        }];

        // present the info modal for this extra
        EHIInfoModalViewModel *infoModal = [[EHIInfoModalViewModel alloc] initWithModel:lineItem.extra];
        infoModal.secondButtonTitle = EHILocalizedString(@"standard_close_button", @"CLOSE", @"");
        [infoModal present:nil];
    }
}

- (EHICarClassExtra *)extraAtIndexPath:(NSIndexPath *)indexPath
{
    // get the model at this index path
    EHIReservationSublistSection *section = self.sections[indexPath.section];
    id model = section.models[indexPath.item];
   
    // if these are line items, the extra is (maybe) nested here
    if(self.type == EHIReservationSublistTypeLineItem) {
        model = [(EHICarClassPriceLineItem *)model extra];
    }
    
    return model;
}

# pragma mark - Generators

+ (instancetype)sublistModelForCarClass:(EHICarClass *)carClass prepay:(BOOL)prepay
{
    NSArray *lineItems = [self lineItemsForCarClass:carClass prepay:prepay];
    
    NSDictionary *sectionTitles = @{
        @(EHIReservationLineItemTypeRedemption)  : EHILocalizedString(@"reservation_line_item_adjustments_title", @"ADJUSTMENTS", @""),
        @(EHIReservationLineItemTypeVehicleRate) : EHILocalizedString(@"reservation_line_item_rental_title", @"RENTAL", @""),
        @(EHIReservationLineItemTypeEquipment)   : EHILocalizedString(@"reservation_line_item_equipment_title", @"EQUIPMENT", @""),
        @(EHIReservationLineItemTypeCoverage)    : EHILocalizedString(@"reservation_line_item_coverage_title", @"PROTECTION", @""),
        @(EHIReservationLineItemTypeSavings)     : EHILocalizedString(@"reservation_line_item_savings_title", @"SAVINGS", @""),
        @(EHIReservationLineItemTypeFee)         : EHILocalizedString(@"reservation_line_item_taxes_fees_title", @"TAXES & FEES", @""),
        @(EHIReservationLineItemTypeFeeMileage)  : EHILocalizedString(@"reservation_line_item_mileage_title", @"MILEAGE", @"")
    };

    EHIReservationSublistViewModel *sublistModel = [EHIReservationSublistViewModel new];
    sublistModel.type = EHIReservationSublistTypeLineItem;
    
    // group line items by type
    NSDictionary *sectionModels = (lineItems ?: @[]).groupBy(^(id <EHIReservationLineItemRenderable> lineItem) {
        return @(lineItem.type);
    }).ehi_select(^BOOL(NSNumber *type, NSArray *lineItems) {
        return sectionTitles[type] != nil;
    });
  
    // sort the sections by the enumeration value and generate the sections
    sublistModel.sections = sectionTitles.allKeys.sort.map(^(NSNumber *section) {
        return [EHIReservationSublistSection sectionWithTitle:sectionTitles[section] models:sectionModels[section]];
    });
    
    // only return a model if we have at least one section
    return sublistModel.sections.count ? sublistModel : nil;
}


+ (NSArray *)lineItemsForCarClass:(EHICarClass *)carClass prepay:(BOOL)prepay
{
    NSArray *lineItems = @[
        [self lineItemExtrasForClassClass:carClass prepay:prepay],
        ([carClass vehicleRateForPrepay:prepay].priceSummary.lineItems ?: @[]),
        [EHIReservationLineItem lineItemForLearnMoreButton],
        (carClass.mileage ? [EHICarClassPriceLineItem lineItemForMileage:carClass.mileage] : @[]),
    ].flatten;
    
    // no line items if only learn more button exists
    return lineItems.count != 1 ? lineItems : @[];
}


@end

@implementation EHIReservationSublistViewModel (Generators)

+ (NSArray *)lineItemExtrasForClassClass:(EHICarClass *)carClass prepay:(BOOL)prepay
{
    NSArray *includedEquipmentItems = ([carClass vehicleRateForPrepay:prepay].extras.equipment ?: @[])
        .select(^(EHICarClassExtra *extra) { return extra.isIncluded; })
        .map(^(EHICarClassExtra *extra) {
            return [EHICarClassPriceLineItem lineItemForExtra:extra type:EHIReservationLineItemTypeEquipment];
        });
    
    NSArray *includedCoverageItems = ([carClass vehicleRateForPrepay:prepay].extras.insurance ?: @[])
        .select(^(EHICarClassExtra *extra) { return extra.isIncluded; })
        .map(^(EHICarClassExtra *extra) {
            return [EHICarClassPriceLineItem lineItemForExtra:extra type:EHIReservationLineItemTypeCoverage];
        });
    
    return @[
        includedEquipmentItems,
        includedCoverageItems
    ];
}

@end
