//
//  EHIAnalyticsContext+Serialization.m
//  Enterprise
//
//  Created by Ty Cobb on 6/1/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIAnalyticsContext_Private.h"

NS_ASSUME_NONNULL_BEGIN

#define EHIAnalyticsContextPathSeparator @":"

@implementation EHIAnalyticsContext (Serialization)

- (nullable NSString *)path
{
    NSArray *components = @[
        self.screen ?: @"",
        self.state  ?: @"",
        self.actionTypeString ? [self.actionTypeString stringByAppendingString:self.action ?: @""] : @""
    ];
    
    // generate the path, provided there's something to generate from
    NSString *path = components.ehi_compressJoin(EHIAnalyticsContextPathSeparator);
    if(!path.length) {
        path = nil;
    }
    
    return path;
}

- (NSDictionary *)dictionaryRepresentation
{
    // build up the list of attributes in order of ascending precedence: shared -> standard -> temporary
    NSDictionary *attributes = self.sharedAttributes
        .extend(self.attributes)
        .extend(self.temporaryAttributes);
   
    return [self.class serializeAttributes:attributes];
}

+ (NSDictionary *)serializeAttributes:(NSDictionary *)attributes
{
    NSMutableDictionary *result = [[NSMutableDictionary alloc] initWithCapacity:attributes.count];
    
     // and write them into the result, calling description on each value
    for(id<NSCopying> key in attributes) {
        result[key] = [self serializeValue:attributes[key]];
    }
    
    return [result copy];
}

NSString * ehi_serializeActions(NSString *action, ...)
{
    va_list args;
    va_start(args, action);
   
    NSMutableArray *actions = [[NSMutableArray alloc] initWithObjects:action, nil];
    while((action = va_arg(args, NSString *)) != nil) {
        [actions addObject:action];
    }
    
    NSString *result = actions.join(EHIAnalyticsContextPathSeparator);
    
    va_end(args);
    
    return result;
}

//
// Helpers
//

- (NSDictionary *)sharedAttributes
{
    NSMutableDictionary *result = [NSMutableDictionary new];
    
    // add the screen information
    [result setValue:self.path forKey:EHIAnalyticsCurrentScreenKey];
    [result setValue:self.previousPath forKey:EHIAnalyticsPreviousScreenKey];
    [result setValue:self.screenKey forKey:EHIAnalyticsScreenUrlKey];
    
    // add the action information
    [result setValue:self.action forKey:EHIAnalyticsActionNameKey];
    [result setValue:[self stringFromActionType:self.actionType] forKey:EHIAnalyticsActionTypeKey];
    
    return [result copy];
}

+ (id)serializeValue:(id)value
{
    id result = nil;
 
    // if this is an array, join the valid components
    if([value isKindOfClass:[NSArray class]]) {
        NSArray *list = value;
        if(list.count) {
            result = list.ehi_compressJoin(@", ");
        }
    }
    // if this is an NSNumber, we'll try and apply some custom serialization
    else if([value isKindOfClass:[NSNumber class]]) {
        NSNumber *number = value;
        if([number ehi_isBooleanLike]) {
            result = number.boolValue ? EHIAnalyticsTrueValue : EHIAnalyticsFalseValue;
        }
    }
    
    // otherwise, return the description
    if(!result) {
        result = [value description];
    }
    
    return result;
}

@end

NS_ASSUME_NONNULL_END
