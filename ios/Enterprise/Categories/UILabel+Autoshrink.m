//
//  UILabel+Autoshrink.m
//  Enterprise
//
//  Created by cgross on 9/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "UILabel+Autoshrink.h"

@implementation UILabel (Autoshrink)

- (void)shrinkTextIfNeeded:(NSString *)text rect:(CGRect)rect
{
    if (!text) {
        return;
    }
    
    // cache line break character set
    static NSCharacterSet *lineBreakCharacters;
    if(!lineBreakCharacters) {
        NSMutableCharacterSet *workingSet = [[NSCharacterSet whitespaceAndNewlineCharacterSet] mutableCopy];
        [workingSet addCharactersInString:@"—–-"];
        lineBreakCharacters = [workingSet copy];
    }
    
    // search for line break characters
    BOOL isOneWord = [text rangeOfCharacterFromSet:lineBreakCharacters].location == NSNotFound;
    
    // shrinking when one word
    self.numberOfLines             = isOneWord ? 1 : 0;
    self.adjustsFontSizeToFitWidth = isOneWord;
    self.minimumScaleFactor        = 0.8;
    
    if (!isOneWord) {
        // find longest word and check if it fits without wrapping, otherwise reduce font size
        NSString *longestWord = [text componentsSeparatedByCharactersInSet:lineBreakCharacters].max(^(NSString *word) {
            return word.length;
        });
        
        CGRect textRect = [longestWord boundingRectWithSize:CGSizeMake(MAXFLOAT, MAXFLOAT)
                                                    options:NSStringDrawingUsesLineFragmentOrigin
                                                 attributes:@{
                                                              NSFontAttributeName : self.font,
                                                              NSKernAttributeName : @(-0.5),
                                                              }
                                                    context:nil];
                
        if (textRect.size.width > rect.size.width) {
//            EHIDomainInfo(EHILogDomainGeneral, @"Shrink Text: %@", text);
            self.font = [self.font fontWithSize:self.font.pointSize - 2];
        }
    }
}

@end
