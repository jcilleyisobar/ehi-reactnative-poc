//
//  EHIDealContentViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 08/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIDealContentViewModel.h"

@interface EHIDealContentViewModel ()
@property (copy, nonatomic) NSAttributedString *contentText;
@end

@implementation EHIDealContentViewModel

- (void)updateWithModel:(id)model
{
    if([model isKindOfClass:NSString.class]) {
        [self buildContentWith:model handler:^(NSAttributedString *attributedContent){
            self.contentText = attributedContent;
        }];
    }
}

//
// Helpers
//

- (void)buildContentWith:(NSString *)content handler:(void (^)(NSAttributedString *))handler
{
    if(!content.ehi_isHTML) {
        handler([self attributedContent:content]);
    }
    
    // This uses WebKit under the hood...not calling from the main thread may crash....
    dispatch_async(dispatch_get_main_queue(), ^{
        handler([self attributedHTML:content]);
    });
}

- (NSAttributedString *)attributedContent:(NSString *)content
{
    return EHIAttributedStringBuilder.new
        .appendText(content)
        .fontStyle(EHIFontStyleLight, 18.0f)
        .string;
}

- (NSAttributedString *)attributedHTML:(NSString *)html
{
    NSString *css = @" \
        body, h1, h2, h3, h4, h5, h6 { \
            font-family: 'SourceSansPro-Light'; \
            font-size: 16.00px; \
        } \
        h1, h2, h3, h4, h5, h6 { \
            font-weight: normal; \
        } \
        p { \
            margin: 0; \
        } \
        ul { \
            padding-left: 0; \
        } \
    ";

    NSString *ajustedHTML = [html ehi_adjustedWithCustomStyling:css];
    return [[NSAttributedString alloc] initWithData:[ajustedHTML dataUsingEncoding:NSUnicodeStringEncoding]
                                            options:@{ NSDocumentTypeDocumentAttribute: NSHTMLTextDocumentType }
                                 documentAttributes:nil
                                              error:nil];
}

@end
