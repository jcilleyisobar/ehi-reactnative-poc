//
//  NSString+HTML.h
//  Enterprise
//
//  Created by mplace on 3/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSString (HTML)

/**
 @brief  Wrap this string as the body of an html document with font
         set to SourceSansPro-Light.
 @return the string as the body of a font adjusted html document
*/

- (NSString *)ehi_fontAdjustedHtml;

/**
 @brief  removes html tags and character references
 @return the string without html tags or character references
*/

- (NSString *)ehi_stripHtml;

@end
