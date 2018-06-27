//
//  EHIReservationFeeViewModel.m
//  Enterprise
//
//  Created by Ty Cobb on 4/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationFeeViewModel.h"
#import "EHICarClassPriceLineItem.h"
#import "EHIServices+Config.h"

@interface EHIReservationFeeViewModel ()
@property (strong, nonatomic) EHICarClassPriceLineItem *lineItem;
@property (copy  , nonatomic) NSString *title;
@property (copy  , nonatomic) NSString *priceText;
@property (copy  , nonatomic) NSAttributedString *detailsText;
@end

@implementation EHIReservationFeeViewModel

- (void)updateWithModel:(EHICarClassPriceLineItem *)lineItem
{
    [super updateWithModel:lineItem];
    
    self.lineItem = lineItem;
   
    // if this is a real line item, use its data
    if(!lineItem.isPlaceholder) {
        self.title     = lineItem.formattedTitle;
        self.priceText = lineItem.formattedTotal;
    }
    // otherwise, if this is a placeholder add the learn more text
    else {
        self.title = EHILocalizedString(@"class_details_fees_learn_more", @"Learn more about these charges", @"Title for the class details 'Learn More' fees link");
        [[EHIServices sharedInstance] fetchContentForType:EHIWebContentTypeTaxes handler:^(EHIWebContent *content, EHIServicesError *error) {
            self.detailsText = [self attributedStringFromHtml:content.body];
        }];
    }
}

//
// Helpers
//

- (NSAttributedString *)attributedStringFromHtml:(NSString *)html
{
    if(!html) {
        return nil;
    }
    
    // storage for out parameters
    NSError *error; NSDictionary *attributes;
    
    //remove double spacing
    html = [html stringByReplacingOccurrencesOfString:@"<br>\n&nbsp;<br>" withString:@"<br>"];
   
    // generate the attributed string from the raw html
    NSData *data = [html dataUsingEncoding:NSUTF8StringEncoding];
    NSMutableAttributedString *result = [[NSMutableAttributedString alloc] initWithData:data options:@{
        NSDocumentTypeDocumentAttribute      : NSHTMLTextDocumentType,
        NSCharacterEncodingDocumentAttribute : @(NSUTF8StringEncoding)
    } documentAttributes:&attributes error:&error];
    
    if(error) {
        return nil;
    }
    
    return [self formatAttributedString:result];
}

- (NSAttributedString *)formatAttributedString:(NSMutableAttributedString *)string
{
    NSRange range = (NSRange){0,[string length]};
    [string enumerateAttribute:NSFontAttributeName inRange:range options:NSAttributedStringEnumerationLongestEffectiveRangeNotRequired usingBlock:^(id value, NSRange range, BOOL *stop) {
        UIFont* currentFont = value;
        UIFont *replacementFont = nil;
        
        if ([currentFont.fontName rangeOfString:@"bold" options:NSCaseInsensitiveSearch].location != NSNotFound) {
            replacementFont = [UIFont ehi_fontWithStyle:EHIFontStyleBold size:16.0f];
        } else {
            replacementFont = [UIFont ehi_fontWithStyle:EHIFontStyleLight size:14.0f];
        }
        
        [string addAttribute:NSFontAttributeName value:replacementFont range:range];
    }];
    return string;
}

# pragma mark - Learn More

+ (EHIReservationFeeViewModel *)learnMoreViewModel
{
    return [[self alloc] initWithModel:[EHIModel placeholder]];
}

- (BOOL)isLearnMore
{
    return self.lineItem.isPlaceholder;
}

# pragma mark - MTRReactive

+ (NSArray *)nonreactiveProperties:(EHIReservationFeeViewModel *)object
{
    return @[
        @key(object.lineItem),
    ];
}

@end
