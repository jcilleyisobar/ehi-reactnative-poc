//
//  EHICorporateAdditionalInfo.m
//  Enterprise
//
//  Created by fhu on 6/8/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIContractAdditionalInfo.h"
#import "EHIModel_Subclass.h"

@implementation EHIContractAdditionalInfo

# pragma mark - Accessor

- (NSArray *)options
{
    return self.supportedValues.sortBy(^(EHIContractAdditionalInfoDropdownValue *dropdownValue) {
        return dropdownValue.value;
    }).map(^(EHIContractAdditionalInfoDropdownValue *dropdownValue) {
        return dropdownValue.displayText;
    });
}

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHIContractAdditionalInfo *)model
{
    return @{
        @"display_on_splash"        : @key(model.shouldDisplayOnSplash),
        @"supported_values"         : @key(model.supportedValues),
        @"helper_text"              : @key(model.placeholder),
        @"validate_additional_info" : @key(model.isPreRate)
    };
}

+ (void)registerTransformers:(EHIContractAdditionalInfo *)model
{
    // register a mapping for the extra status
    [self key:@key(model.type) registerMap:@{
        @"DATE"        : @(EHIContractAdditionalInfoTypeDate),
        @"DROPDOWN"    : @(EHIContractAdditionalInfoTypeDropdownList),
        @"EXACT VALUE" : @(EHIContractAdditionalInfoTypeExactValue),
        @"PATTERN"     : @(EHIContractAdditionalInfoTypePattern),
        @"TEXT"        : @(EHIContractAdditionalInfoTypeText),
    } defaultValue:@(EHIContractAdditionalInfoTypeUnknown)];
}

@end
