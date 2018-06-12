//
//  NSString+HTML.m
//  Enterprise
//
//  Created by mplace on 3/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "NSString+HTML.h"

@implementation NSString (HTML)

- (NSString *)ehi_fontAdjustedHtml
{
    return [[NSString alloc] initWithFormat:@" \
        <html> \
            <head> \
                <style type='text/css'> \
                    body { \
                        font-family: SourceSansPro-Light \
                    } \
                </style> \
            </head> \
            <body>%@</body> \
        </html>",
    self];
}

- (NSString *)ehi_stripHtml
{
    NSString *result = self;
   
    result = [result ehi_stripHTMLFromString];
    result = [result ehi_stripHtmlCharacterReferences];
    result = [result stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
    
    return result;
}

- (NSString *)ehi_stripHtmlCharacterReferences
{
    NSString *result = self;
    
    result = [result stringByReplacingOccurrencesOfString:@"&nbsp;" withString:@" "];
    result = [result stringByReplacingOccurrencesOfString:@"&quot;" withString:@"\""];
    result = [result stringByReplacingOccurrencesOfString:@"&lt;" withString:@"<"];
    result = [result stringByReplacingOccurrencesOfString:@"&gt;" withString:@">"];
    result = [result stringByReplacingOccurrencesOfString:@"&amp;" withString:@"&"];
    
    return result;
}

- (NSString *)ehi_stripHTMLFromString
{
    NSString *result = self;
    
    NSRange range;
    while((range = [result rangeOfString:@"<[^>]+>" options:NSRegularExpressionSearch]).location != NSNotFound) {
        result = [result stringByReplacingCharactersInRange:range withString:@""];
    }
    
    return result;
}

@end
