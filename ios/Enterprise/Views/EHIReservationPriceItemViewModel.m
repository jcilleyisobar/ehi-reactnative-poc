//
//  EHIReservationPriceItemViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 7/14/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReservationPriceItemViewModel.h"
#import "EHIReservationLineItem.h"
#import "EHIInfoModalViewModel.h"

@interface EHIReservationPriceItemViewModel ()
@property (strong, nonatomic) EHICarClassPriceLineItem *lineItem;
@end

@implementation EHIReservationPriceItemViewModel

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model conformsToProtocol:@protocol(EHIReservationLineItemRenderable)]) {
        self.lineItem       = model;
        self.title          = self.buildTitle;
        self.accessoryTitle = EHIAttributedStringBuilder.new.appendText(self.buildAccessoryTitle).string;
    }
}

- (NSString *)buildAccessoryTitle
{
    return [self.lineItem formattedTotal];
}

- (BOOL)isCharged
{
    return self.lineItem.isCharged;
}

- (NSAttributedString *)buildTitle
{
    EHIAttributedStringBuilder *builder = EHIAttributedStringBuilder.new;
    NSString *lineTitle = [self.lineItem formattedTitle] ?: @"";
    builder.appendText(lineTitle);
    
    if(self.hasDetails) {
        builder.color([UIColor ehi_greenColor]);
    }
    
    NSString *lineRate  = [self.lineItem formattedRate];
    if(lineRate) {
        NSString *formattedRate = [NSString stringWithFormat:@"(%@)", lineRate];
        
        if(self.lineItem.type == EHIReservationLineItemTypeRedemption) {
            builder = builder.newline;
        } else {
            builder = builder.space;
        }
        
        builder.appendText(formattedRate).fontStyle(EHIFontStyleLight, 14.0f).color([UIColor ehi_grayColor3]);
    }
    
    return builder.string;
}

- (BOOL)isLearnMore
{
    if([self.lineItem respondsToSelector:@selector(isLearnMore)]) {
        return self.lineItem.isLearnMore;
    }

    return NO;
}

- (BOOL)hasDetails
{
    if([self.lineItem respondsToSelector:@selector(longDetails)]) {
        return self.lineItem.extra.longDetails.length > 0;
    }
    
    return NO;
}

- (void)showDetail
{
    if(self.hasDetails) {
       
        [EHIAnalytics trackAction:EHIAnalyticsActionShowModal handler:^(EHIAnalyticsContext *context) {
            context[EHIAnalyticsModalSubjectKey] = self.lineItem.extra.code;
        }];
        
        // present the info modal for this extra
        EHIInfoModalViewModel *infoModal = [[EHIInfoModalViewModel alloc] initWithModel:self.lineItem.extra];
        infoModal.secondButtonTitle = EHILocalizedString(@"standard_close_button", @"CLOSE", @"");
        [infoModal present:nil];
    }
    
    if(self.isLearnMore) {
        ehi_call(self.lineItem.action)();
    }
}

@end
