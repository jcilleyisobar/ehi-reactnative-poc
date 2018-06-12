//
//  EHIPriceFormatter.h
//  Enterprise
//
//  Created by Alex Koller on 3/27/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIPrice.h"

typedef NS_ENUM(NSUInteger, EHIPriceFontSize) {
    EHIPriceFontSizeSmall  = 18,
    EHIPriceFontSizeMedium = 28
};

@interface EHIPriceFormatter : NSObject

/** Returns a formatter for the parameterized price */
+ (EHIPriceFormatter *)format:(EHIPrice *)price;

/** Whether or not to shrink change (if showing). Defaults to YES */
- (EHIPriceFormatter *(^)(BOOL))scalesChange;

/** Font size of attributed price. Defaults to EHIPriceFontSizeMedium */
- (EHIPriceFormatter *(^)(EHIPriceFontSize))size;

/** Font style of attributed price. Defaults to EHIFontStyleRegular */
- (EHIPriceFormatter *(^)(EHIFontStyle))fontStyle;

/** Whether or not to use the absolute price. Defaults to NO */
- (EHIPriceFormatter *(^)(BOOL))abs;

/** Whether or not to use the price is negative. Defaults to NO */
- (EHIPriceFormatter *(^)(BOOL))neg;

/** Whether or not to use the show the currency code. Defaults to YES */
- (EHIPriceFormatter *(^)(BOOL))omitCurrencyCode;

/** Update COR (This is used to inject locales on tests) */
- (EHIPriceFormatter *(^)(NSLocale *))locale;

/**
 @brief Creates a formatted price string builder using the supplied parameters.
 
 The price string is built using the format '{symbol}{price} {code}'. Any unsupplied parameters
 are skipped. The currency code is always shrunk based on the given size. The builder is returned
 to add any additional formatted.
 
 @return A builder with the formatted price string
*/

- (EHIAttributedStringBuilder *)builder;

/**
 Generates an attributed string using the supplied parameters and internal string builder.
 
 @return The formatted price string
*/

- (NSAttributedString *)attributedString;

/**
 Generates a string using the supplied parameters and internal string builder, but any font
 and size information is discarded.
 
 @return The formatted price string
*/

- (NSString *)string;

@end
