//
//  EHIClickableLabel.m
//  Enterprise
//
//  Created by cgross on 1/22/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIClickableLabel.h"

@interface EHIClickableLabel ()
@property (strong, nonatomic) NSLayoutManager *layoutManager;
@property (strong, nonatomic) NSTextContainer *textContainer;
@property (strong, nonatomic) NSTextStorage *textStorage;
@end

@implementation EHIClickableLabel

- (instancetype)initWithFrame:(CGRect)frame
{
    if(self = [super initWithFrame:frame]) {
        [self addTapGesture];
    }
    
    return self;
}

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        [self addTapGesture];
    }
    
    return self;
}

- (void)addTapGesture
{
    self.userInteractionEnabled = YES;
    
    UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleTap:)];
    [self addGestureRecognizer:tapGesture];
}

- (void)setAttributedText:(NSAttributedString *)attributedText
{
    [super setAttributedText:attributedText];
    
    if (attributedText) {
        [self.textStorage setAttributedString:attributedText];
    }
}

- (void)updateTextContainer
{
    CGSize size = self.bounds.size;
    size.width = MIN(size.width, self.preferredMaxLayoutWidth);
    size.height = CGFLOAT_MAX;
    self.textContainer.size = size;
}

- (void)setPreferredMaxLayoutWidth:(CGFloat)preferredMaxLayoutWidth
{
    [super setPreferredMaxLayoutWidth:preferredMaxLayoutWidth];
    
    [self updateTextContainer];
}

# pragma mark - handle Tap

- (void)handleTap:(UITapGestureRecognizer *)recognizer
{
    CGPoint locationOfTouchInLabel = [recognizer locationInView:self];
    NSInteger indexOfCharacter = [self stringIndexAtLocation:locationOfTouchInLabel];
    
    if (indexOfCharacter == NSNotFound) {
        return;
    }
    
    NSDictionary *attributesAtSelection = [self.attributedText attributesAtIndex:indexOfCharacter effectiveRange:nil];
    [attributesAtSelection enumerateKeysAndObjectsUsingBlock:^(NSString *key, id obj, BOOL *stop) {
        if ([key isEqualToString:EHILinkAttributeName]) {
            void (^handler)(void) = obj;
            ehi_call(handler)();
        }
    }];
}


#pragma mark - Text Container / Storage / Manager

- (NSTextStorage *)textStorage
{
    if (!_textStorage)
    {
        _textStorage = [[NSTextStorage alloc] init];
        [_textStorage addLayoutManager:self.layoutManager];
        [self.layoutManager setTextStorage:_textStorage];
    }
    
    return _textStorage;
}

- (NSTextContainer *)textContainer
{
    if (!_textContainer)
    {
        _textContainer = [[NSTextContainer alloc] init];
        _textContainer.lineFragmentPadding = 0;
        _textContainer.maximumNumberOfLines = self.numberOfLines;
        _textContainer.lineBreakMode = self.lineBreakMode;
        _textContainer.widthTracksTextView = YES;
        
        [self updateTextContainer];
        
        [_textContainer setLayoutManager:self.layoutManager];
    }
    
    return _textContainer;
}

- (NSLayoutManager *)layoutManager
{
    if (!_layoutManager)
    {
        // Create a layout manager for rendering
        _layoutManager = [[NSLayoutManager alloc] init];
        [_layoutManager addTextContainer:self.textContainer];
    }
    
    return _layoutManager;
}

#pragma mark - Helpers

- (NSInteger)stringIndexAtLocation:(CGPoint)location
{
    if (self.textStorage.string.length == 0)
    {
        return NSNotFound;
    }
    
    NSRange glyphRange = [self.layoutManager glyphRangeForTextContainer:self.textContainer];
    CGPoint textOffset = [self textOffsetForGlyphRange:glyphRange];
    
    location.x -= textOffset.x;
    location.y -= textOffset.y;
    
    NSUInteger glyphIndex = [self.layoutManager glyphIndexForPoint:location
                                                   inTextContainer:self.textContainer fractionOfDistanceThroughGlyph:NULL];
    
    return [self.layoutManager characterIndexForGlyphAtIndex:glyphIndex];
}

- (CGPoint)textOffsetForGlyphRange:(NSRange)glyphRange
{
    CGPoint textOffset = CGPointZero;
    
    CGRect textBounds = [self.layoutManager boundingRectForGlyphRange:glyphRange
                                                      inTextContainer:self.textContainer];
    CGFloat paddingHeight = (self.bounds.size.height - textBounds.size.height) / 2.0f;
    if (paddingHeight != 0)
    {
        textOffset.y = paddingHeight;
    }
    
    return textOffset;
}

- (void)drawTextInRect:(CGRect)rect
{
    [self.layoutManager drawGlyphsForGlyphRange:NSMakeRange(0, self.textStorage.length) atPoint:CGPointMake(0, 0)];
}

@end
