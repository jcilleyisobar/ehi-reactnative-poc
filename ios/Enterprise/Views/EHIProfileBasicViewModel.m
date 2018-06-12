//
//  EHIProfileBasicViewModel.m
//  Enterprise
//
//  Created by fhu on 5/18/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIProfileBasicViewModel.h"
#import "EHIProfileItem.h"
#import "EHIPhone.h"

@implementation EHIProfileBasicViewModel

- (void)updateWithModel:(EHIProfileItem *)model
{
    [super updateWithModel:model];
    
    if ([model isKindOfClass:[EHIProfileItem class]]) {
        self.title = model.title;
        self.attributedText = [self attributedStringForModel:model];
    }
}

- (NSAttributedString *)attributedStringForModel:(EHIProfileItem *)model
{
    switch (model.type) {
        case EHIProfileCellTypePhone:
            return [self attributedStringForPhoneNumbers:model.data];
        default:
            return [self attributedStringForDefault:model.data];
    }
}

- (NSAttributedString *)attributedStringForDefault:(NSString *)data
{
    return EHIAttributedStringBuilder.new.text(data).fontStyle(EHIFontStyleLight, 18.0f)
        .lineSpacing(3.0f).string;
}

- (NSAttributedString *)attributedStringForPhoneNumbers:(NSArray<EHIPhone> *)phoneNumbers
{
    EHIAttributedStringBuilder *builder = EHIAttributedStringBuilder.new;

    phoneNumbers.each(^(EHIPhone *phoneNumber, int index) {
        builder.appendText(phoneNumber.maskedNumber).fontStyle(EHIFontStyleLight, 18.0f).lineSpacing(9.0f)
            .appendFormat(@" (%@)", phoneNumber.typeTitle).fontStyle(EHIFontStyleLight, 13.0f).lineSpacing(9.0f);
        if (index < phoneNumbers.count - 1) {
            builder.appendText(@"\n");
        }
    });
    return builder.string;
}

@end
