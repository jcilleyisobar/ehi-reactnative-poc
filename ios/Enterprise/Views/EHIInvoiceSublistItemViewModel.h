//
//  EHIInvoiceSublistItemViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 7/20/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIInvoiceSublistItemViewModel : EHIViewModel <MTRReactive>
@property (copy, nonatomic) NSAttributedString *title;
@property (copy, nonatomic) NSAttributedString *subtitle;

+ (instancetype)modelWithTitle:(NSAttributedString *)title subtitle:(NSAttributedString *)subtitle;

@end
