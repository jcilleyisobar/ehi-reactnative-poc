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

    NSAttributedString *titleAndRate = EHIAttributedStringBuilder.new
        .color([UIColor blackColor])
        .fontStyle(EHIFontStyleLight, 18.f)
        .text(lineItem.formattedTitle)
        .space
        .appendText(lineItem.formattedRate)
        .color([UIColor ehi_grayColor4])
        .fontStyle(EHIFontStyleLight, 14.f)
        .string;
   
    self.titleAndRate   = titleAndRate;
    self.accessoryTitle = lineItem.formattedTotal;
    self.hasDetails     = lineItem.extra || isSummary;
    self.hideIcon       = !isSummary;
}

@end
