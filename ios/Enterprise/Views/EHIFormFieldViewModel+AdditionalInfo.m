//
//  EHIFormFieldAdditionalInfoViewModel.m
//  Enterprise
//
//  Created by fhu on 6/9/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldViewModel+AdditionalInfo.h"
#import "EHIFormFieldDateViewModel.h"
#import "EHIFormFieldDropdownViewModel.h"
#import "EHIFormFieldTextViewModel.h"

@implementation EHIFormFieldViewModel (Generator)

+ (EHIFormFieldViewModel *)viewModelForCorporateCodeInfo:(EHIContractAdditionalInfo *)info value:(NSString *)value
{
    EHIFormFieldViewModel *field = [self viewModelHelperForInfo:info];
    field.uid             = info.uid;
    field.attributedTitle = [self formattedTitleForInfo:info];
    field.inputValue      = [self inputValueForField:field fromAdditionalInfoValue:value];
    [field validates:EHIFormFieldValidationNotEmpty];
    
    if(info.type == EHIContractAdditionalInfoTypeText) {
        field.subtitle = info.placeholder;
    }
    
    return field;
}

//
// Helpers
//

+ (EHIFormFieldViewModel *)viewModelHelperForInfo:(EHIContractAdditionalInfo *)info
{
    switch(info.type) {
        case EHIContractAdditionalInfoTypeUnknown:
        case EHIContractAdditionalInfoTypeExactValue:
        case EHIContractAdditionalInfoTypePattern:
        case EHIContractAdditionalInfoTypeText:
            return [EHIFormFieldTextViewModel new];
        case EHIContractAdditionalInfoTypeDate:
            return [EHIFormFieldDateViewModel new];
        case EHIContractAdditionalInfoTypeDropdownList:
            return [[EHIFormFieldDropdownViewModel alloc] initWithModel:info.options];
    }
}

+ (NSAttributedString *)formattedTitleForInfo:(EHIContractAdditionalInfo *)info
{
    NSString *suffix = [NSString string];
    NSString *title  = info.name;

    if(info.isRequired) {
        title = [info.name stringByAppendingString:@" *"];
    } else {
        suffix = EHILocalizedString(@"additional_info_header_optional", @"Optional", @"'optional' text that informs user if an additional information is optional");
    }

    return EHIAttributedStringBuilder.new.text(title).fontStyle(EHIFontStyleBold, 14)
        .space.appendText(suffix).size(14.0).string;
}

+ (id)inputValueForField:(EHIFormFieldViewModel *)field fromAdditionalInfoValue:(NSString *)value
{
    BOOL isDateViewModel     = [field isKindOfClass:[EHIFormFieldDateViewModel class]];
    BOOL convertibleToString = [value respondsToSelector:@selector(ehi_dateTime)];
    if(isDateViewModel && convertibleToString) {
        return [value ehi_dateTime];
    }
    
    return value;
}

@end
