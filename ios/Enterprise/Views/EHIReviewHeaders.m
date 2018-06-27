//
//  EHIReviewHeaders.m
//  Enterprise
//
//  Created by Rafael Ramos on 6/3/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReviewHeaders.h"
#import "EHIReservationBuilder.h"

@interface EHIReviewHeaders ()
@property (strong, nonatomic) NSDictionary *sectionHeaders;
@end

@implementation EHIReviewHeaders

- (EHISectionHeaderModel *)headerForSection:(EHIReviewSection)section
{
    return self.sectionHeaders[@(section)];
}

- (NSDictionary *)sectionHeaders
{
    if(_sectionHeaders) {
        return _sectionHeaders;
    }

    // create headers
    EHIReviewSectionHeaderViewModel *rentalHeader = [[EHIReviewSectionHeaderViewModel alloc] initWithModel:EHILocalizedString(@"rental_section_title_rental", @"RENTAL", @"header title for reservations review screen's location section")];
    rentalHeader.hideDivider = YES;
    
    EHIReviewSectionHeaderViewModel *driverInfo   = [[EHIReviewSectionHeaderViewModel alloc] initWithModel:EHILocalizedString(@"reservation_review_details_section_title", @"DETAILS", @"")];
    EHIReviewSectionHeaderViewModel *priceDetails = [[EHIReviewSectionHeaderViewModel alloc] initWithModel:EHILocalizedString(@"price_section_title_rental_cost", @"RENTAL COST", @"header title for reservations confirmation screen's price details section")];
    EHIReviewSectionHeaderViewModel *prepayPaymentMethod = [[EHIReviewSectionHeaderViewModel alloc] initWithModel:EHILocalizedString(@"reservation_confirmation_payment_method_section_title", @"PAYMENT METHOD", @"")];

    return @{
        @(EHIReviewSectionRentalHeader)        : rentalHeader,
        @(EHIReviewSectionDriverInfo)          : driverInfo,
        @(EHIReviewSectionPriceDetails)        : priceDetails,
        @(EHIReviewSectionPaymentMethod)       : prepayPaymentMethod,
        @(EHIReviewSectionPaymentMethodLocked) : prepayPaymentMethod
    };
}

@end
