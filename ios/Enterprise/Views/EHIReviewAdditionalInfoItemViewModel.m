//
//  EHIReviewAdditionalInfoItemViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 8/2/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReviewAdditionalInfoItemViewModel.h"
#import "EHIReservationBuilder.h"

@implementation EHIReviewAdditionalInfoItemViewModel

- (instancetype)initWithAdditionalInfo:(EHIContractAdditionalInfo *)info
{
    if(self = [super init]) {
        [self buildWithAdditionalInfo:info];
    }
    
    return self;
}

//
// Helpers
//

- (void)buildWithAdditionalInfo:(EHIContractAdditionalInfo *)info
{
    _title = [self nameForAdditionalInfo:info];
    _value = [self valueForAdditionalInfo:info];
}

- (NSAttributedString *)nameForAdditionalInfo:(EHIContractAdditionalInfo *)info
{
    NSString *name          = info.name ?: @"";
    EHIAttributedStringBuilder *builder = EHIAttributedStringBuilder.new;
    builder.appendText(name);
    
    if(!info.isRequired) {
        NSString *optional = EHILocalizedString(@"additional_info_header_optional", @"Optional", @"");
        builder.space.appendText(optional);
    }
    
    return builder.appendText(@":").string;
}

- (NSAttributedString *)valueForAdditionalInfo:(EHIContractAdditionalInfo *)info
{
    EHIContractAdditionalInfoValue *infoValue = [self.builder additionalInfoForKey:info.uid];
    
    EHIAttributedStringBuilder *builder = EHIAttributedStringBuilder.new;
    NSString *value = infoValue.value;
    
    if(value && value.length > 0) {
        builder.appendText(value).fontStyle(EHIFontStyleLight, self.fontSize).color([UIColor blackColor]);
    } else {
        NSString *notProvided = EHILocalizedString(@"additional_info_not_provided", @"Not provided", @"");
        builder.appendText(notProvided).fontStyle(EHIFontStyleItalic, self.fontSize).color([UIColor ehi_grayColor3]);
    }
    
    return builder.string;
}

- (CGFloat)fontSize
{
    return 17.0f;
}

# pragma mark - Passthrough

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

@end
