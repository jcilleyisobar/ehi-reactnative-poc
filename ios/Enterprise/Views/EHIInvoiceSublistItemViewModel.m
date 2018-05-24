//
//  EHIInvoiceSublistItemViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 7/20/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIInvoiceSublistItemViewModel.h"

@implementation EHIInvoiceSublistItemViewModel

+ (instancetype)modelWithTitle:(NSAttributedString *)title subtitle:(NSAttributedString *)subtitle
{
    EHIInvoiceSublistItemViewModel *model = [EHIInvoiceSublistItemViewModel new];
    model.title     = title;
    model.subtitle  = subtitle;
    
    return model;
}

@end
