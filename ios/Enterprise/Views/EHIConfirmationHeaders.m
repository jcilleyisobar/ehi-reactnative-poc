//
//  EHIConfirmationHeaders.m
//  Enterprise
//
//  Created by Rafael Ramos on 6/6/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIConfirmationHeaders.h"

@interface EHIConfirmationHeaders ()
@property (strong, nonatomic) NSDictionary *sectionHeaders;
@end

@implementation EHIConfirmationHeaders

- (EHIReviewSectionHeaderViewModel *)headerForSection:(EHIConfirmationSection)section
{
    return self.sectionHeaders[@(section)];
}

- (NSDictionary *)sectionHeaders
{
    if(_sectionHeaders) {
        return _sectionHeaders;
    }
    EHIReviewSectionHeaderViewModel *rentalHeader = [[EHIReviewSectionHeaderViewModel alloc] initWithModel:EHILocalizedString(@"rental_section_title_rental", @"RENTAL", @"header title for reservations review screen's location section")];
    EHIReviewSectionHeaderViewModel *driverInfo   = [[EHIReviewSectionHeaderViewModel alloc] initWithModel:EHILocalizedString(@"reservation_review_details_section_title", @"DETAILS", @"")];
    EHIReviewSectionHeaderViewModel *priceDetails = [[EHIReviewSectionHeaderViewModel alloc] initWithModel:EHILocalizedString(@"price_section_title_rental_cost", @"RENTAL COST", @"header title for reservations confirmation screen's price details section")];
    return @{
             @(EHIConfirmationSectionRentalHeader)         : rentalHeader,
             @(EHIConfirmationSectionDriverInfo) : driverInfo,
             @(EHIConfirmationSectionPriceDetails) : priceDetails
    };
}


@end
