//
//  EHIListCollectionView.m
//  Enterprise
//
//  Created by Ty Cobb on 1/21/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIListCollectionView.h"
#import "EHIListCollectionViewDelegateTrampoline.h"
#import "EHIListCollectionViewSections_Private.h"
#import "EHIListMergeFacilitator.h"
#import "EHIRefreshControlCell.h"

@interface EHIListCollectionView () <EHIListDataSourceSectionDelegate, UICollectionViewDataSource>
@property (strong, nonatomic) EHIListCollectionViewDelegateTrampoline *trampoline;
@property (strong, nonatomic) EHIListCollectionViewSections *sections;
@property (strong, nonatomic) NSMutableSet *invalidElements;
@property (strong, nonatomic) NSMutableSet *registeredClasses;
@property (strong, nonatomic) NSMutableArray *afterFlushHandlers;
@property (assign, nonatomic) BOOL isBatchingUpdates;
@property (assign, nonatomic) BOOL isFlushing;
@property (assign, nonatomic) BOOL didScheduleFlush;
@property (nonatomic, readonly) id<EHIListCollectionViewDelegate> delegate;
@end

@implementation EHIListCollectionView

- (instancetype)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        _sections = [EHIListCollectionViewSections new];
        _sections.sectionDelegate = self;
        
        _invalidElements = [NSMutableSet new];
        _afterFlushHandlers = [NSMutableArray new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    // we'll be our own data source and delegate
    self.dataSource = self;
    self.trampoline = [EHIListCollectionViewDelegateTrampoline new];
}

# pragma mark - Update Batching

- (void)performAnimated:(BOOL)animated batchUpdates:(void (^)(void))updates completion:(void (^)(BOOL))completion
{
    NSAssert(updates, @"Calling performAnimated: without updates is meaningless");

    // if animated, run performBatchUpdates normally
    if(animated) {
        [self performBatchUpdates:updates completion:completion];
    }
    // otherwise, just run the updates in-line
    else {
        updates();
        ehi_call(completion)(YES);
    }
}

# pragma mark - Reuse

- (void)prepareForReuse
{
    [self.sections reset];
    [self reloadData];
}

# pragma mark - UICollectionView

- (void)performBatchUpdates:(void (^)(void))updates completion:(void (^)(BOOL))completion
{
    // ensure any existing invalidated elements are flushed
    [self flushForced:NO completion:^{
        // make sure to flush out any reactions that resolving those elements created, or our
        // updates block won't run synchronously
        [[MTRReactor reactor] clearFlush:^{
            // prepare to perform the updates
            self.isBatchingUpdates = YES;
           
            // and kick them off
            [super performBatchUpdates:^{
                // aggregate the updates
                ehi_call(updates)();
                
                // resolve the updates
                [self flushForced:NO completion:nil];
                [self setIsBatchingUpdates:NO];
            } completion:^(BOOL finished) {
                ehi_call(completion)(finished);
                
                if([self.delegate respondsToSelector:@selector(collectionViewDidReload:)]) {
                    [self.delegate collectionViewDidReload:self];
                }
            }];

            [UIView animateWithDuration:0.3 animations:^{
                for(UICollectionViewCell *cell in self.visibleCells) {
                    [cell.contentView layoutIfNeeded];
                }
            }];
       }];
    }];
}

# pragma mark - UICollectionViewDataSource

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView
{
    return self.sections.count;
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    EHIListDataSourceElement *element = self.sections[section].primary;
    // verify that we both have models and a cell type
    return element.models.count && element.klass ? element.models.count : 0;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    // determine the section and cell base type to dequeue
    EHIListDataSourceSection *section = [self.sections sectionForIndexPath:indexPath];
    Class<EHIListCell> klass = section.klass;
    
    NSAssert(klass, @"Didn't have a cell class for %@", indexPath);
   
    // attempt to dequeue the cell
    id model = section.models[indexPath.item];
    EHICollectionViewCell *cell =
        [collectionView ehi_dequeueReusableCellWithClass:klass metrics:section.metrics model:model atIndexPath:indexPath];

    // let the cell know if its the last in its section
    cell.isLastInSection = indexPath.item == section.models.count - 1;
    // capture the selected state properly
    cell.selected = [collectionView.indexPathsForSelectedItems containsObject:indexPath];
    
    // notify our delegate if necessary
    if([self.delegate respondsToSelector:@selector(collectionView:didDequeueCell:atIndexPath:)]) {
        [self.delegate collectionView:self didDequeueCell:cell atIndexPath:indexPath];
    }
    
    // hand the refresh control to the trampoline
    if(indexPath.section == self.refreshControlSection) {
        self.trampoline.refreshControl = model;
    }
    
    return cell;
}

- (UICollectionReusableView *)collectionView:(UICollectionView *)collectionView viewForSupplementaryElementOfKind:(NSString *)kind atIndexPath:(NSIndexPath *)indexPath
{
    // determine the section and cell base type to dequeue
    EHIListDataSourceSection *section = [self.sections sectionForIndexPath:indexPath];
    EHIListDataSourceElement *element = section[kind];
    
    Class<EHIListCell> klass = element.klass;
    id model = element.models[indexPath.item];
    
    // if we don't have anything to dequeue a header with in this section, then don't
    if(!model && !klass) {
        return nil;
    }
    
    NSAssert(!model || klass, @"Must set a class in section %d for element %@", (int)section.index, kind);

    // attempt to dequeue the reusable view
    EHICollectionReusableView *view =
        [self ehi_dequeueReusableSupplementaryViewWithKind:kind class:klass metrics:element.metrics model:model atIndexPath:indexPath];
    
    // notify our delegate if necessary
    if([self.delegate respondsToSelector:@selector(collectionView:didDequeueReusableView:kind:atIndexPath:)]) {
        [self.delegate collectionView:collectionView didDequeueReusableView:view kind:kind atIndexPath:indexPath];
    }
    
    return view;
}

//
// Category Hooks
//

- (void)ehi_prepareToDequeueResuableCellWithClass:(Class<EHIListCell>)klass
{
    [self ensureRegistrationForClass:klass kind:nil];
}

- (void)ehi_prepareToDequeueResuableViewWithKind:(NSString *)kind class:(Class<EHIListCell>)klass
{
    [self ensureRegistrationForClass:klass kind:kind];
}

# pragma mark - Trampoline

- (void)setTrampoline:(EHIListCollectionViewDelegateTrampoline *)trampoline
{
    _trampoline = trampoline;
    _trampoline.target = self.delegate;
    
    self.delegate = trampoline;
}

- (void)setDelegate:(id<EHIListCollectionViewDelegate>)delegate
{
    if(self.trampoline && ![delegate isKindOfClass:[EHIListCollectionViewDelegateTrampoline class]]) {
        self.trampoline.target = delegate;
        delegate = self.trampoline;
    }

    [super setDelegate:delegate];
}

# pragma mark - Registration

- (void)ensureRegistrationForClass:(Class<EHIListCell>)klass kind:(NSString *)kind;
{
    EHILayoutMetrics *metrics = [klass metrics];
    
    if(!metrics.isAutomaticallyRegisterable || [self.registeredClasses containsObject:klass]) {
        return;
    }
   
    // we'll assume this is a supplementary view
    if(kind) {
        [self ehi_registerNibForSupplementaryViewWithClass:klass deviceify:metrics.isDeviceSpecific kind:kind];
    }
    // we'll assume this is a cell
    else {
        [self ehi_registerNibForCellWithClass:klass deviceify:metrics.isDeviceSpecific];
    }
}

- (NSMutableSet *)registeredClasses
{
    if(!_registeredClasses) {
        _registeredClasses = [NSMutableSet new];
    }
    return _registeredClasses;
}

# pragma mark - Flushing

- (void)scheduleFlush
{
    if(!self.didScheduleFlush) {
        self.didScheduleFlush = YES;
        dispatch_async(dispatch_get_main_queue(), ^{
            [self flushForced:NO completion:nil];
        });
    }
}

- (void)flushWithCompletion:(void (^)(void))completion
{
    [self flushForced:YES completion:completion];
}

- (void)flushForced:(BOOL)shouldForce completion:(void(^)(void))completion
{
    // add the handler if we have one
    if(completion) {
        [self.afterFlushHandlers addObject:completion];
    }
   
    // if we don't have anything to flush, just call any handlers right away
    if(!self.invalidElements.count) {
        [self runFlushCallbacks];
    }
 
    // if we haven't already started the flush and we have something to do, then flush
    if(self.invalidElements.count && !self.isFlushing) {
        self.isFlushing = YES;
        
        [[MTRReactor reactor] clearFlush:^{
            if(self.isBatchingUpdates) {
                [self mergeElements:self.invalidElements.copy force:shouldForce];
            } else {
                [self reloadElements:self.invalidElements.copy force:shouldForce];
            }
            
            // update the flushing state
            self.didScheduleFlush = NO;
            self.isFlushing = NO;
            
            // clear out our elements and callback our handlers
            [self.invalidElements removeAllObjects];
            [self runFlushCallbacks];
        }];
    }
}

- (void)runFlushCallbacks
{
    // grab a copy of the after flush handlers
    NSArray *handlers = [self.afterFlushHandlers copy];
    [self.afterFlushHandlers removeAllObjects];
    
    // callback all the completions
    for(void(^completion)(void) in handlers) {
        completion();
    }
}

# pragma mark - Updates

- (void)mergeElements:(NSSet *)elementSet force:(BOOL)shouldForce
{
    if(!elementSet.count) {
        return;
    }
    
    // merge any sections that can be inserted/removed wholesale
    NSArray *elements = [self mergeSectionsForElements:elementSet.allObjects];
  
    // run a granular merge on whatever is left
    for(EHIListDataSourceElement *element in elements) {
        [self mergeUpdatesForElement:element];
        [element setIsInvalid:NO];
    }
}

- (NSArray *)mergeSectionsForElements:(NSArray *)elements
{
    // filter out elements that aren't primary, or need a granular merge
    NSArray *validatedElements = elements.select(^(EHIListDataSourceElement *element) {
        return element.isPrimary && (!element.previousModels.count || !element.models.count);
    })
    // perform the correct collection view operation for this section
    .each(^(EHIListDataSourceElement *element) {
        NSIndexSet *section = [NSIndexSet indexSetWithIndex:element.section.index];
        [self reloadSections:section];
    });
   
    // capture the indices of the validated sections
    NSIndexSet *validatedSections = validatedElements.map(^(EHIListDataSourceElement *element) {
        return @(element.section.index);
    }).ehi_indexSet;
  
    // find any remaining elements to validate
    NSArray *invalidElements = elements.select(^(EHIListDataSourceElement *element) {
        // update the invalid state of any elements in the inserted/deleted section
        element.isInvalid = ![validatedSections containsIndex:element.section.index];
        // return any that are still invalid
        return element.isInvalid;
    });
    
    return invalidElements;
}

- (void)mergeUpdatesForElement:(EHIListDataSourceElement *)element
{
    // don't do anything if the models are the same
    if(element.previousModels == element.models) {
        return;
    }
    
    // allocate the buffer for the updates
    void *buffer = ehi_createUpdatesBuffer(MAX(element.models.count, element.previousModels.count));
    
    // run the merge
    [EHIListMergeFaciliator processUpdates:buffer fromModels:element.previousModels toModels:element.models];
    [EHIListMergeFaciliator resolveUpdates:buffer inSection:element.section.index againstCollectionView:self];
    
    // clean up the buffer
    ehi_deleteUpdatesBuffer(buffer);       
}

- (void)reloadElements:(NSSet *)elements force:(BOOL)shouldForce
{
    if(!elements.count) {
        return;
    }
  
    [self ehi_reloadWithSelection:self.preservesSelectionOnReload];

    // mark all the elements as valid
    for(EHIListDataSourceElement *element in elements) {
        element.isInvalid = NO;
    }
    
    if([self.delegate respondsToSelector:@selector(collectionViewDidReload:)]) {
        [self.delegate collectionViewDidReload:self];
    }
}

# pragma mark - EHIListDataSourceSectionDelegate

- (void)section:(EHIListDataSourceSection *)section didInvalidateElement:(EHIListDataSourceElement *)element
{
    [self.invalidElements addObject:element];
    [self scheduleFlush];
}

# pragma mark - Touches

- (UIView *)hitTest:(CGPoint)point withEvent:(UIEvent *)event
{
    UIView *target = [super hitTest:point withEvent:event];
    
    // don't return touches to the collection view if specified
    if(self.ignoreTouchesOutsideContent && target == self) {
        target = nil;
    }
    
    return target;
}

# pragma mark - Refresh Control

- (NSInteger)refreshControlSection
{
    return self.trampoline.refreshControlSection;
}

- (void)setRefreshControlSection:(NSInteger)refreshControlSection
{
    if(self.trampoline.refreshControlSection == refreshControlSection) {
        return;
    }
   
    if(self.trampoline.refreshControlSection != EHIRefreshControlSectionNil) {
        [self.sections removeSection:self.trampoline.refreshControlSection];
    }
    
    self.trampoline.refreshControlSection = refreshControlSection;
   
    if(self.trampoline.refreshControlSection != EHIRefreshControlSectionNil) {
        EHIListDataSourceSection *section = self.sections[self.trampoline.refreshControlSection];
        section.klass = [EHIRefreshControlCell class];
        section.model = [EHIRefreshControlViewModel new];
    }
}

- (EHIRefreshControlViewModel *)refreshControl
{
    return self.refreshControlSection != EHIRefreshControlSectionNil ? self.sections[self.refreshControlSection].model : nil;
}

# pragma mark - Accessors

- (EHIListDataSourceSection *)section
{
    return self.sections[0];
}

- (id<EHIListCollectionViewDelegate>)delegate
{
    return (id<EHIListCollectionViewDelegate>)[super delegate];
}

- (id<EHIListCollectionViewDelegate>)customDelegate
{
    return self.delegate;
}

@end
