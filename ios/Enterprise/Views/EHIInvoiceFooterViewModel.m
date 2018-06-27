//
//  EHIInvoiceFooterViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 7/20/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIInvoiceFooterViewModel.h"
#import "EHIUserRentals.h"

@implementation EHIInvoiceFooterViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _deductionMessage    = EHILocalizedString(@"invoice_tax_deduction_warn", @"This receipt does not entitle you to a tax deduction", @"");
        _enterpriseBrandName = EHILocalizedString(@"invoice_enterprise", @"Enterprise Rent-A-Car", @"");
    }
    
    return self;
}

- (void)updateWithModel:(id)model
{
    if([model isKindOfClass:[EHIUserRental  class]]) {
        [self updateWithRental:(EHIUserRental *)model];
    }
}

- (void)updateWithRental:(EHIUserRental *)rental
{
    self.varNumber     = [self vatNumberForRental:rental];
}

-(void)updateWithInvoiceNumber:(NSString *) invoiceNumber
{
    self.invoiceNumber = [self invoiceNumberTextForNumber:invoiceNumber];
    
}

//
// Helpers
//

- (NSString *)vatNumberForRental:(EHIUserRental *)rental
{
    NSString *vatNumber = rental.vatNumber;
    if(vatNumber.length > 0) {
        NSString *vatTitle  = EHILocalizedString(@"invoice_vat_number", @"VAT#: #{vat_number}", @"");
        return [vatTitle ehi_applyReplacementMap:@{
                   @"vat_number" : vatNumber
               }];
    } else {
        return nil;
    }
}

- (NSString *)invoiceNumberTextForNumber:(NSString *)number
{
    NSString *invoiceTitle  = EHILocalizedString(@"invoice_number", @"Invoice: #{invoice_number}", @"");
    NSString *invoiceNumber = number ?: @"";
    
    return [invoiceTitle ehi_applyReplacementMap:@{
        @"invoice_number" : invoiceNumber
    }];
}

@end
