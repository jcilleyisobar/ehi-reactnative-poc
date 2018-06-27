//
//  EHITextView.m
//  Enterprise
//
//  Created by Alex Koller on 7/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITextView.h"

@implementation EHITextView

# pragma mark - Text

- (void)setText:(NSString *)text
{
    NSInteger cursorPosition = self.cursorPosition;
    NSInteger lengthDelta    = text.length - self.text.length;
    
    // this is probably a cut, so don't move the cursor
    if(lengthDelta < -1) {
        lengthDelta = 0;
    }
    
    self.attributedText = [NSAttributedString attributedStringWithString:text font:self.font color:self.textColor];
    self.cursorPosition = cursorPosition + lengthDelta;
}

# pragma mark - Cursor

- (NSInteger)cursorPosition
{
    return [self offsetFromPosition:self.beginningOfDocument toPosition:self.selectedTextRange.start];
}

- (void)setCursorPosition:(NSInteger)position
{
    UITextPosition *start = [self positionFromPosition:self.beginningOfDocument offset:position];
    UITextPosition *end   = [self positionFromPosition:start offset:0];
    [self setSelectedTextRange:[self textRangeFromPosition:start toPosition:end]];
}

@end
