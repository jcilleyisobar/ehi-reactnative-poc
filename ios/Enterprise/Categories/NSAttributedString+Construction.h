//
//  NSAttributedString+Construction.h
//  Enterprise
//
//  Created by Ty Cobb on 2/27/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "UIFont+EHIFont.h"

@interface EHIAttributedStringBuilder : NSObject

/**
 @brief Instantiates a builder with the given string 
 
 Any attributes attached to this string are used as default attributes for subsequently
 appended text.
 
 @param string The text set for the entire builder
*/

- (instancetype)initWithString:(NSAttributedString *)string;

/** Sets the text for the entire attributed string */
- (EHIAttributedStringBuilder *(^)(NSString *))text;

/**
 @brief Appends an attributed string to the result
 
 Default attributes held by the builder override any attributes on this string.
 @param string The attributed text to append
*/

- (EHIAttributedStringBuilder *(^)(NSAttributedString *))append;

/**
 @brief Appends an unattributed text to the result
 
 Default attributes held by the builder are applied to the string before appending.
 @param string The unattributed text to append
*/

- (EHIAttributedStringBuilder *(^)(NSString *))appendText;

/**
 @brief Appends an unattributed formatted string to the result
 
 The formatting behavior of this method is identical to @c +stringWithFormat:. The
 range of the formatted string is considered the active range.
*/

- (EHIAttributedStringBuilder *(^)(NSString *, ...))appendFormat;

/**
 @brief Replace the occurence of string with an attributed string
 
 Default attributes held by the builder override any attributes on this string.
 @param string the text to replace 
 @param attributedString The attributed text to insert
 */

- (EHIAttributedStringBuilder *(^)(NSString *, NSAttributedString *))replace;


/** Manually updates the currently modified range of the result; subsequent changes will affect this range */
- (EHIAttributedStringBuilder *(^)(NSRange))range;
/** Sets attributes for last added text or defaults if no text is set */
- (EHIAttributedStringBuilder *(^)(NSDictionary *))attributes;


/** Sets the font for last added text or defaults if no text is set */
- (EHIAttributedStringBuilder *(^)(EHIFontStyle, CGFloat))fontStyle;
/** Sets the font for last added text or defaults if no text is set */
- (EHIAttributedStringBuilder *(^)(UIFont *))font;
/** Sets light font with size for last added text or defaults if no text is set */
- (EHIAttributedStringBuilder *(^)(CGFloat))size;
/** Sets text color for last added text or defaults if no text is set */
- (EHIAttributedStringBuilder *(^)(UIColor *))color;


- (EHIAttributedStringBuilder *(^)(NSParagraphStyle *))paragraphStyle;
/** Sets the line spacing and headIndent or defaults if no text is set */
- (EHIAttributedStringBuilder *(^)(CGFloat, CGFloat))lineSpacingAndHeadIndent;
/** Sets paragraph style for last added text or defaults if no text is set */
- (EHIAttributedStringBuilder *(^)(CGFloat, NSTextAlignment))paragraph;
/** Sets headIndent for last added text or defaults if no text is set */
- (EHIAttributedStringBuilder *(^)(CGFloat))headIndent;
/** Sets line spacing for last added text or defaults if no text is set */
- (EHIAttributedStringBuilder *(^)(CGFloat))lineSpacing;
/** Sets paragraph spacing for last added text or defaults if no text is set */
- (EHIAttributedStringBuilder *(^)(CGFloat))paragraphSpacing;
/** Sets minimum line height for last added text or defaults if no text is set */
- (EHIAttributedStringBuilder *(^)(CGFloat))minimumLineHeight;
/** Sets kerning for last added text or defaults if no text is set */
- (EHIAttributedStringBuilder *(^)(CGFloat))kerning;
/** Applies strikethrough for last added text or defaults if no text is set */
- (EHIAttributedStringBuilder *)strikethrough;

/** Convenience method to quickly add a space to the builder */
- (EHIAttributedStringBuilder *)space;
/** Convenience method to quickly add a newline to the builder */
- (EHIAttributedStringBuilder *)newline;
/** Attaches an image to the current position in the string */
- (EHIAttributedStringBuilder *(^)(NSString *))image;

/**
 @brief Generates an attributed string

 An attributed string is constructed from the current state of the builder and returned
 to the caller.
*/

- (NSAttributedString *)string;

@end

#define EHILinkAttributeName   @"EHILinkAttributeName"

@interface NSAttributedString (Construction)

/** Constructs a new string builder */
+ (EHIAttributedStringBuilder *)build;
/** Constructs a string builder for this attributed string */
- (EHIAttributedStringBuilder *)rebuild;

/** Constructs a @c NSAttributedString using the supplied string and font with automatic kerning */
+ (NSAttributedString *)attributedStringWithString:(NSString *)string font:(UIFont *)font;
/** Constructs a @c NSAttributedString using the supplied string, font, and color with automatic kerning */
+ (NSAttributedString *)attributedStringWithString:(NSString *)string font:(UIFont *)font color:(UIColor *)color;
/** Constructs a @c NSAttributedString using the supplied string, font, and color with automatic kerning with a clickHandler */
+ (NSAttributedString *)attributedStringWithString:(NSString *)string font:(UIFont *)font color:(UIColor *)color tapHandler:(void (^)(void))handler;
/** Constructs a @c NSAttributedString by formatting @c items into a bulleted list */
+ (NSAttributedString *)attributedStringListWithItems:(NSArray *)items;
/** Constructs a @c NSAttributedString by formatting @c items into a bulleted list with option to control default formatting */
+ (NSAttributedString *)attributedStringListWithItems:(NSArray *)items formatting:(BOOL)formatting;
/** Constructs a @c NSAttributedString by styling the lines of a title split by '\n' */
+ (NSAttributedString *)attributedSplitLineTitle:(NSString *)title font:(UIFont *)font;

@end
