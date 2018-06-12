//
//  EHISurveySession.m
//  Enterprise
//
//  Created by Rafael Ramos on 12/9/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHISurveySession.h"
#import "EHISurveyKeys.h"

@interface EHISurveySession ()
@property (strong, nonatomic) NSMutableDictionary *storage;
@end

@implementation EHISurveySession

- (instancetype)init
{
    if(self = [super init]) {
        _storage = [NSMutableDictionary new];
    }
    
    return self;
}

- (NSDictionary *)decodeSession
{
    return self.storage.copy;
}

# pragma mark - Subscripting

- (id)objectForKeyedSubscript:(id)key
{
    return self.storage[key];
}

- (void)setObject:(id)obj forKeyedSubscript:(NSString *)key
{
    if(obj && key) {
        [self.storage setObject:[self parseObject:obj] forKeyedSubscript:key];
    }
}

//
// Helpers
//

- (NSString *)parseObject:(id)object
{
    id result = nil;
    
    if([object isKindOfClass:[NSArray class]]) {
        NSArray *list = object;
        if(list.count) {
            result = list.ehi_compressJoin(@", ");
        }
    }
    
    if([object isKindOfClass:[NSNumber class]]) {
        NSNumber *number = object;
        if([number ehi_isBooleanLike]) {
            result = [number boolValue] ? EHISurveyTrueValue : EHISurveyFalseValue;
        }
    }
    
    if(!result) {
        result = [object description];
    }

    return result;
}


@end
