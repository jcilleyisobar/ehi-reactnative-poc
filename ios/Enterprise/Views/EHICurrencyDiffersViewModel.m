//
//  EHICurrencyDiffersViewModel.m
//  Enterprise
//
//  Created by Rafael Machado on 06/10/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHICurrencyDiffersViewModel.h"
#import "EHIPriceContext.h"

@implementation EHICurrencyDiffersViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        if([model conformsToProtocol:@protocol(EHIPriceContext)]) {
            [self updateWithPriceContext:model];
        }
    }
    
    return self;
}

- (void)updateWithPriceContext:(id<EHIPriceContext>)priceContext
{
    NSString *format = EHILocalizedString(@"car_class_currency_code_differs_title", @"PRICES DISPLAYED IN: #{currency_code}", @"title for a transparency total cell");
    
    CGFloat fontSize   = 16.0f;
    NSString *currency = [self currencyForPriceContext:priceContext];
    NSAttributedString *price = EHIAttributedStringBuilder.new.appendText(currency).fontStyle(EHIFontStyleBold, fontSize).string;
    
    _title = EHIAttributedStringBuilder.new.appendText(format).fontStyle(EHIFontStyleLight, fontSize).replace(@"#{currency_code}", price).string;
}

- (NSString *)currencyForPriceContext:(id<EHIPriceContext>)priceContext
{
    NSString *code   = priceContext.viewPrice.code ?: @"";
    NSString *symbol = priceContext.viewPrice.symbol ?: @"";
    
    if([code ehi_isEqualToStringIgnoringCase:symbol]) {
        return code;
    } else {
        return [NSString stringWithFormat:@"%@ (%@)", code, symbol];
    }
}

@end
