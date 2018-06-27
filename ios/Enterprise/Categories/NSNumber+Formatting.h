//
//  NSNumber+Formatting.h
//  Enterprise
//
//  Created by mplace on 5/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSNumber (Formatting)

/** Returns a localized number string for interface display */
- (NSString *)ehi_localizedDecimalString;

/**
 @brief @c YES if this is number wraps a boolean
 
 This method may return false positives, but shouldn't return false negatives 
*/

- (BOOL)ehi_isBooleanLike;

/**
 @brief Negates the bool value of the receiver
 
 This method returns an @c NSNumber with the negated bool value of the receiver
 */

- (NSNumber *)ehi_negateBool;

@end
