//
//  EHIConfirmationAdditionalInfoViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 7/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIConfirmationAdditionalInfoViewModel.h"
#import "EHIContractAdditionalInfoValue.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIConfirmationAdditionalInfoViewModel ()
@property (strong, nonatomic) EHIContractAdditionalInfoValue *additionalInfo;
@end

@implementation EHIConfirmationAdditionalInfoViewModel

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHIContractAdditionalInfoValue class]]) {
        [self updateWithAdditionalInfo:model];
    }
}

- (void)updateWithAdditionalInfo:(EHIContractAdditionalInfoValue *)info
{
    self.additionalInfo = info;
    
    self.title = EHILocalizedString(@"reservation_confirmation_assistance_section_title", @"ADDITIONAL INFORMATION", @"");
    self.name  = self.formattedTitle;
    self.value = self.formattedValue;
    self.isLastInSection = info.isLastInSection;
    self.shouldShowSectionTitle = info.shouldShowSectionTitle;
}

//
// Helpers
//

- (NSString *)formattedTitle
{
    NSString *name = self.additionalInfo.name ?: @"";
    EHIAttributedStringBuilder *builder = EHIAttributedStringBuilder.new;
    builder.appendText(name);
    
    BOOL isRequired = self.additionalInfo.isRequired;
    if(!isRequired) {
        NSString *optional = EHILocalizedString(@"additional_info_header_optional", @"Optional", @"");
        builder.space.appendText(optional);
    }
    
    return builder.appendText(@":").string.string;
}

- (NSAttributedString *)formattedValue
{
    NSString *value = self.additionalInfo.value;
    
    // convert dates from services format to friendly representation
    if(self.additionalInfo.type == EHIContractAdditionalInfoTypeDate) {
        value = [[value ehi_dateTime] ehi_localizedShortDateString];
    }
    
    EHIAttributedStringBuilder *builder = EHIAttributedStringBuilder.new;
    
    CGFloat fontSize = 15.0f;
    if(value && value.length > 0) {
        builder.appendText(value).fontStyle(EHIFontStyleLight, fontSize).color([UIColor blackColor]);
    } else {
        NSString *notProvided = EHILocalizedString(@"additional_info_not_provided", @"Not provided", @"");
        builder.appendText(notProvided).fontStyle(EHIFontStyleItalic, fontSize).color([UIColor ehi_grayColor3]);
    }
    
    return builder.string;
}

@end

NS_ASSUME_NONNULL_END