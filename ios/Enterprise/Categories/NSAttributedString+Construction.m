//
//  NSAttributedString+Construction.m
//  Enterprise
//
//  Created by Ty Cobb on 2/27/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "NSAttributedString+Construction.h"

@interface EHIAttributedStringBuilder ()
@property (strong, nonatomic) NSMutableAttributedString *result;
@property (strong, nonatomic) NSDictionary *sharedAttributes;
@property (assign, nonatomic) NSRange workingRange;
@property (strong, nonatomic) NSDictionary *workingAttributes;
@end

@implementation EHIAttributedStringBuilder

- (instancetype)init
{
    if(self = [super init]) {
        _result = [NSMutableAttributedString new];
        [self invalidateWorkingRange:nil];
    };
    
    return self;
}

# pragma mark - Setting Text

- (instancetype)initWithString:(NSAttributedString *)string
{
    NSDictionary *attributes = [string attributesAtIndex:0 effectiveRange:NULL];
    NSAssert(attributes, @"couldn't find any attributes on string: %@", string);

    if(self = [super init]) {
        _result = [string mutableCopy];
        _sharedAttributes = attributes;
        [self invalidateWorkingRange:string.string];
    }
    
    return self;
}

- (EHIAttributedStringBuilder *(^)(NSString *))text
{
    return ^(NSString *text) {
        // generate the replacement string
        self.result = [[NSMutableAttributedString alloc] initWithString:text attributes:self.sharedAttributes];
        [self invalidateWorkingRange:text];
        
        return self;
    };
}

# pragma mark - Appending

- (EHIAttributedStringBuilder *(^)(NSAttributedString *))append
{
    return ^(NSAttributedString *string) {
        // add shared attributes to incoming string
        NSMutableAttributedString *result = [string mutableCopy];
        [result addAttributes:self.sharedAttributes range:(NSRange){ .length = string.length }];
        
        return [self appendAttributedText:result modifiesRange:YES];
    };
}

- (EHIAttributedStringBuilder *(^)(NSString *))appendText
{
    return ^(NSString *text) {
        return [self appendText:text];
    };
}

- (EHIAttributedStringBuilder *(^)(NSString *, ...))appendFormat
{
    return ^(NSString *format, ...) {
        NSParameterAssert(format);
        
        va_list args;
        va_start(args, format);
        NSString *result = [[NSString alloc] initWithFormat:format arguments:args];
        va_end(args);
        
        return [self appendText:result];
    };
}

- (EHIAttributedStringBuilder *(^)(NSString *, NSAttributedString *))replace
{
    return ^(NSString *string, NSAttributedString *newString) {
        NSMutableAttributedString *result = [newString mutableCopy];
        [result addAttributes:self.sharedAttributes range:(NSRange){ .length = newString.length }];
        
        return [self replaceString:string withAttributedText:result modifiesRange:YES];
    };
}

//
// Convenenience
//

- (EHIAttributedStringBuilder *)space
{
    return [self appendText:@" " modifiesRange:NO];
}

- (EHIAttributedStringBuilder *)newline
{
    return [self appendText:@"\n" modifiesRange:NO];
}

//
// Helpers
//

- (EHIAttributedStringBuilder *)appendText:(NSString *)text
{
    // by default, appending text should modify the working range
    return [self appendText:text modifiesRange:YES];
}

- (EHIAttributedStringBuilder *)appendText:(NSString *)text modifiesRange:(BOOL)modifiesRange
{
    NSAttributedString *string = [[NSAttributedString alloc] initWithString:text ?: @"" attributes:self.sharedAttributes];
    return [self appendAttributedText:string modifiesRange:modifiesRange];
}

- (EHIAttributedStringBuilder *)appendAttributedText:(NSAttributedString *)text modifiesRange:(BOOL)modifiesRange
{
    if(!text) {
        text = [[NSAttributedString alloc] initWithString:@"" attributes:self.sharedAttributes];
    }
    
    [self.result appendAttributedString:text];
    
    if(modifiesRange) {
        [self invalidateWorkingRange:text.string];
    }
    
    return self;
}

- (EHIAttributedStringBuilder *)replaceString:(NSString *)string withAttributedText:(NSAttributedString *)text modifiesRange:(BOOL)modifiesRange
{
    NSRange range = [self.result.string rangeOfString:string];
    if (NSRangeIsNull(range)) {
        return self;
    }

    [self.result replaceCharactersInRange:range withAttributedString:text];
    
    if(modifiesRange) {
        [self invalidateWorkingRange:self.result.string];
    }
    
    return self;
}

# pragma mark - Applying Attributes

- (EHIAttributedStringBuilder *(^)(NSRange))range
{
    return ^(NSRange range) {
        self.workingRange = range;
        return self;
    };
}

- (EHIAttributedStringBuilder *(^)(NSDictionary *))attributes
{
    return ^(NSDictionary *attributes) {
        NSParameterAssert(attributes);
        return [self addAttributes:attributes];
    };
}

- (EHIAttributedStringBuilder *(^)(EHIFontStyle, CGFloat))fontStyle
{
    return ^(EHIFontStyle fontStyle, CGFloat fontSize) {
        UIFont *font = [UIFont ehi_fontWithStyle:fontStyle size:fontSize];
        return [self updateFont:font];
    };
}

- (EHIAttributedStringBuilder *(^)(UIFont *))font
{
    return ^(UIFont *font) {
        return [self updateFont:font];
    };
}

- (EHIAttributedStringBuilder *(^)(CGFloat))size
{
    return ^(CGFloat size) {
        UIFont *font = [UIFont ehi_fontWithStyle:EHIFontStyleLight size:size];
        return [self updateFont:font];
    };
}

- (EHIAttributedStringBuilder *(^)(UIColor *))color
{
    return ^(UIColor *color) {
        NSParameterAssert(color);
        
        return [self addAttributes:@{
            NSForegroundColorAttributeName : color
        }];
    };
}

- (EHIAttributedStringBuilder *(^)(CGFloat, CGFloat))lineSpacingAndHeadIndent
{
    return ^(CGFloat lineSpacing, CGFloat headIndent) {
        // set paragraph style for attributed string
        NSMutableParagraphStyle *style = [NSMutableParagraphStyle new];
        style.lineSpacing = lineSpacing;
        style.headIndent  = headIndent;
        
        return [self addAttributes:@{
            NSParagraphStyleAttributeName : style
        }];
    };
}

- (EHIAttributedStringBuilder *(^)(CGFloat, NSTextAlignment))paragraph
{
    return ^(CGFloat spacing, NSTextAlignment alignment) {
        // set paragraph style for attributed string
        NSMutableParagraphStyle *style = [NSMutableParagraphStyle new];
        style.lineSpacing = spacing;
        style.alignment   = alignment;
        
        return [self addAttributes:@{
            NSParagraphStyleAttributeName : style
        }];
    };
}

- (EHIAttributedStringBuilder *(^)(CGFloat))headIndent
{
    return ^(CGFloat headIndent) {
        // set paragraph style for attributed string
        NSMutableParagraphStyle *style = [NSMutableParagraphStyle new];
        style.headIndent = headIndent;
        
        return [self addAttributes:@{
            NSParagraphStyleAttributeName : style
        }];
    };
}

- (EHIAttributedStringBuilder *(^)(CGFloat))lineSpacing
{
    return ^(CGFloat spacing) {
        // set paragraph style for attributed string
        NSMutableParagraphStyle *style = [NSMutableParagraphStyle new];
        style.lineSpacing = spacing;
        
        return [self addAttributes:@{
            NSParagraphStyleAttributeName : style
        }];
    };
}

- (EHIAttributedStringBuilder *(^)(CGFloat))paragraphSpacing
{
    return ^(CGFloat spacing) {
        // set paragraph style for attributed string
        NSMutableParagraphStyle *style = [NSMutableParagraphStyle new];
        style.paragraphSpacing = spacing;
        
        return [self addAttributes:@{
            NSParagraphStyleAttributeName : style
        }];
    };
}

- (EHIAttributedStringBuilder *(^)(NSParagraphStyle *))paragraphStyle
{
    return ^(NSParagraphStyle *paragraph) {
        return [self addAttributes:@{
             NSParagraphStyleAttributeName : paragraph
        }];
    };
}

- (EHIAttributedStringBuilder *(^)(CGFloat))minimumLineHeight
{
    return ^(CGFloat minimumLineHeight) {
        // set paragraph style for attributed string
        NSMutableParagraphStyle *style = [NSMutableParagraphStyle new];
        style.minimumLineHeight = minimumLineHeight;
        
        return [self addAttributes:@{
            NSParagraphStyleAttributeName : style
        }];
    };
}

- (EHIAttributedStringBuilder *(^)(CGFloat))kerning
{
    return ^(CGFloat kerning) {
        return [self addAttributes:@{
            NSKernAttributeName : @(kerning)
        }];
    };
}

- (EHIAttributedStringBuilder *)strikethrough
{
    return [self addAttributes:@{
        NSStrikethroughStyleAttributeName : [NSNumber numberWithInteger:NSUnderlineStyleSingle]
    }];
}

- (EHIAttributedStringBuilder *(^)(NSString *))image
{
    return ^(NSString *imageName) {
        UIFont *currentFont = self.workingAttributes[NSFontAttributeName];
        
        NSTextAttachment *attachment = [NSTextAttachment new];
        attachment.image  = [UIImage imageNamed:imageName];
        attachment.bounds = (CGRect){
            .origin.y = currentFont.descender,
            .size     = attachment.image.size
        };
       
        NSAttributedString *attachmentString = [NSAttributedString attributedStringWithAttachment:attachment];
        [self.result appendAttributedString:attachmentString];
        
        return self;
    };
}

//
// Helpers
//

- (EHIAttributedStringBuilder *)updateFont:(UIFont *)font
{
    [self addAttributes:@{
        NSFontAttributeName : font,
        NSKernAttributeName : [UIFont ehi_kerningForFont:font]
    }];
    
    return self;
}

- (EHIAttributedStringBuilder *)addAttributes:(NSDictionary *)attributes
{
    if(NSRangeIsNull(self.workingRange)) {
        self.sharedAttributes = self.sharedAttributes.extend(attributes);
    } else {
        [self.result addAttributes:attributes range:self.workingRange];
    }
    
    return self;
}

- (void)invalidateWorkingRange:(NSString *)newText
{
    // provided we have text, set working range to location/length of last added text
    self.workingRange = !newText ? NSRangeNull : (NSRange) {
        .location = self.result.length - newText.length,
        .length = newText.length
    };
}

# pragma mark - Accessors

- (NSDictionary *)sharedAttributes
{
    if(!_sharedAttributes) {
        _sharedAttributes = [NSDictionary new];
    }
    
    return _sharedAttributes;
}

- (NSDictionary *)workingAttributes
{
    NSRange range = self.workingRange;
    return [self.result attributesAtIndex:range.location effectiveRange:&range];
}

# pragma mark - String Retrieval

- (NSAttributedString *)string
{
    return [self.result copy];
}

@end

@implementation NSAttributedString (Construction)

+ (EHIAttributedStringBuilder *)build
{
    return [EHIAttributedStringBuilder new];
}

- (EHIAttributedStringBuilder *)rebuild
{
    return [[EHIAttributedStringBuilder alloc] initWithString:self];
}

+ (NSAttributedString *)attributedStringWithString:(NSString *)string font:(UIFont *)font
{
    return [self attributedStringWithString:string font:font color:nil];
}

+ (NSAttributedString *)attributedStringWithString:(NSString *)string font:(UIFont *)font color:(UIColor *)color
{
    return [self attributedStringWithString:string font:font color:color tapHandler:nil];
}

+ (NSAttributedString *)attributedStringWithString:(NSString *)string font:(UIFont *)font color:(UIColor *)color tapHandler:(void (^)(void))handler
{
    if(!string) {
        return nil;
    }
 
    // font and kerning are required attribtues
    NSDictionary *attributes = @{
        NSFontAttributeName : font,
        NSKernAttributeName : [UIFont ehi_kerningForFont:font],
    };
 
    // optionally append the color
    attributes = [attributes ehi_appendKey:NSForegroundColorAttributeName value:color];
    
    // optionally append clickHandler
    attributes = [attributes ehi_appendKey:EHILinkAttributeName value:handler];
   
    return [[NSAttributedString alloc] initWithString:string attributes:attributes];
}

+ (NSAttributedString *)attributedStringListWithItems:(NSArray *)items
{
    return [self attributedStringListWithItems:items level:1 formatting:YES];
}

+ (NSAttributedString *)attributedStringListWithItems:(NSArray *)items formatting:(BOOL)formatting
{
    return [self attributedStringListWithItems:items level:1 formatting:formatting];
}

+ (NSAttributedString *)attributedStringListWithItems:(NSArray *)items level:(float)level formatting:(BOOL)formatting
{
    NSString *tab = @"   ";
    const CGFloat tabSpacing = 20.0f;
    
    CGFloat lineSpacing = 0;
    EHIAttributedStringBuilder *builder;
    if (formatting) {
        builder = EHIAttributedStringBuilder.new.size(18);
        lineSpacing = 8;
    } else {
        builder = EHIAttributedStringBuilder.new;
    }
    
    items.each(^(id item, int index) {
        if ([item isKindOfClass:[NSArray class]]) {
            builder.newline.appendText([NSString stringWithFormat:@"%@%@", tab, @"  "   ]).lineSpacingAndHeadIndent(lineSpacing, tabSpacing * (level + 1)).append([self attributedStringListWithItems:item level:level + 1 formatting:NO]);
        } else {
            NSString *bullet = [NSString stringWithFormat:@"•%@", tab];
            if(index == 0) {
                builder.text(bullet).lineSpacingAndHeadIndent(lineSpacing, tabSpacing);
            } else {
                builder.newline.appendText(bullet).lineSpacingAndHeadIndent(lineSpacing, tabSpacing);
            }
                
            // style bullet point and append item text
            builder.size(14).color([UIColor ehi_silverColor])
                .space.appendText(item);
        }
    });
    
    return builder.string;
}

+ (NSAttributedString *)attributedSplitLineTitle:(NSString *)title font:(UIFont *)font
{
    EHIAttributedStringBuilder *builder = EHIAttributedStringBuilder.new.font(font);
    NSArray *titleComponents = [title componentsSeparatedByString:@"\n"];
   
    if(titleComponents.count <= 1) {
        return builder.text(title).string;
    } else {
        NSString *firstLine  = titleComponents[0];
        NSString *secondLine = titleComponents[1];
        
        return builder.text(firstLine).newline
            .appendText(secondLine).color([UIColor ehi_greenColor]).string;
    }
}

@end
