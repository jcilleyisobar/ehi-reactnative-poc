//
//  UIFont+EHIFont.m
//  Enterprise
//
//  Created by Ty Cobb on 1/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "UIFont+EHIFont.h"

#define EHIFontExtraLight           @"SourceSansPro-ExtraLight"
#define EHIFontExtraLightItalic     @"SourceSansPro-ExtraLightItalic"
#define EHIFontLight                @"SourceSansPro-Light"
#define EHIFontLightItalic          @"SourceSansPro-LightItalic"
#define EHIFontRegular              @"SourceSansPro-Regular"
#define EHIFontMedium               @"SourceSansPro-SemiBold"
#define EHIFontMediumItalic         @"SourceSansPro-SemiBoldItalic"
#define EHIFontBold                 @"SourceSansPro-Bold"
#define EHIFontBoldItalic           @"SourceSansPro-BoldItalic"
#define EHIFontHeavy                @"SourceSansPro-Black"
#define EHIFontHeavyItalic          @"SourceSansPro-BlackItalic"

@implementation UIFont (EHIFont)

+ (instancetype)ehi_fontWithStyle:(EHIFontStyle)style size:(CGFloat)size
{
    NSString *fontName = [self ehi_fontNameForStyle:style];
    NSAssert(fontName, @"Did not find a custom font name for: %d", (int)style);
    return [self fontWithName:fontName size:size];
}

+ (NSString *)ehi_fontNameForStyle:(EHIFontStyle)style
{
    if(style & EHIFontStyleExtraLight) {
        return style & EHIFontStyleItalic ? EHIFontExtraLightItalic : EHIFontExtraLight;
    }
    else if(style & EHIFontStyleLight) {
        return style & EHIFontStyleItalic ? EHIFontLightItalic : EHIFontLight;
    }
    else if(style & EHIFontStyleMedium) {
        return style & EHIFontStyleItalic ? EHIFontMediumItalic : EHIFontMedium;
    }
    else if(style & EHIFontStyleBold) {
        return style & EHIFontStyleItalic ? EHIFontBoldItalic : EHIFontBold;
    }
    else if(style & EHIFontStyleHeavy) {
        return style & EHIFontStyleItalic ? EHIFontHeavyItalic : EHIFontHeavy;
    }
    else if(style & EHIFontStyleRegular) {
        return EHIFontRegular;
    }
    else if (style & EHIFontStyleItalic) {
        return EHIFontLightItalic;
    }
    
    return nil;
}

+ (NSNumber *)ehi_kerningForFont:(UIFont *)font
{
    CGFloat size = font.pointSize;

    if(size <= 14.0) {
        return @(0.0);
    } else if (size <= 18.0) {
        return @(-0.5f);
    } else if (size <= 24.0) {
        return @(-1.0f);
    } else {
        return @(-1.2f);
    }
}

@end

@implementation UIFont (Debug)

+ (void)ehi_listFonts:(NSArray *)families
{
#ifdef DEBUG
    EHIVerbose(@"* * * * FONT NAMES * * * *");
    
    // list all the families if we don't have a filter
    if(!families) {
        EHIVerbose(@"all available family names:%@", UIFont.familyNames);
    }
    // print the fonts within the families otherwise
    else {
        for(NSString *family in UIFont.familyNames) {
            if(![families containsObject:family]) {
                continue;
            }
            
            EHIVerbose(@"family: %@", family);
            NSArray *fontNames = [UIFont fontNamesForFamilyName:family];
            for(NSString *fontName in fontNames) {
                EHIVerbose(@"\tfont: %@", fontName);
            }
        }
    }
    
    EHIVerbose(@"* * * * END  FONTS * * * *");
#endif
}

@end
