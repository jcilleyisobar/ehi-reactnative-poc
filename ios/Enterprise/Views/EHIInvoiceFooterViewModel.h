//
//  EHIInvoiceFooterViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 7/20/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIInvoiceFooterViewModel : EHIViewModel <MTRReactive>
@property (copy, nonatomic) NSString *deductionMessage;
@property (copy, nonatomic) NSString *varNumber;
@property (copy, nonatomic) NSString *invoiceNumber;
@property (copy, nonatomic) NSString *enterpriseBrandName;
@property (copy, nonatomic) NSString *enterpriseAddress;

-(void)updateWithInvoiceNumber:(NSString *) invoiceNumber;
@end


