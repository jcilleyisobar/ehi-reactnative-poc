//
//  EHIClassDetailsPriceLineItemViewModel.m
//  Enterprise
//
//  Created by mplace on 3/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIClassDetailsPriceLineItemViewModel.h"
#import "EHICarClassPriceLineItem.h"
#import "EHIPriceFormatter.h"

@implementation EHIClassDetailsPriceLineItemViewModel

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    // otherwise, render this as a line item
    if([model isKindOfClass:[EHICarClassPriceLineItem class]]) {
        [self updateWithLineItem:model];
    }
}

- (void)updateWithLineItem:(EHICarClassPriceLineItem *)lineItem
{
    BOOL isSummary = lineItem.type == EHIReservationLineItemTypeFeeSummary;
   
    self.title          = lineItem.formattedTitle;
    self.rateString     = lineItem.formattedRate;
    self.accessoryTitle = lineItem.formattedTotal;
    self.hasDetails     = lineItem.extra || isSummary;
    self.hideIcon       = !isSummary;
}

@end
