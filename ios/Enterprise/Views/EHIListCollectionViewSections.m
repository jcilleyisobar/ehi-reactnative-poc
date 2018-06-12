//
//  EHIListCollectionViewSections.m
//  Enterprise
//
//  Created by Ty Cobb on 2/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIListCollectionViewSections_Private.h"
#import "EHIListCollectionViewMassAssigner.h"

@interface EHIListCollectionViewSections ()
@property (strong, nonatomic) NSMutableDictionary *sectionMap;
@property (strong, nonatomic) EHIListCollectionViewMassAssigner *sectionAssigner;
@end

@implementation EHIListCollectionViewSections

- (instancetype)init
{
    if(self = [super init]) {
        _sectionMap = [NSMutableDictionary new];
        _sectionAssigner = [[EHIListCollectionViewMassAssigner alloc] initWithElementKind:nil adapter:self];
    }
    
    return self;
}

# pragma mark - Section Creation

- (EHIListDataSourceSection *)objectAtIndexedSubscript:(NSInteger)index
{
    EHIListDataSourceSection *section = self.sectionMap[@(index)];
    
    if(!section) {
        section = [[EHIListDataSourceSection alloc] initWithIndex:index];
        section.delegate = self.sectionDelegate;
        self.sectionMap[@(index)] = section;
    }
    
    return section;
}

- (void)construct:(NSDictionary *)klassMap
{
    for(NSNumber *section in klassMap) {
        self[section.integerValue].klass = klassMap[section];
    }
}

# pragma mark - Section Desctruction

- (void)removeSection:(NSInteger)index
{
    [self.sectionMap removeObjectForKey:@(index)];
}

- (void)reset
{
    [self.sectionMap removeAllObjects];
}

# pragma mark - Mass Assignment

- (id)forwardingTargetForSelector:(SEL)selector
{
    // forward mass-assignment for the primary element to our stored assigner
    if([self.sectionAssigner respondsToSelector:selector]) {
        return self.sectionAssigner;
    }
    
    return [super forwardingTargetForSelector:selector];
}

- (id<EHIListDataSource>)header
{
    return [self assignerWithKind:UICollectionElementKindSectionHeader];
}

- (id<EHIListDataSource>)footer
{
    return [self assignerWithKind:UICollectionElementKindSectionFooter];
}

- (id<EHIListDataSource>)objectForKeyedSubscript:(NSString *)kind
{
    return [self assignerWithKind:kind];
}

- (EHIListCollectionViewMassAssigner *)assignerWithKind:(NSString *)kind
{
    return [[EHIListCollectionViewMassAssigner alloc] initWithElementKind:kind adapter:self];
}

# pragma mark - Accessors

- (NSInteger)count
{
    if(!self.sectionMap.count) {
        return 0;
    }
    
    NSInteger sections = 0;
    for(EHIListDataSourceSection *section in self.sectionMap.allValues) {
        sections = MAX(sections, section.index + 1);
    }
    
    return sections;
}

- (EHIListDataSourceSection *)sectionForIndexPath:(NSIndexPath *)indexPath
{
    return self.sectionMap[@(indexPath.section)];
}

# pragma mark - NSFastEnumeration

- (NSUInteger)countByEnumeratingWithState:(NSFastEnumerationState *)state objects:(__unsafe_unretained id [])buffer count:(NSUInteger)len
{
    return [self.sectionMap.allValues countByEnumeratingWithState:state objects:buffer count:len];
}

@end
