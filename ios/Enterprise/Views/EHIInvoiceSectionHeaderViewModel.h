//
//  EHIInvoiceSectionHeaderViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 7/26/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIInvoiceSectionHeaderViewModel : EHIViewModel <MTRReactive>
@property (copy, nonatomic) NSString *title;
@property (copy, nonatomic) NSString *actionTitle;
@end
