//
//  EHIAnalyticsContext.m
//  Enterprise
//
//  Created by Ty Cobb on 5/28/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIAnalyticsContext_Private.h"

NS_ASSUME_NONNULL_BEGIN

@implementation EHIAnalyticsContext

- (instancetype)init
{
    if(self = [super init]) {
        // create the attributes (by default the current attributes)
        _attributes = [NSMutableDictionary new];
        _currentAttributes = _attributes;
    }
    
    return self;
}

# pragma mark - Tags

- (void)setRouterScreen:(NSString *)screen
{
    self.screenKey = screen;
    self.screen    = screen ? [self screenFromRouterScreen:screen] : nil;
}

- (void)setRouterState:(NSString *)screen
{
    self.screenKey = screen;
    // setting the router state is considered a screen change, so it's NOT silent
    [self setState:[self stateFromRouterScreen:screen] silent:NO];
}

- (void)setState:(nullable NSString *)state
{
    [self setState:state silent:YES];
}

- (void)setState:(nullable NSString *)state silent:(BOOL)isSilent
{
    // capture the current path as the previous path
    if(!isSilent) {
        self.previousPath = self.path;
    }
   
    // and then update your state
    _state = state;
    EHIDomainVerbose(EHILogDomainAnalytics, @"state  - %@", self);
}

# pragma mark - Accessors

- (nullable NSString *)actionTypeString
{
    return [self stringFromActionType:self.actionType];
}

# pragma mark - Updates

- (void)encode:(Class<EHIAnalyticsEncodable>)klass encodable:(nullable id<EHIAnalyticsEncodable>)encodable
{
    [self encode:klass encodable:encodable prefix:nil];
}

- (void)encode:(Class<EHIAnalyticsEncodable>)klass encodable:(nullable id<EHIAnalyticsEncodable>)encodable prefix:(nullable NSString *)prefix
{
    [self encodeWithPrefix:prefix handler:^(EHIAnalyticsContext *context) {
        [klass encodeWithContext:self instance:encodable];
    }];
}

- (void)encodeWithPrefix:(nullable NSString *)prefix handler:(EHIAnalyticsContextHandler)handler
{
     // capture the current prefix and set the new one before encoding
    NSString *previousPrefix = self.activePrefix;
    self.activePrefix = prefix;
   
    handler(self);
    
    // restore to the previous prefix
    self.activePrefix = previousPrefix;   
}

- (EHIAnalyticsContext *)clone
{
    EHIAnalyticsContext *clone = EHIAnalyticsContext.new;
    clone.screen        = self.screen;
    clone.state         = self.state;
    clone.action        = self.action;
    clone.actionType    = self.actionType;
    clone.customerValue = self.customerValue;
    clone.macroEvent    = self.macroEvent;
    clone.previousPath  = self.previousPath;
    clone.screenKey     = self.screenKey;

    // private properties
    clone.attributes          = self.attributes.mutableCopy;
    clone.activePrefix        = self.activePrefix;
    clone.temporaryAttributes = self.temporaryAttributes.mutableCopy;
    clone.currentAttributes   = self.currentAttributes.mutableCopy;

    return clone;
}

# pragma mark - Debugging

- (NSString *)description
{
    return [[NSString alloc] initWithFormat:@"<context %p; path = %@; c.v. = %d>", self, self.path, (int)self.customerValue];
}

@end

@implementation EHIAnalyticsContext (Subscripting)

- (void)setObject:(nullable id)object forKeyedSubscript:(NSString *)key
{
    key = [self prefixKey:key];
    
    // if the value is nil, remove it from the attributes
    if(!object) {
        [self.currentAttributes removeObjectForKey:key];
    }
    // otherwise, set this value at the root level
    else {
        self.currentAttributes[key] = object;
    }
}

- (nullable id)objectForKeyedSubscript:(NSString *)key
{
    key = [self prefixKey:key];
    return self.temporaryAttributes[key] ?: self.attributes[key];
}

//
// Helpers
//

- (NSString *)prefixKey:(NSString *)key
{
    return self.activePrefix ? [self.activePrefix stringByAppendingString:key] : key;
}

@end

@implementation EHIAnalyticsContext (Temporary)

- (void)applyTemporaryAttributes:(EHIAnalyticsContextHandler)handler
{
    // create temporary attributes if necessary
    if(!self.temporaryAttributes) {
        self.temporaryAttributes = [NSMutableDictionary new];
    }
   
    // make the temporary attributes the current attributes for the duration of the block
    self.currentAttributes = self.temporaryAttributes;
    handler(self);
    self.currentAttributes = self.attributes;
}

- (void)clearTemporaryAttributes
{
    self.macroEvent = nil;
    self.customerValue = 0;
    
    // remove any attributes
    self.temporaryAttributes = nil;
}

@end

NS_ASSUME_NONNULL_END
