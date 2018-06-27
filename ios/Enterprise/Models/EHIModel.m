//
//  EHIModel.m
//  Enterprise
//
//  Created by Ty Cobb on 1/5/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@import ObjectiveC;

#import "EHIModel_Subclass.h"
#import "EHIPlaceholder.h"

@implementation EHIModel

+ (void)initialize
{
    // create a new collection and store it
    EHICollection *collection = [EHICollection new];
    self.collection = collection;
    
    // set the collection name, pull in the mappings
    collection.name = NSStringFromClass(self);
    collection.mappings = [self mappings:nil];
    
    // allow the subclass to register any value transformers
    [self registerTransformers:nil];
    // allow the subclass to perform any custom configuration
    [self prepareCollection:collection];
}

# pragma mark - Prototype

static char *ehi_collectionKey;

- (EHICollection *)collection
{
    return [self.class collection];
}

+ (EHICollection *)collection
{
    return objc_getAssociatedObject(self, ehi_collectionKey);
}

+ (void)setCollection:(EHICollection *)collection
{
    objc_setAssociatedObject(self, ehi_collectionKey, collection, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

+ (void)prepareCollection:(EHICollection *)collection
{
    
}

# pragma mark - Factory initializers

+ (id)modelsWithDictionaries:(id<EHIMappable>)dictionaries
{
    return [dictionaries map:^(NSDictionary *dictionary) {
        return [self modelWithDictionary:dictionary];
    }];
}

+ (instancetype)modelWithDictionary:(NSDictionary *)dictionary
{
    if(!dictionary || dictionary.count == 0) {
        return nil;
    }
    
    Class classToInstantiate = [self subclassToInstantiateForDictionary:dictionary placeholder:nil];
    // recurse into this method for the subclass if one was returned
    if(classToInstantiate != self) {
        return [classToInstantiate modelWithDictionary:dictionary];
    }
    
    return [[self alloc] initWithDictionary:dictionary];
}

# pragma mark - Model lifecycle

- (instancetype)init
{
    return [self initWithDictionary:@{ }];
}

- (instancetype)initWithDictionary:(NSDictionary *)attributes
{
    if(self = [super init]) {
        [self updateWithDictionary:attributes];
    }
    
    return self;
}

- (void)updateWithDictionary:(NSDictionary *)dictionary
{
    [self updateWithDictionary:dictionary forceDeletions:NO];
}

- (void)updateWithDictionary:(NSDictionary *)dictionary forceDeletions:(BOOL)forceDeletions
{
    if(![dictionary isKindOfClass:[NSDictionary class]]) {
        EHIDomainError(EHILogDomainModels, @"attempted to update a model with a non-dictionary value: %@", dictionary);
        return;
    }
   
    NSMutableDictionary *attributes = [self dictionaryByMappingKeysFromDictionary:dictionary];
    [self parseDictionary:attributes];

    if(forceDeletions) {
        [self insertNullsForMissingAttributes:attributes];
    }
    
    for(NSString *key in attributes) {
        id value = attributes[key];

        // allow NSNulls to pass through safely
        if([value isEqual:[NSNull null]]) {
            value = nil;
        }
        
        // check if we're going to delete a value
        id oldValue = [self valueForKey:key];
        BOOL willDeleteValue = oldValue && !value;
        
        if(value || (willDeleteValue && forceDeletions)) {
            // TODO: There's a bug here. If the user modifies the key in parseAttributes:, the sub-dictionary
            // we pull out from the unparsed data will be nil or (worse) something completely different. This
            // should merge the fields of the model itself, but we need to implement a model-model update in
            // order to achieve that.
            
            // if there's an existing model, update it with the raw dictionary value
            if([oldValue isKindOfClass:[EHIModel class]] && value != nil) {
                [oldValue updateWithDictionary:attributes[key] forceDeletions:forceDeletions];
            }
            // if we don't have an existing child, set a new value
            else {
                // apply any transfomers first
                value = [self automaticallyTransformValue:value forKey:key];
                // then try and create a model from this value
                value = [self deserializeModelsFromValue:value forKey:key];
                // finally set the value
                [self setValue:value forKey:key];
            }
        }
    }
}

- (void)setNilValueForKey:(NSString *)key
{
    [self setValue:@(0) forKey:key];
}
                             
- (void)insertNullsForMissingAttributes:(NSMutableDictionary *)attributes
{
    objc_property_t *properties; uint count;
    properties = class_copyPropertyList(self.class, &count);
    
    for(int index=0 ; index<count ; index++) {
        objc_property_t property = properties[index];
        const char *propertyName = property_getName(property);
        NSString *propertyNameObj = [NSString stringWithUTF8String:propertyName];
        
        // mark missing properties with backing ivars for deletion
        if(class_hasInstanceVariableForPropertyName(self.class, propertyName) && !attributes[propertyNameObj]) {
            [attributes setValue:[NSNull null] forKey:propertyNameObj];
        }
    }
    
    free(properties);
}

- (id)automaticallyTransformValue:(id)value forKey:(NSString *)key
{
    return [self.class automaticallyTransformValue:value forKey:key];
}

+ (id)automaticallyTransformValue:(id)value forKey:(NSString *)key
{
    NSValueTransformer *transformer = self.collection.transformers[key];
    
    if(transformer) {
        value = [self transformValueOrValues:value withTransformer:transformer];
    } else if(self == [EHIModel class]) {
        return value;
    }
   
    // traverese class hierarchy
    return [[self superclass] automaticallyTransformValue:value forKey:key];
}

+ (id)transformValueOrValues:(id)valueOrValues withTransformer:(NSValueTransformer *)transformer
{
    // if this is not a mappable type, transform it
    if(![valueOrValues conformsToProtocol:@protocol(EHIMappable)]) {
        return [transformer transformedValue:valueOrValues];
    }
   
    // otherwise, map through and transform each value
    return [valueOrValues map:^(id value) {
        return [transformer transformedValue:value];
    }];
}

- (id)deserializeModelsFromValue:(id)value forKey:(NSString *)key
{
    // determine the model classes to create, if possible
    Class<EHIModel> klass; Class collection;
    klass = [self classToInstantiateForKey:key collection:&collection];
   
    // do nothing if we have no class
    if(!klass) {
        value = value;
    }
    // if we don't have a collection, we'll create a single instance
    else if(!collection) {
        value = [klass modelWithDictionary:value];
    }
    // otherwise, map whatever collection we have into models
    else {
        value = [value map:^(NSDictionary *dictionary) {
            return [klass modelWithDictionary:dictionary];
        }];
    }
    
    return value;
}

- (Class<EHIModel>)classToInstantiateForKey:(NSString *)key collection:(Class *)collection
{
    // if a property for `key` has a type such as NSArray<ModelClass>, this method breaks up
    // the property's type string to return the corresponding classes
    Class result = nil;
    
    // get the property for this key
    objc_property_t property = class_getProperty(self.class, key.UTF8String);
    
    if(property != NULL) {
        // if one exists, we're going to inspect the type to see if we can infer the model class
        // the type is a string in the format: '@"Class<Annotation>"'
        char *type = property_copyAttributeValue(property, "T");
       
        // lop off the initial '@"' string from the type
        char *root = type + 2;
        
        // get the start of the annotation (if any)
        char *annotation = strchr(type, '<');
        
        // determine the class from the annotation
        if(annotation != NULL) {
            // break off the protocol annotation from the root type
            annotation[0] = '\0';
            annotation += 1;
            annotation[strlen(annotation)-2] = '\0';
           
            // create the classes from the type components
            *collection = NSClassFromString(@(root));
            result      = NSClassFromString(@(annotation));
        }
        // otherwise, try and determine the class from the base type
        else {
            // truncate the trailing '"' from the root type
            root[strlen(root)-1] = '\0';
            
            // we don't want to be creating non-model types
            result = NSClassFromString(@(root));
            if(![result isSubclassOfClass:[EHIModel class]]) {
                result = nil;
            }
        }
        
        free(type);
    }

    return result;
}

# pragma mark - Internal Mappings

- (NSMutableDictionary *)dictionaryByMappingKeysFromDictionary:(NSDictionary *)dictionary
{
    NSMutableDictionary *mappedDictionary = [[NSMutableDictionary alloc] initWithCapacity:dictionary.count];
    
    for(NSString *key in dictionary) {
        NSString *mappedKey = [self.class mappedKeyForKey:key];
        id value = dictionary[key];
        
        // filter out null values
        if(value != [NSNull null]) {
            mappedDictionary[mappedKey] = dictionary[key];
        }
    }
    
    return mappedDictionary;
}

+ (NSString *)mappedKeyForKey:(NSString *)key
{
    NSDictionary *mappings = self.collection.mappings;
    
    // if we find a mapping on this class, we're done
    NSString *mappedKey = mappings[key];
    if(mappedKey) {
        return mappedKey;
    }
    // if we hit the bottom of the hierarchy, we didn't find anything
    else if(self == [EHIModel class]) {
        return key;
    }
   
    // otherwise recurse up the tree
    return [[self superclass] mappedKeyForKey:key];
}

# pragma mark - EHINetworkEncodable 

- (void)encodeWithRequest:(EHINetworkRequest *)request
{
    
}

# pragma mark - KVC

- (id)valueForUndefinedKey:(NSString *)key
{
    EHIDomainVerbose(EHILogDomainModels, @"[%@] get undefined key: %@", self, key);
    return nil;
}

- (void)setValue:(id)value forUndefinedKey:(NSString *)key
{
    EHIDomainVerbose(EHILogDomainModels, @"[%@] set undefined key: %@ => %@", self, key, value);
}

# pragma mark - Placeholder

+ (instancetype)placeholder
{
    static EHIPlaceholder *placeholder;
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        placeholder = [EHIPlaceholder new];
    });
    
    return placeholder;
}

- (BOOL)isPlaceholder
{
    return NO;
}

# pragma mark - Equality

- (BOOL)isEqual:(id)object
{
    BOOL isEqual = [super isEqual:object];
   
    // if super failed, see if our ids are the same
    if(!isEqual && [object isKindOfClass:self.class]) {
        isEqual = [self.uid isEqual:[object uid]];
    }
    
    return isEqual;
}

- (NSUInteger)hash
{
    return [self.uid hash];
}

@end

@implementation EHIModel (Parsing)

+ (NSDictionary *)mappings:(id)model
{
    return @{
        @"id" : @"uid",
        @"description" : @"details"
    };
}

+ (Class<EHIModel>)subclassToInstantiateForDictionary:(NSDictionary *)dictionary placeholder:(id)placeholder
{
    return self;
}

- (void)parseDictionary:(NSMutableDictionary *)dictionary
{

}

@end

@implementation EHIModel (Transformers)

+ (void)registerTransformers:(id)model
{

}

+ (void)key:(NSString *)key registerMap:(NSDictionary *)map
{
    [self key:key registerMap:map defaultValue:nil];
}

+ (void)key:(NSString *)key registerMap:(NSDictionary *)map defaultValue:(id)value
{
    EHIMapTransformer *transformer = [[EHIMapTransformer alloc] initWithMap:map];
    transformer.defaultValue = value;
    [self key:key registerTransformer:transformer];
}

+ (void)key:(NSString *)key registerTransformer:(NSValueTransformer *)transformer
{
    NSParameterAssert(transformer);
   
    // lazy load the transformers map
    EHICollection *collection = self.collection;
    if(!collection.transformers) {
        collection.transformers = [NSMutableDictionary new];
    }
    
    collection.transformers[key] = transformer;
}

+ (NSValueTransformer *)transformerForKey:(NSString *)key
{
    return self.collection.transformers[key];
}

@end
