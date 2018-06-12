//
//  EHIClassSelectFootnoteViewModel.m
//  Enterprise
//
//  Created by mplace on 6/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIClassSelectFootnoteViewModel.h"
#import "EHIPriceContext.h"

@implementation EHIClassSelectFootnoteViewModel

- (instancetype)initWithModel:(id)model
{
    if (self = [super initWithModel:model]) {
        NSString *makeModelFootnote = EHILocalizedString(@"class_select_make_model_footnote_title", @"Or similar model with comparable features.", @"class select footnote describing the similar make model stipulation.");
        _makeModelTitle = [self footnoteForTitle:makeModelFootnote bullet:@"*"];
        
        NSString *sourceCurrencyFootnote = EHILocalizedString(@"class_select_source_currency_footnote_title", @"Total cost is estimated based on current exchange rates. Actual cost may vary at time of payment. FPO", @"class select footnote describing the source currency rules.");
        self.sourceCurrencyTitle = [self footnoteForTitle:sourceCurrencyFootnote bullet:@"**"];
    }
    
    return self;
}

- (void)updateWithModel:(id<EHIPriceContext>)price
{
    [super updateWithModel:price];
    
    self.hidesSourceCurrencyTitle = !price.viewCurrencyDiffersFromSourceCurrency;
}

//
// Helper
//

- (NSAttributedString *)footnoteForTitle:(NSString *)title bullet:(NSString *)bullet
{
    return EHIAttributedStringBuilder.new
        .fontStyle(EHIFontStyleLight, 14.f).text(bullet)
        .space.appendText(title).string;
}

@end
