//
//  NSDate+MaskingTests.h
//  Enterprise
//
//  Created by Rafael Machado on 29/09/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

@interface NSDate (MaskingTests)

/** Returns a localized date using the given masked date string, based on @c locale */
+ (NSString *)ehi_localizedMaskedDate:(NSString *)maskedDate usingLocale:(NSLocale *)locale;

@end
