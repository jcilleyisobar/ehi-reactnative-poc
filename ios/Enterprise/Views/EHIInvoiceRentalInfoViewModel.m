//
//  EHIInvoiceRentalInfoViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 7/20/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIInvoiceRentalInfoViewModel.h"
#import "EHIUserRental.h"

@implementation EHIInvoiceRentalInfoViewModel

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

- (void)updateWithUserRental:(EHIUserRental *)userRental
{
    self.rentalDate      = [self rentaDateForRental:userRental];
    self.rentalAgreement = [self rentalAgreementForRental:userRental];
    self.contractInfo    = [self contractInfoForRental:userRental];
}

//
// Helpers
//

- (NSString *)rentaDateForRental:(EHIUserRental *)userRental
{
    NSString *rentalDateTitle = EHILocalizedString(@"invoice_rental_date", @"RENTAL FOR #{date}", @"");
    NSString *pickupDate      = [userRental.pickupDate ehi_stringForTemplate:@"MMM dd, yyyy"].uppercaseString ?: @"";
    return [rentalDateTitle ehi_applyReplacementMap:@{
        @"date" : pickupDate
    }];
}

- (NSString *)rentalAgreementForRental:(EHIUserRental *)userRental
{
    NSString *rentalAgreementTitle = EHILocalizedString(@"invoice_rental_number", @"Rental Agreement #: #{rental_number}", @"");
    NSString *rentalNumber         = userRental.rentalAgreementNumber ?: @"";
    
    return [rentalAgreementTitle ehi_applyReplacementMap:@{
        @"rental_number" : rentalNumber
    }];
}

- (NSString *)contractInfoForRental:(EHIUserRental *)userRental
{
    NSString *contractName = userRental.contractName ?: @"";
    if(contractName) {
        NSString *contractTitle = EHILocalizedString(@"invoice_rental_contract_name", @"Contract: #{contract_name}", @"");
        return [contractTitle ehi_applyReplacementMap:@{
            @"contract_name" : contractName
        }];
    } else {
        return nil;
    }
}

@end
