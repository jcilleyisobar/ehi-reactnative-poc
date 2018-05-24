//
//  EHIOptionalBoolean.h
//  Enterprise
//
//  Created by Marcelo Rodrigues on 11/04/2018.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIFunctions.h"

typedef NS_ENUM(NSUInteger, EHIOptionalBoolean) {
    EHIOptionalBooleanNull,
    EHIOptionalBooleanTrue,
    EHIOptionalBooleanFalse,
};

NS_INLINE NSValueTransformer * EHIOptionalBooleanTransformer()
{
    EHIMapTransformer *transformer = [[EHIMapTransformer alloc] initWithMap:@{
        [NSNull null]         : @(EHIOptionalBooleanNull),
        EHIStringifyFlag(YES) : @(EHIOptionalBooleanTrue),
        EHIStringifyFlag(NO)  : @(EHIOptionalBooleanFalse),
    }];

    transformer.defaultValue = @(EHIOptionalBooleanNull);

    return transformer;
}
