//
//  NSDate+MaskingTests.m
//  Enterprise
//
//  Created by Rafael Machado on 29/09/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "NSDate+MaskingTests.h"

@implementation NSDate (MaskingTests)

+ (NSString *)ehi_localizedMaskedDate:(NSString *)maskedDate usingLocale:(NSLocale *)locale
{
    if(!maskedDate || !locale) {
        return nil;
    }
    
    NSString *template = [NSDateFormatter dateFormatFromTemplate:@"yyyy-MM-dd" options:0 locale:locale];
    NSString *day      = [maskedDate ehi_stripNonDecimalCharacters];
    
    NSString *result = template.lowercaseString;
    result = [result stringByReplacingOccurrencesOfString:@"dd" withString:day];
    result = [result stringByReplacingOccurrencesOfString:@"m" withString:EHIMaskString];
    result = [result stringByReplacingOccurrencesOfString:@"y" withString:EHIMaskString];
    
    return result;
}

@end
