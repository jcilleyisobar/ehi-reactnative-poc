//
//  EHICollectionReusableView.m
//  Enterprise
//
//  Created by Ty Cobb on 1/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@import ObjectiveC;

#import "EHICollectionReusableView.h"

@interface EHICollectionReusableView ()
@property (assign, nonatomic) BOOL hasRegisteredReactions;
@end

@implementation EHICollectionReusableView

- (void)willMoveToWindow:(UIWindow *)window
{
    [super willMoveToWindow:window];
    
    if(window) {
        // tell the view model it's active
        self.viewModel.isActive = YES;
        
        // register reactions any time we're moving into being
        [self registerReactions:self.viewModel];
        [self setHasRegisteredReactions:YES];
    }
}

- (void)didMoveToWindow
{
    [super didMoveToWindow];
    
    if(!self.window) {
        // tell the view model we're going offscreen
        self.viewModel.isActive = NO;
        self.hasRegisteredReactions = NO;
    }
}

+ (NSString *)kind
{
    return NSStringFromClass([self class]);
}

# pragma mark - EHIListCell

+ (Class<EHIListCell>)subclassForModel:(id)model
{
    return self;
}

+ (NSString *)identifier
{
    return NSStringFromClass(self);
}

- (void)registerReactions:(id)viewModel
{
    
}

- (void)didEndDisplaying
{
    
}

# pragma mark - EHISizable

+ (CGSize)sizeForContainerSize:(CGSize)size
{
    return [self sizeForContainerSize:size metrics:nil];
}

+ (CGSize)sizeForContainerSize:(CGSize)size metrics:(EHILayoutMetrics *)metrics
{
    if(!metrics) {
        metrics = [self metrics];
    }
    
    return [metrics sizeForContainerSize:size];
}

+ (CGSize)dynamicSizeForContainerSize:(CGSize)size metrics:(EHILayoutMetrics *)metrics model:(id)model
{
    if(!metrics) {
        metrics = [self metrics];
    }
    
    return [metrics dynamicSizeForView:self.reference.view containerSize:size model:model];
}

# pragma mark - EHILayoutable

+ (EHILayoutMetrics *)defaultMetrics
{
    return [EHILayoutMetrics new];
}

# pragma mark - EHIUpdateable

- (void)updateWithModel:(id)model
{
    [self updateWithModel:model metrics:self.class.metrics];
}

- (void)updateWithModel:(id)model metrics:(EHILayoutMetrics *)metrics
{
    [self.viewModel updateWithModel:model];
    
    // view model changes could cause the constraints to need updating, so let's make
    // we take a pass through that method
    [self setNeedsUpdateConstraints];
}


# pragma mark - Reference Cell

+ (EHIDisposable *)reference
{
    static char *ehi_referenceViewKey;
   
    EHIDisposable *disposable = objc_getAssociatedObject(self, ehi_referenceViewKey);
    if(!disposable) {
        disposable = [[EHIDisposable alloc] initWithGenerator:^{
            return [self ehi_instanceFromNib];
        }];
        
        objc_setAssociatedObject(self, ehi_referenceViewKey, disposable, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    }
    
    return disposable;
}

+ (void)modifyReferents:(NSInteger)delta
{
    // mod our reference cell's reference count
    [self reference].referents += delta;
    
    // and mod those of our subclasses, if any
    if([(id)self respondsToSelector:@selector(potentialSubclasses)]) {
        // this obviously fails if any subclass also has potential subclasses. But we don't, it's not encouraged,
        // and it's harder to account for
        NSArray *subclasses = [self potentialSubclasses];
        for(Class<EHIListCell> subclass in subclasses) {
            if(self != subclass) {
                [subclass reference].referents += delta;
            }
        }
    }
}

@end

@implementation UICollectionView (EHICollectionReusableView)

- (id)ehi_dequeueReusableSupplementaryViewWithClass:(Class<EHIListCell>)klass model:(id)model atIndexPath:(NSIndexPath *)indexPath
{
    return [self ehi_dequeueReusableSupplementaryViewWithKind:[klass kind] class:klass metrics:nil model:model atIndexPath:indexPath];
}

- (id)ehi_dequeueReusableSupplementaryViewWithKind:(NSString *)kind class:(Class<EHIListCell>)klass model:(id)model atIndexPath:(NSIndexPath *)indexPath
{
    return [self ehi_dequeueReusableSupplementaryViewWithKind:kind class:klass metrics:nil model:model atIndexPath:indexPath];
}

- (id)ehi_dequeueReusableSupplementaryViewWithKind:(NSString *)kind class:(Class<EHIListCell>)klass metrics:(EHILayoutMetrics *)metrics model:(id)model atIndexPath:(NSIndexPath *)indexPath
{
    if(!metrics) {
        metrics = [klass metrics];
    }
    
    klass = [klass subclassForModel:model];
    [self ehi_prepareToDequeueResuableViewWithKind:kind class:klass];
    
    EHICollectionReusableView *cell = [self dequeueReusableSupplementaryViewOfKind:kind withReuseIdentifier:metrics.identifier ?: [klass identifier] forIndexPath:indexPath];
    [cell updateWithModel:model metrics:metrics];
    
    return cell;
}

- (void)ehi_prepareToDequeueResuableViewWithKind:(NSString *)kind class:(Class<EHIListCell>)klass
{
    
}

@end

@implementation EHIDisposable (EHICollectionReusableView)

- (EHICollectionReusableView *)view
{
    return self.element;
}

@end
