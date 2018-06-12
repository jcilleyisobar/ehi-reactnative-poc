//
//  EHIRequiredInfoFootnoteViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 07/05/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIRequiredInfoFootnoteViewModel.h"

@interface EHIRequiredInfoFootnoteViewModel ()
@property (copy  , nonatomic) NSAttributedString *note;
@property (assign, nonatomic) EHIRequiredInfoFootnoteType type;
@end

@implementation EHIRequiredInfoFootnoteViewModel

+ (instancetype)initWithType:(EHIRequiredInfoFootnoteType)type
{
    EHIRequiredInfoFootnoteViewModel *model = [EHIRequiredInfoFootnoteViewModel new];
    model.type = type;
    model.note = [self noteWithType:type];
    
    return model;
    
}

+ (NSAttributedString *)noteWithType:(EHIRequiredInfoFootnoteType)type
{
    NSString *note = EHILocalizedString(@"gdpr_marketing_optin_lang", @"By selecting this box, you would like to receive email promotions, surveys and offers from Enterprise-Rent-A-Car. Note that your email interactions can be used to perform analytics and produce content & ads tailored to your interests. Please know that you can unsubscribe at any time. Please consult our Privacy Policy and our Cookie Policy to find out more.", @"");

    NSString *sectionSign = type != EHIRequiredInfoFootnoteTypeReservation ? EHISectionSignString : @"";
    
    return EHIAttributedStringBuilder.new
        .appendText(sectionSign)
        .fontStyle(EHIFontStyleBold, 14.0f)
        .space
        .appendText(note)
        .fontStyle(EHIFontStyleLight, 14.0f)
        .string;
}

@end
