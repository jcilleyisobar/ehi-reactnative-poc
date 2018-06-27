//
//  EHIRentalsCondensedReceiptViewModel.m
//  Enterprise
//
//  Created by cgross on 7/19/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIRentalsCondensedReceiptViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIUserRental.h"
#import "EHIPriceFormatter.h"

@implementation EHIRentalsCondensedReceiptViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        
        _pickupTitle = EHILocalizedString(@"trip_summary_pick_up", @"PICK-UP", @"");
        _returnTitle = EHILocalizedString(@"trip_summary_return", @"RETURN", @"");
        _totalTitle  = EHILocalizedString(@"trip_summary_final_total", @"Final Total", @"");
        _pointsTitle = EHILocalizedString(@"trip_summary_points_earned", @"Points Earned", @"");
    }
    
    return self;
}

- (void)updateWithModel:(EHIUserRental *)model
{
    [super updateWithModel:model];
    
    if ([model isKindOfClass:[EHIUserRental class]]) {
        [self initWithRental:model];
    }
}

- (void)initWithRental:(EHIUserRental *)rental
{
    
    NSString *rentalAgreementTitle = EHILocalizedString(@"invoice_rental_number", @"Rental Agreement #: #{rental_number}", @"");
    NSString *rentalNumber = rental.rentalAgreementNumber ?: @"";
    rentalAgreementTitle = [rentalAgreementTitle ehi_applyReplacementMap:@{
        @"rental_number" : rentalNumber ?: @"",
    }];
    _rentalAgreementNumber = rentalAgreementTitle;

    NSString *contractName  = rental.contractName ?: @"";
    NSString *contractTitle = EHILocalizedString(@"invoice_rental_contract_name", @"Contract: #{contract_name}", @"");
    contractTitle = [contractTitle ehi_applyReplacementMap:@{
        @"contract_name" : contractName,
    }];
    _contract       = contractTitle;
    _showContract   = contractName.length > 0;
    
    _pickupDate     = rental.pickupTimeDisplay;
    _pickupLocation = rental.pickupLocation.displayName;
    _pickupCity     = [rental.pickupLocation.address formattedAddress:YES];
    
    _returnDate     = rental.returnTimeDisplay;
    _returnLocation = rental.returnLocation.displayName;
    _returnCity     = [rental.returnLocation.address formattedAddress:YES];
    
    _totalPrice     = [EHIPriceFormatter format:rental.priceSummary].string;
    _points         = rental.pointsEarned;
    _showPoints     = rental.pointsEarned.length > 0;
}

@end
