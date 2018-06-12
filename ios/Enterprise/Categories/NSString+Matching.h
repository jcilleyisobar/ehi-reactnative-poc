//
//  NSString+Matching.h
//  Enterprise
//
//  Created by Ty Cobb on 1/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@import Foundation;

extern NSString * const EHIMaskString;
extern NSString * const EHISectionSignString;
@interface NSString (Matching)

// Matching
- (NSString *)firstStringMatchingRegex:(NSString *)regex;
- (NSArray *)stringsMatchingRegex:(NSString *)regex;
- (NSArray *)textCheckingResultsForRegex:(NSString *)regex;

- (BOOL)matchesRegex:(NSString *)regex;
- (BOOL)matchesCharacterSet:(NSCharacterSet *)characterSet;

- (BOOL)ehi_validEmail;
- (BOOL)ehi_isPhoneNumber;
- (BOOL)ehi_isPossiblePhoneNumber;
- (BOOL)ehi_isMasked;

// Replacement
- (NSString *)stringByReplacingMatchesForRegex:(NSString *)regex withTemplate:(NSString *)replacement;

- (BOOL)ehi_isEqualToStringIgnoringCase:(NSString *)string;

@end
