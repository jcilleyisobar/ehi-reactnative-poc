//
//  EHIPriceFormatter.m
//  Enterprise
//
//  Created by Alex Koller on 3/27/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#define EHIPriceShrinkRatio 0.6f

#import "EHIPriceFormatter.h"

@interface EHIPriceFormatter ()
@property (strong, nonatomic) NSMutableAttributedString *result;
@property (assign, nonatomic) double amountB;
@property (strong, nonatomic) NSString *codeB;
@property (strong, nonatomic) NSString *symbolB;
@property (assign, nonatomic) BOOL scalesChangeB;
@property (assign, nonatomic) BOOL absB;
@property (assign, nonatomic) BOOL negB;
@property (assign, nonatomic) BOOL omitCurrencyCodeB;
@property (strong, nonatomic) NSLocale *localeB;
@property (assign, nonatomic) EHIPriceFontSize sizeB;
@property (assign, nonatomic) EHIFontStyle fontStyleB;
@property (nonatomic, readonly) NSNumberFormatter *numberFormatter;
@end

@implementation EHIPriceFormatter

+ (EHIPriceFormatter *)format:(EHIPrice *)price
{
    return [EHIPriceFormatter new].price(price);
}

- (instancetype)init
{
    if(self = [super init]) {
        _scalesChangeB = YES;
        _sizeB = EHIPriceFontSizeMedium;
        _fontStyleB = EHIFontStyleRegular;
    }
    
    return self;
}

- (EHIPriceFormatter *(^)(EHIPrice *))price
{
    return ^(EHIPrice *price) {
        self.amountB = price.amount;
        self.codeB   = price.code;
        self.symbolB = price.symbol;
        return self;
    };
}

- (EHIPriceFormatter *(^)(BOOL))scalesChange
{
    return ^(BOOL shrinkChange) {
        self.scalesChangeB = shrinkChange;
        return self;
    };
}

- (EHIPriceFormatter *(^)(EHIPriceFontSize))size
{
    return ^(EHIPriceFontSize size) {
        self.sizeB = size;
        return self;
    };
}

- (EHIPriceFormatter *(^)(EHIFontStyle))fontStyle
{
    return ^(EHIFontStyle fontStyle) {
        self.fontStyleB = fontStyle;
        return self;
    };
}

- (EHIPriceFormatter *(^)(BOOL))abs
{
    return ^(BOOL abs) {
        self.absB = abs;
        return self;
    };
}

- (EHIPriceFormatter *(^)(BOOL))neg
{
    return ^(BOOL neg) {
        self.negB = neg;
        return self;
    };
}

- (EHIPriceFormatter *(^)(BOOL))omitCurrencyCode
{
    return ^(BOOL omit) {
        self.omitCurrencyCodeB = omit;
        return self;
    };
}

- (EHIPriceFormatter *(^)(NSLocale *))locale
{
    return ^(NSLocale *locale) {
        self.localeB = locale;
        return self;
    };
}

- (EHIAttributedStringBuilder *)builder
{
    self.numberFormatter.locale       = self.localeB;
    self.numberFormatter.currencyCode = self.codeB;

    NSString *currencySymbol = self.symbolB ?: self.numberFormatter.currencySymbol;

    // if we are going to omit the currency symbol, we need to reconfigure it
    if(self.omitCurrencyCodeB) {
        self.numberFormatter.currencySymbol = @"";
    }

    // generate the raw price text
    NSNumber *amount = @(self.absB ? fabs(self.amountB) : (self.negB ? -self.amountB : self.amountB));
    NSString *text   = [self.numberFormatter stringFromNumber:amount];

    if(self.omitCurrencyCodeB) {
        self.numberFormatter.currencySymbol = currencySymbol;
    }

    // create the builder with teh shared properties
    EHIAttributedStringBuilder *builder = EHIAttributedStringBuilder.new
        .fontStyle(self.fontStyleB, self.sizeB);
    
    // if we don't scale the change, render all the text
    if(!self.scalesChangeB) {
        builder.text(text);
    }
    // split the components by the decimal separator
    else {
        NSArray *components = [text componentsSeparatedByString:self.numberFormatter.decimalSeparator];
        builder.appendText([components ehi_safelyAccess:0]);
        builder.appendFormat(@"%@%@", self.numberFormatter.decimalSeparator, [components ehi_safelyAccess:1])
            .fontStyle(self.fontStyleB, self.sizeB * EHIPriceShrinkRatio);
    }
    
    return builder;
}

- (NSAttributedString *)attributedString
{
    return [self builder].string;
}

- (NSString *)string
{
    return self.attributedString.string;
}

- (NSLocale *)localeB
{
    if(!_localeB) {
        return [NSLocale autoupdatingCurrentLocale];
    }
    
    return _localeB;
}

# pragma mark - Number Formatter

- (NSNumberFormatter *)numberFormatter
{
    static NSNumberFormatter *formatter;
    static dispatch_once_t onceToken;
    
    dispatch_once(&onceToken, ^{
        formatter = [NSNumberFormatter new];
        formatter.numberStyle = NSNumberFormatterCurrencyStyle;
    });
    
    return formatter;
}

@end
