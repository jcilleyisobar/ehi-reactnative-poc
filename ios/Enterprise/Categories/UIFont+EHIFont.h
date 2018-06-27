//
//  UIFont+EHIFont.h
//  Enterprise
//
//  Created by Ty Cobb on 1/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

typedef NS_ENUM(NSInteger, EHIFontStyle) {
    // font weights: (listed in precedence order)
    EHIFontStyleExtraLight  = 1 << 0,
    EHIFontStyleLight       = 1 << 1,
    EHIFontStyleRegular     = 1 << 2,
    EHIFontStyleMedium      = 1 << 3,
    EHIFontStyleBold        = 1 << 4,
    EHIFontStyleHeavy       = 1 << 5,
   
    // font styles
    EHIFontStyleItalic  = 1 << 31
};

@interface UIFont (EHIFont)

+ (NSString *)ehi_fontName;

/**
 @brief Returns the font for the correct style/size combo
 
 If there is no font for the specified style, this method throws an exception.
 
 @param style A bitmask of options to determine the correct font
 @param size The point size of the font to create
 
 @return The @c UIFont for the specified style
*/

+ (instancetype)ehi_fontWithStyle:(EHIFontStyle)style size:(CGFloat)size;

/**
 @brief  Returns the correct kerning value for the given font
 
 @param font The font for which a kerning should be determined
 
 @return The kerning width for the provided @c UIFont
 */
+ (NSNumber *)ehi_kerningForFont:(UIFont *)font;

@end

@interface UIFont (Debug)

/**
 @brief Prints out the list of font famlies.
 
 If the @c families parameter is passed, the method will print out the fonts for the
 those families. Otherwise, this just prints out all the family names.
 
 This method only prints in @c DEBUG and with logging set to verbose.

 @param families The font families to filter by, or @c nil.
*/

+ (void)ehi_listFonts:(NSArray *)families;

@end
