//
//  EHIListDataSourceElement.m
//  Enterprise
//
//  Created by Ty Cobb on 1/21/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIListDataSourceElement.h"
#import "EHIListDataSourceSection_Private.h"

@implementation EHIListDataSourceElement

@synthesize
    klass=_klass,
    models=_models,
    metrics=_metrics,
    isDynamicallySized=_isDynamicallySized;

# pragma mark - Initializers

- (instancetype)init
{
    return [self initWithKind:nil];
}

- (instancetype)initWithKind:(NSString *)kind
{
    if(self = [super init]) {
        _kind = kind;
    }
    
    return self;
}

- (void)dealloc
{
    // ensure the referents get cleaned up
    self.klass = nil;
}

# pragma mark - Setters

- (void)setKlass:(Class<EHIListCell>)klass
{
    if(_klass == klass) {
        return;
    }

    // decrement reference counts for old cells, increment for new cells
    [_klass modifyReferents:-1];
    _klass = klass;
    [_klass modifyReferents:1];
   
    // we don't need to invalidate until we actually have data
    if(self.models.count) {
        self.isInvalid = YES;
    }
}

- (void)setModels:(NSArray *)models
{
    NSAssert(!models || [models isKindOfClass:[NSArray class]],
        @"Attempted to call -setModels: with something that's not an array, maybe you meant -setModel:?");
    
    if(_models == models) {
        return;
    }
        
    // record previous models so that we can diff them during invalidation
    self.previousModels = self.models;
    _models = models;
   
    // we don't need to invalidate until we have a class
    if(self.klass) {
        self.isInvalid = YES;
    } else if(models) {
        EHIDomainDebug(EHILogDomainGeneral, @"section: %d kind: %@ attempted to set models without a class", (int)self.section.index, self.kind);
    }
}

- (void)setIsInvalid:(BOOL)isInvalid
{
    if(_isInvalid != isInvalid) {
        // only update when necessary
        _isInvalid = isInvalid;
        
        // notify delegate when we do invalidate
        if(isInvalid) {
            [self.section didInvalidateElement:self];
        }
        // clear out previous models when we're valid again
        else {
            self.previousModels = nil;
        }
    }
}

# pragma mark - Accessors

- (BOOL)isPrimary
{
    return !self.kind;
}

# pragma mark - Computed Properties

- (void)setModel:(id)model
{
    if(model && [model isKindOfClass:[NSArray class]]) {
        EHIDomainDebug(EHILogDomainGeneral, @"attempted to call -setModel: with an array; did you mean -setModels:?");
    }
    
    self.models = model ? @[ model ] : nil;
}

- (id)model
{
    return self.models.firstObject;
}

@end
