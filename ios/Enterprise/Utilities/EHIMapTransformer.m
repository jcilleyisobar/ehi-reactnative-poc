//
//  EHIMapTransformer.m
//  Enterprise
//
//  Created by Ty Cobb on 3/9/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIMapTransformer.h"

@interface EHIMapTransformer ()
@property (copy, nonatomic) NSDictionary *forwardMap;
@property (copy, nonatomic) NSDictionary *reverseMap;
@end

@implementation EHIMapTransformer

- (instancetype)initWithMap:(NSDictionary *)map
{
    if(self = [super init]) {
        _forwardMap = map;
        _reverseMap = map.ehi_reverse;
    }
    
    return self;
}

# pragma mark - NSValueTransformer

- (id)transformedValue:(id)value
{
    return self.forwardMap[value] ?: self.defaultValue;
}

- (id)reverseTransformedValue:(id)value
{
    return self.reverseMap[value] ?: self.reverseDefaultValue;
}

+ (BOOL)allowsReverseTransformation
{
    return YES;
}

@end
