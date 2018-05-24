//
//  EHIListCollectionViewMassAssigner.m
//  Enterprise
//
//  Created by Ty Cobb on 2/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIListCollectionViewMassAssigner.h"

@interface EHIListCollectionViewMassAssigner ()
@property (weak, nonatomic) id<EHIListCollectionViewSectionAdapter> sections;
@property (copy, nonatomic) NSString *kind;
@end

@implementation EHIListCollectionViewMassAssigner

- (instancetype)initWithElementKind:(NSString *)kind adapter:(id<EHIListCollectionViewSectionAdapter>)adapter
{
    if(self = [super init]) {
        _kind = kind;
        _sections = adapter;
    }
    
    return self;
}

# pragma mark - Setters

- (void)setModel:(id)model
{
    [self apply:^(id<EHIListDataSource> dataSource) {
        dataSource.model = model;
    }];
}

- (void)setModels:(NSArray *)models
{
    [self apply:^(id<EHIListDataSource> dataSource) {
        dataSource.models = models;
    }];
}

- (void)setKlass:(Class<EHIListCell>)klass
{
    [self apply:^(id<EHIListDataSource> dataSource) {
        dataSource.klass = klass;
    }];
}

- (void)setIsDynamicallySized:(BOOL)isDynamicallySized
{
    [self apply:^(id<EHIListDataSource> dataSource) {
        dataSource.isDynamicallySized = isDynamicallySized;
    }];
}

- (void)setMetrics:(EHILayoutMetrics *)metrics
{
    [self apply:^(id<EHIListDataSource> dataSource) {
        dataSource.metrics = metrics;
    }];
}

//
// Helpers
//

- (void)apply:(void(^)(id<EHIListDataSource> dataSource))block
{
    for(EHIListDataSourceSection *section in self.sections) {
        id<EHIListDataSource> dataSource = [self dataSourceForSection:section];
        block(dataSource);
    }
}

- (id<EHIListDataSource>)dataSourceForSection:(EHIListDataSourceSection *)section
{
    return self.kind ? section[self.kind] : section;
}

# pragma mark - Getters

- (id)model
{
    return nil;
}

- (NSArray *)models
{
    return nil;
}

- (Class<EHIListCell>)klass
{
    return nil;
}

- (BOOL)isDynamicallySized
{
    return NO;
}

- (EHILayoutMetrics *)metrics
{
    return nil;
}

@end
