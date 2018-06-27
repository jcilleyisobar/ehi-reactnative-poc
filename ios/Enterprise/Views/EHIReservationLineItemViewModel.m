//
//  EHIReservationLineItemViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 4/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationLineItemViewModel.h"
#import "EHICarClassPriceSummary.h"
#import "EHIReservationLineItem.h"

@interface EHIReservationLineItemViewModel()
@property (strong, nonatomic) id <EHIReservationLineItemRenderable> lineItem;
@end

@implementation EHIReservationLineItemViewModel

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model conformsToProtocol:@protocol(EHIReservationLineItemRenderable)]) {
        [self updateWithLineItem:model];
    }
}

- (void)updateWithLineItem:(id <EHIReservationLineItemRenderable>)lineItem
{
    self.lineItem = lineItem;
    
    if(!lineItem.formattedTitle) {
        self.title = nil;
        self.accessoryTitle = nil;
        return;
    }
    
    EHIAttributedStringBuilder *builder = [self stringBuilderForLineItem:lineItem];

    // append the quantity if it exceeds 1
    if(lineItem.quantity > 1) {
        NSString *quantity = [NSString stringWithFormat:@"(x%@)", @(lineItem.quantity).description];
        builder.space.appendText(quantity)
            .size(14.0).color([UIColor ehi_grayColor3]);
    }
    
    // append rate if needed if it exists and the quantity is greater than one
    if(lineItem.formattedRate && lineItem.quantity > 1) {
        builder.space.appendText(lineItem.formattedRate)
            .size(14.0).color([UIColor ehi_grayColor3]);
    }
    
    self.title = builder.string;

    if(lineItem.formattedTotal) {
        self.accessoryTitle = [[NSAttributedString alloc] initWithString:lineItem.formattedTotal];
    }
    
    // set the subtitle if supported
    if([lineItem respondsToSelector:@selector(formattedSubtitle)]) {
        self.subtitle = lineItem.formattedSubtitle;
    }
}

- (EHIAttributedStringBuilder *)stringBuilderForLineItem:(id<EHIReservationLineItemRenderable>)lineItem
{
    EHIAttributedStringBuilder *builder = [EHIAttributedStringBuilder new]
        .text(lineItem.formattedTitle).size(17.0);
    
    if(lineItem.hasDetails) {
        builder.color([UIColor ehi_greenColor]);
    }
    
    return builder;
}

- (BOOL)isLearnMore
{
    return [self.lineItem isKindOfClass:[EHIReservationLineItem class]] && self.lineItem.hasDetails;
}

@end
