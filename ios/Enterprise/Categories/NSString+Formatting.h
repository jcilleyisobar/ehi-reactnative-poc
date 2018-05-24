 //
//  NSString+Formatting.h
//  Enterprise
//
//  Created by Ty Cobb on 2/18/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@interface NSString (Formatting)

/** Generates a string by repeating the receiver @c count number of times */
- (NSString *)ehi_repeat:(NSInteger)count;

/** Masks a string by replacing characters with '•'. Optionally masks last character.  */
- (NSString *)ehi_securedText:(BOOL)showLast;

/** Masks a string by only keeping the last @c n characters and replacing the rest with '*' */
- (NSString *)ehi_maskLast:(NSInteger)n;

/** Masks a string by only keeping the last @c n characters and replacing the rest with @c mask */
- (NSString *)ehi_maskLast:(NSInteger)n mask:(NSString *)mask;

/** Splits the receiver into @c size chunks. A @c nil @c separator defaults to a space separator. */
- (NSString *)ehi_split:(NSUInteger)size separator:(NSString *)separator;

/** Creates a new string consisting of only the existing decimal characters */
- (NSString *)ehi_stripNonDecimalCharacters;

/** Safely substring the last @c chars of a string */
- (NSString *)ehi_last:(NSInteger)count;

/** Safely substring the first @c chars of a string */
- (NSString *)ehi_first:(NSInteger)count;

/** Remove spaces and newline and returns a new string with the result */
- (NSString *)ehi_trim;

/** 
 @brief Generates a new string by from by replacing entries in the @c map

 The map should be a dictionary of replacement keys -> replaced values. The values
 will have @c -description called on them, so they needn't necessarily be strings.
 
 The keys should be undelimited, this method will apply the correct delimiter.
 
 @param map The replacment key -> value map
 
 @return The string formatted with the replacement map
*/

- (NSString *)ehi_applyReplacementMap:(NSDictionary *)map;

/**
 @brief Creates a new string by appending the component
 
 This is a pass-through to @c -ehi_appendComponent:joiner: with @c joiner as @c nil. See
 that method for complete documentation.
*/

- (NSString *)ehi_appendComponent:(NSString *)component;

/**
 @brief Creates a new string by appending the @c component
 
 If the @c component is non-zero in @c length, then the string is unchanged. Otherwise, the
 @c component is added to the result, and if the @c joiner is non-zero in @c length it is 
 also added to the result.
 
 @param component The component to add to the string.
 @param joiner    The string to append after the @c component if it's non-zero
 
 @return The resultant string after appending
*/

- (NSString *)ehi_appendComponent:(NSString *)component joinedBy:(NSString *)joiner;

@end
