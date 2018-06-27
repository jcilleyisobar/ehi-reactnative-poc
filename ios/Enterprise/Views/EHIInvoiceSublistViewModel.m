//
//  EHIInvoiceSublistViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 7/20/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIInvoiceSublistViewModel.h"
#import "EHIUserRental.h"
#import "EHIInvoiceSublistItemViewModel.h"
#import "EHIInvoiceUserRentalSectionBuilder.h"

@interface EHIInvoiceSublistViewModel ()
@property (assign, nonatomic) EHIInvoiceSublistType type;
@end

@implementation EHIInvoiceSublistViewModel

- (instancetype)initWithModel:(id)model type:(EHIInvoiceSublistType)type
{
    if(self = [super initWithModel:model]) {
        _type = type;
        if([model isKindOfClass:[EHIUserRental class]]) {
            [self updateWithUserRental:(EHIUserRental *)model];
        }
    }
    
    return self;
}

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        if([model isKindOfClass:[EHIUserRental class]]) {
            [self updateWithUserRental:(EHIUserRental *)model];
        }
    }
    
    return self;
}

- (void)updateWithModel:(id)model
{
    if([model isKindOfClass:[EHIUserRental class]]) {
        [self updateWithUserRental:(EHIUserRental *)model];
    }
}

- (void)updateWithUserRental:(EHIUserRental *)rental
{
    switch (self.type) {
        case EHIInvoiceSublistTypePriceDetails: {
            self.sections = @[[self priceDetailsSection:rental]].reject([NSNull class]);
            break;
        }
        case EHIInvoiceSublistTypeAdditionalInfo: {
            self.sections = @[
                [self rentalSection:rental],
                [self vehicleSection:rental],
                [self distanceSection:rental]
            ];
            break;
        }
    }
}

# pragma mark - Price Details

- (id)priceDetailsSection:(EHIUserRental *)rental
{
    NSArray *models = [EHIInvoiceUserRentalSectionBuilder modelSection:EHIInvoiceUserRentalSectionPriceDetails
                                                                rental:rental];
    
    return [EHIReservationSublistSection sectionWithTitle:nil models:models] ?: [NSNull null];
}

# pragma mark - Rental

- (EHIReservationSublistSection *)rentalSection:(EHIUserRental *)rental
{
    NSString *title = [EHIInvoiceUserRentalSectionBuilder titleForSection:EHIInvoiceUserRentalSectionRenter];
    NSArray *models = [EHIInvoiceUserRentalSectionBuilder modelSection:EHIInvoiceUserRentalSectionRenter
                                                                rental:rental];

    return [EHIReservationSublistSection sectionWithTitle:title models:models];
}

# pragma mark - Vehicle

- (EHIReservationSublistSection *)vehicleSection:(EHIUserRental *)rental
{
    NSString *title = [EHIInvoiceUserRentalSectionBuilder titleForSection:EHIInvoiceUserRentalSectionVehicleDetails];
    NSArray *models = [EHIInvoiceUserRentalSectionBuilder modelSection:EHIInvoiceUserRentalSectionVehicleDetails
                                                                rental:rental];
    
    return [EHIReservationSublistSection sectionWithTitle:title models:models];
}

# pragma mark - Distance

- (EHIReservationSublistSection *)distanceSection:(EHIUserRental *)rental
{
    NSString *title = [EHIInvoiceUserRentalSectionBuilder titleForSection:EHIInvoiceUserRentalSectionDistance];
    NSArray *models = [EHIInvoiceUserRentalSectionBuilder modelSection:EHIInvoiceUserRentalSectionDistance
                                                                rental:rental];
    
    return [EHIReservationSublistSection sectionWithTitle:title models:models];
}

@end
