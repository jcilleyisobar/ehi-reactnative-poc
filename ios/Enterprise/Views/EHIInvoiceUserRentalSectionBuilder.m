//
//  EHIInvoiceUserRentalSectionBuilder.m
//  Enterprise
//
//  Created by Rafael Ramos on 7/21/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIInvoiceUserRentalSectionBuilder.h"

@implementation EHIInvoiceUserRentalSectionBuilder

+ (NSArray *)modelSection:(EHIInvoiceUserRentalSection)section rental:(EHIUserRental *)rental
{
    switch (section) {
        case EHIInvoiceUserRentalSectionPriceDetails: {
            return [self priceDetails:rental];
            break;
        }
        case EHIInvoiceUserRentalSectionRenter: {
            return @[
                [self renterName:rental],
                [self renterMembershipNumber:rental],
                [self renterContract:rental],
                [self renterAddress:rental]
            ];
        }
        case EHIInvoiceUserRentalSectionVehicleDetails: {
            return @[
                [self carDrivenClass:rental],
                [self carChargedClass:rental],
                [self carMakeModel:rental],
                [self carLicensePlate:rental],
            ];
        }
        case EHIInvoiceUserRentalSectionDistance: {
            return @[
                [self rentalOdometerStart:rental],
                [self rentalOdometerEnd:rental],
                [self rentalTotalDistance:rental]
            ];
        }
    }
}

# pragma mark - Price Details

+ (NSArray *)priceDetails:(EHIUserRental *)rental
{
    EHICarClassVehicleRate *vehicleRates = rental.carClassDetails.vehicleRates.firstObject;
    if(vehicleRates) {
        return (vehicleRates.priceSummary.lineItems ?: @[]).map(^(EHICarClassPriceLineItem *lineItem){
            NSString *title    = lineItem.title ?: @"";
            NSString *subtitle = [self rateTotalForLineItem:lineItem];
            NSString *rate     = lineItem.formattedType ?: @"";
            
            EHIAttributedStringBuilder *titleBuilder = EHIAttributedStringBuilder.new;
            titleBuilder.appendText(title).fontStyle(EHIFontStyleLight, 18.0f);
            titleBuilder.space.appendText(rate).fontStyle(EHIFontStyleLight, 14.0f).color([UIColor ehi_grayColor3]);
            
            return [EHIInvoiceSublistItemViewModel modelWithTitle:titleBuilder.string subtitle:[self makeAttributed:subtitle]];
        });
    }
    
    return @[];
}

+ (NSString *)rateTotalForLineItem:(EHICarClassPriceLineItem *)lineItem
{
    NSString *total = lineItem.formattedTotal;
    BOOL hasFormattedRateTotal = [lineItem respondsToSelector:@selector(formattedRateTotal)];
    // use formattedRateTotal if exists
    if(hasFormattedRateTotal && lineItem.formattedRateTotal) {
        total = lineItem.formattedRateTotal;
    }
    
    return total;
}

# pragma mark - Rental

+ (EHIInvoiceSublistItemViewModel *)renterName:(EHIUserRental *)rental
{
    NSString *title     = EHILocalizedString(@"additional_info_name", @"Name", @"");
    NSString *firstName = rental.firstName ?: @"";
    NSString *lastName  = rental.lastName ?: @"";
    NSString *subtitle  = [NSString stringWithFormat:@"%@ %@", firstName, lastName];
    
    return [EHIInvoiceSublistItemViewModel modelWithTitle:[self makeAttributed:title] subtitle:[self makeAttributed:subtitle]];
}

+ (EHIInvoiceSublistItemViewModel *)renterMembershipNumber:(EHIUserRental *)rental
{
    NSString *title    = EHILocalizedString(@"additional_info_member_number", @"Member #", @"");
    NSString *subtitle = rental.membershipNumber ?: @"";
    
    return [EHIInvoiceSublistItemViewModel modelWithTitle:[self makeAttributed:title] subtitle:[self makeAttributed:subtitle]];
}

+ (EHIInvoiceSublistItemViewModel *)renterContract:(EHIUserRental *)rental
{
    NSString *title    = EHILocalizedString(@"additional_info_contract", @"Contract", @"");
    NSString *subtitle = rental.contractName;
    
    return [EHIInvoiceSublistItemViewModel modelWithTitle:[self makeAttributed:title] subtitle:[self makeAttributed:subtitle]];
}

+ (EHIInvoiceSublistItemViewModel *)renterAddress:(EHIUserRental *)rental
{
    NSString *title    = EHILocalizedString(@"additional_info_address", @"Address", @"");
    NSString *subtitle = [rental.address formattedAddress:YES];
    
    return [EHIInvoiceSublistItemViewModel modelWithTitle:[self makeAttributed:title] subtitle:[self makeAttributed:subtitle]];
}

# pragma mark - Vehicle

+ (EHIInvoiceSublistItemViewModel *)carDrivenClass:(EHIUserRental *)rental
{
    NSString *title    = EHILocalizedString(@"additional_info_driven_class", @"Driven Class", @"");
    NSString *subtitle = rental.carClassDetails.classDriven ?: @"";
    
    return [EHIInvoiceSublistItemViewModel modelWithTitle:[self makeAttributed:title] subtitle:[self makeAttributed:subtitle]];
}

+ (EHIInvoiceSublistItemViewModel *)carChargedClass:(EHIUserRental *)rental
{
    NSString *title    = EHILocalizedString(@"additional_info_charged_class", @"Charged Class", @"");
    NSString *subtitle = rental.carClassDetails.classChanged ?: @"";
    
    return [EHIInvoiceSublistItemViewModel modelWithTitle:[self makeAttributed:title] subtitle:[self makeAttributed:subtitle]];
}

+ (EHIInvoiceSublistItemViewModel *)carMakeModel:(EHIUserRental *)rental
{
    NSString *title    = EHILocalizedString(@"additional_info_model", @"Make/Model", @"");
    NSString *subtitle = rental.carClassDetails.makeModel;
    
    return [EHIInvoiceSublistItemViewModel modelWithTitle:[self makeAttributed:title] subtitle:[self makeAttributed:subtitle]];
}

+ (EHIInvoiceSublistItemViewModel *)carLicensePlate:(EHIUserRental *)rental
{
    NSString *title    = EHILocalizedString(@"additional_info_license_plate", @"License Plate", @"");
    NSString *subtitle = rental.carClassDetails.licensePlate;
    
    return [EHIInvoiceSublistItemViewModel modelWithTitle:[self makeAttributed:title] subtitle:[self makeAttributed:subtitle]];
}

# pragma mark - Distance

+ (EHIInvoiceSublistItemViewModel *)rentalOdometerStart:(EHIUserRental *)rental
{
    NSString *title    = EHILocalizedString(@"additional_info_odometer_start", @"Odometer Start", @"");
    EHICarClass *carClass = rental.carClassDetails;
    NSString *subtitle    = [NSString stringWithFormat:@"%@ %@", carClass.odometerStart, carClass.distanceUnit];
    
    return [EHIInvoiceSublistItemViewModel modelWithTitle:[self makeAttributed:title] subtitle:[self makeAttributed:subtitle]];
}

+ (EHIInvoiceSublistItemViewModel *)rentalOdometerEnd:(EHIUserRental *)rental
{
    NSString *title    = EHILocalizedString(@"additional_info_odometer_end", @"Odometer End", @"");
    EHICarClass *carClass = rental.carClassDetails;
    NSString *subtitle    = [NSString stringWithFormat:@"%@ %@", carClass.odometerEnd, carClass.distanceUnit];
    
    return [EHIInvoiceSublistItemViewModel modelWithTitle:[self makeAttributed:title] subtitle:[self makeAttributed:subtitle]];
}

+ (EHIInvoiceSublistItemViewModel *)rentalTotalDistance:(EHIUserRental *)rental
{
    NSString *title       = EHILocalizedString(@"additional_info_total_distance", @"Total Distance", @"");
    EHICarClass *carClass = rental.carClassDetails;
    NSString *subtitle    = [NSString stringWithFormat:@"%@ %@", carClass.distanceTraveled, carClass.distanceUnit];
    
    return [EHIInvoiceSublistItemViewModel modelWithTitle:[self makeAttributed:title] subtitle:[self makeAttributed:subtitle]];
}

+ (NSAttributedString *)makeAttributed:(NSString *)text
{
    EHIAttributedStringBuilder *builder = EHIAttributedStringBuilder.new;
    builder.appendText(text).fontStyle(EHIFontStyleLight, 18.0f);
    
    return builder.string;
}

# pragma mark - Section titles

+ (NSString *)titleForSection:(EHIInvoiceUserRentalSection)section
{
    switch(section) {
        case EHIInvoiceUserRentalSectionPriceDetails:
            return EHILocalizedString(@"price_section_title_rental_cost", @"RENTAL COST", @"header title for reservations confirmation screen's price details section");
        case EHIInvoiceUserRentalSectionRenter:
            return EHILocalizedString(@"additional_info_renter_details", @"RENTER DETAILS", @"");
        case EHIInvoiceUserRentalSectionVehicleDetails:
            return EHILocalizedString(@"additional_info_vehicle_details_title", @"VEHICLE DETAILS", @"");
        case EHIInvoiceUserRentalSectionDistance:
            return EHILocalizedString(@"additional_info_distance_title", @"DISTANCE", @"");
    }
}

@end
