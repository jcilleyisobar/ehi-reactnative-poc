//
//  EHICorporateAccountInfoDropdownValue.m
//  Enterprise
//
//  Created by fhu on 6/12/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIContractAdditionalInfoDropdownValue.h"

@implementation EHIContractAdditionalInfoDropdownValue

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHIContractAdditionalInfoDropdownValue *)model
{
    return @{
        @"display_text" : @key(model.displayText),
    };
}

@end