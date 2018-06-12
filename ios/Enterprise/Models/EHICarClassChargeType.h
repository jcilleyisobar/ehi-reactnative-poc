//
//  EHICarClassChargeType.h
//  Enterprise
//
//  Created by Michael Place on 5/29/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIMapTransformer.h"

typedef NS_ENUM(NSInteger, EHICarClassChargeType) {
    EHICarClassChargeTypeUnknown,
    EHICarClassChargeTypePrepay,
    EHICarClassChargeTypePayLater,
    EHICarClassChargeTypeContract,
    EHICarClassChargeTypePromotion,
    EHICarClassChargeTypeRetail,
};


NS_INLINE NSValueTransformer * EHICarClassChargeTypeTransformer()
{
    // apply the service key -> enum mapping
    EHIMapTransformer *transformer = [[EHIMapTransformer alloc] initWithMap:@{
        @"PREPAY"    : @(EHICarClassChargeTypePrepay),
        @"PAYLATER"  : @(EHICarClassChargeTypePayLater),
        @"CONTRACT"  : @(EHICarClassChargeTypeContract),
        @"PROMOTION" : @(EHICarClassChargeTypePromotion),
        @"RETAIL"    : @(EHICarClassChargeTypeRetail),
    }];
    
    // default to unknown
    transformer.defaultValue = @(EHICarClassChargeTypeUnknown);
    
    return transformer;
}