//
//  EHIModel.h
//  Enterprise
//
//  Created by Ty Cobb on 1/5/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIEncodableObject.h"

@import ObjectiveC;

static BOOL ignoreEncodableKeyFilters = NO;

@implementation EHIEncodableObject

# pragma mark - Encodable Key Generation

+ (NSSet *)allKeys
{
    NSSet *allKeys = objc_getAssociatedObject(self, _cmd);
    if(allKeys) {
        return allKeys;
    }
    
    NSMutableSet *result = [NSMutableSet set];
    
    // get this method on our superclass
    Class superClass = class_getSuperclass(self);
    Method method = class_getClassMethod(superClass, _cmd);
    
    // if it has an implementation, get its keys recursively
    if(method != NULL) {
        [result unionSet:[superClass allKeys]];
    }
    
    // add our list of keys
    [result addObjectsFromArray:[self encodableProperties]];
    
    // cache the keys on the class
    NSSet *copy = [result copy];
    objc_setAssociatedObject(self, _cmd, copy, OBJC_ASSOCIATION_RETAIN);
    
    return copy;
}

+ (NSSet *)encodableKeys
{
    NSSet *encodableKeys = objc_getAssociatedObject(self, _cmd);
    if(encodableKeys) {
        return encodableKeys;
    }
    
    NSMutableSet *result = [NSMutableSet set];
   
    // get this method on our superclass
    Class superClass = class_getSuperclass(self);
    Method method = class_getClassMethod(superClass, _cmd);
    
    // if it has an implementation, get its keys recursively
    if(method != NULL) {
        [result unionSet:[superClass encodableKeys]];
    }
 
    // get our list of keys, either from the whitelist or through introspection
    NSArray *whitelist = [self encodableKeys:nil];
    NSArray *localKeys = whitelist.count ? whitelist : [self encodableProperties];
    [result addObjectsFromArray:localKeys];
 
    // remove any keys that should be unencodable
    [result minusSet:[NSSet setWithArray:[self unencodableKeys:nil]]];
    
    // cache the keys on the class
    NSSet *copy = [result copy];
    objc_setAssociatedObject(self, _cmd, copy, OBJC_ASSOCIATION_RETAIN);
    
    return copy;
}

+ (NSArray *)encodableProperties
{
    NSMutableArray *result = [NSMutableArray new];
    
    unsigned int count = 0;
    objc_property_t *properties = class_copyPropertyList(self, &count);

    // for each property
    for(int i = 0; i < count; i++) {
        objc_property_t property = properties[i];
        const char *name = property_getName(property);
        
        // check that the property is not weak and has storage
        BOOL isWeak = property_hasAttribute(property, "W");
        BOOL isEncodable = !isWeak && class_hasInstanceVariableForPropertyName(self, name);
        
        // if its neither, we'll encode it
        if(isEncodable) {
            [result addObject:@(name)];
        }
    }
    
    free(properties);
    
    return [result copy];
}

//
// Helpers
//

BOOL property_hasAttribute(objc_property_t property, char *attribute)
{
    // check that the property value is non-zero
    char *value = property_copyAttributeValue(property, attribute);
    BOOL hasValue = value != NULL && strlen(value) != 0;
   
    // clean up and return
    free(value);
    return hasValue;
}

BOOL class_hasInstanceVariableForPropertyName(Class klass, const char *name)
{
    size_t length = strlen(name) + 2;
    char ivarName[length];
    
    // add the leading underscore
    ivarName[0] = '_';
    ivarName[1] = '\0';
    
    // concat the property name
    strcat(ivarName, name);
    
    Ivar ivar = class_getInstanceVariable(klass, ivarName);
    return ivar != NULL;
}

# pragma mark - Subclassing Hooks

+ (NSArray *)encodableKeys:(id)object
{
    return @[ ];
}

+ (NSArray *)unencodableKeys:(id)placeholder
{
    return @[ ];
}

# pragma mark - Description

- (NSString *)description
{
    return [self recursiveDescriptionWithDepth:NSIntegerMax];
}

- (NSString *)recursiveDescriptionWithDepth:(NSInteger)depth
{
    NSDictionary *dictionaryRep = [NSDictionary dictionaryWithEncodableObject:self recurseDepth:depth];
    return [[super description] stringByAppendingString:[dictionaryRep description]];
}

# pragma mark - NSCoding

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [self init]) {
        NSSet *properties = ignoreEncodableKeyFilters ? [self.class allKeys] : [self.class encodableKeys];
        for(NSString *key in properties) {
            if([decoder containsValueForKey:key]) {
                id value = [decoder decodeObjectForKey:key];
                [self setValue:value forKey:key];
            }
        }
    }
    
    return self;
}

- (void)encodeWithCoder:(NSCoder *)coder
{
    NSSet *properties = ignoreEncodableKeyFilters ? [self.class allKeys] : [self.class encodableKeys];
    for(NSString *key in properties) {
        [coder encodeObject:[self valueForKey:key] forKey:key];
    }
}

# pragma mark - NSSecureCoding

+ (BOOL)supportsSecureCoding
{
    return NO; // soon...
}

# pragma mark - Copying

- (id)deepCopy
{
    return [self deepCopy:NO];
}

- (id)deepCopy:(BOOL)ignoreKeyFilters
{
    ignoreEncodableKeyFilters = ignoreKeyFilters;
    id copy = [NSKeyedUnarchiver unarchiveObjectWithData:[NSKeyedArchiver archivedDataWithRootObject:self]];
    ignoreEncodableKeyFilters = NO;
    
    return copy;
}

# pragma mark - NSCopying

- (id)copyWithZone:(NSZone *)zone
{
    id copy = [self.class new];
    
    NSSet *properties = ignoreEncodableKeyFilters ? [self.class allKeys] : [self.class encodableKeys];
    for(NSString *key in properties) {
        [copy setValue:[self valueForKey:key] forKey:key];
    }
    
    return copy;
}

# pragma mark - QuickLook

- (id)debugQuickLookObject
{
    return [self debugDescription];
}

@end


@implementation NSDictionary (EHIEncodableObject)

+ (NSDictionary *)dictionaryWithEncodableObject:(EHIEncodableObject *)object
{
    return [NSDictionary dictionaryWithEncodableObject:object recurseDepth:NSIntegerMax];
}

+ (NSDictionary *)dictionaryWithEncodableObject:(EHIEncodableObject *)object recurseDepth:(NSInteger)depth
{
    NSMutableDictionary *result = [NSMutableDictionary dictionary];
    NSSet *propertyNames = [object.class encodableKeys];
    
    NSInteger childDepth = depth;
    for(NSString *key in propertyNames) {
        id value = [object valueForKey:key];
        
        if([value isKindOfClass:[EHIEncodableObject class]] && --childDepth >= 0) {
            value = [NSDictionary dictionaryWithEncodableObject:value];
        }
        
        [result setValue:value forKey:key];
    }
    
    return [result copy];
}

@end
