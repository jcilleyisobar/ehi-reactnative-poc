//
//  EHIListDataSourceSection.m
//  Enterprise
//
//  Created by Ty Cobb on 1/21/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIListDataSourceSection_Private.h"

@implementation EHIListDataSourceSection

- (instancetype)init
{
    NSAssert(false, @"Sections are only initializable using -initWithIndex:"); return nil;
}

- (instancetype)initWithIndex:(NSInteger)index
{
    if(self = [super init]) {
        _index    = index;
        _elements = [NSMutableDictionary new];
        _primary  = [EHIListDataSourceElement new];
        _primary.section = self;
    }
    
    return self;
}

# pragma mark - Elements

- (EHIListDataSourceElement *)header
{
    return [self lazyElementOfKind:UICollectionElementKindSectionHeader];
}

- (EHIListDataSourceElement *)footer
{
    return [self lazyElementOfKind:UICollectionElementKindSectionFooter];
}

- (EHIListDataSourceElement *)objectForKeyedSubscript:(NSString *)kind
{
    return [self lazyElementOfKind:kind];
}

//
// Helpers
//

- (EHIListDataSourceElement *)lazyElementOfKind:(NSString *)kind
{
    EHIListDataSourceElement *element = self.elements[kind];
    
    if(!element) {
        element = [[EHIListDataSourceElement alloc] initWithKind:kind];
        element.section = self;
        self.elements[kind] = element;
    }

    return element;
}

- (NSMutableDictionary *)elements
{
    if(!_elements) {
        _elements = [NSMutableDictionary new];
    }
    
    return _elements;
}

# pragma mark - EHIListDataSourceElementDelegate

- (void)didInvalidateElement:(EHIListDataSourceElement *)element
{
    [self.delegate section:self didInvalidateElement:element];
}

# pragma mark - EHIListCollectionViewDataSource

- (void)setKlass:(Class<EHIListCell>)klass
{
    self.primary.klass = klass;
}

- (Class<EHIListCell>)klass
{
    return self.primary.klass;
}

- (BOOL)isDynamicallySized
{
    return self.primary.isDynamicallySized;
}

- (void)setIsDynamicallySized:(BOOL)isDynamicallySized
{
    self.primary.isDynamicallySized = isDynamicallySized;
}

- (NSArray *)models
{
    return self.primary.models;
}

- (void)setModels:(NSArray *)models
{
    self.primary.models = models;
}

- (void)setMetrics:(EHILayoutMetrics *)metrics
{
    self.primary.metrics = metrics;
}

- (EHILayoutMetrics *)metrics
{
    return self.primary.metrics;
}

- (void)setModel:(id)model
{
    self.primary.model = model;
}

- (id)model
{
    return self.primary.model;
}

@end
