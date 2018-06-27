//
//  EHICollectionViewCell.m
//  Enterprise
//
//  Created by Ty Cobb on 1/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@import ObjectiveC;

#import "EHICollectionViewCell.h"
#import "EHIView.h"

@interface EHICollectionViewCell ()
@property (strong, nonatomic) id ownershipToken;
@property (assign, nonatomic) BOOL isReferenceCell;
@property (assign, nonatomic) BOOL hasBecomeVisible;
@end

@implementation EHICollectionViewCell

- (void)setBounds:(CGRect)bounds
{
    [super setBounds:bounds];
    
    // This is a workaround for iOS 8 collection view cell constraint bug
    self.contentView.frame = bounds;
}

# pragma mark - Lifecycle

- (void)willMoveToWindow:(UIWindow *)window
{
    [super willMoveToWindow:window];
    
    if(window) {
        // activate our current view model, if any
        [self activateViewModel:self.viewModel];
        // mark that we've reached the visibility point
        self.hasBecomeVisible = YES;
    }
}

- (void)didMoveToWindow
{
    [super didMoveToWindow];
    
    if(!self.window) {
        // tell the view model we're going offscreen
        self.viewModel.isActive = NO;
        self.hasBecomeVisible = NO;
    }
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    // give subviews a chance to set the accessibilities identifiers
    [self registerAccessibilityIdentifiers];
}

# pragma mark - Accessibility

- (void)registerAccessibilityIdentifiers { }

# pragma mark - View Model

- (void)setViewModel:(EHIViewModel *)viewModel
{
    // filter out redundant updates
    if(!self.isReferenceCell && [_viewModel isEqual:viewModel]) {
        return;
    }
   
    // the reference cell doesn't participate in the view model lifecycle
    if(!self.isReferenceCell) {
        // if we still own our old view model, let's clean it up so old reactions don't fire
        if([self.ownershipToken isEqual:_viewModel.ownershipToken]) {
            _viewModel.isActive = NO;
            
            // allow cell to make preparations to take on a new viewModel
            [self prepareToBindViewModel:viewModel];
        }
        // take ownersip of this new view model
        self.ownershipToken = [viewModel takeOwnership];
    }
  
    _viewModel = viewModel;
   
    // if we've already become visible, activate this view model immediately
    if(self.hasBecomeVisible) {
        [self activateViewModel:viewModel];
    }
}

- (void)activateViewModel:(EHIViewModel *)viewModel
{
    // by default, the reference cell does not allow reactivity
    [self activateViewModel:viewModel permitsReactivity:!self.isReferenceCell];
}

- (void)activateViewModel:(EHIViewModel *)viewModel permitsReactivity:(BOOL)permitsReactivity
{
    if(viewModel) {
        // tell the view model it's active. the reference cell doesn't participate
        // in the view model lifecycle
        if(!self.isReferenceCell) {
            viewModel.isActive = YES;
        }
        
        // register reactions any time a view model moves into being, as long as reactivity
        // is allowed
        if(permitsReactivity) {
            [self registerReactions:viewModel];
        }
        else {
            [[MTRReactor reactor] disabled:^{
                [self registerReactions:viewModel];
            }];
        }
    }
}

# pragma mark - EHIUpdatable

- (void)updateWithModel:(id)model
{
    [self updateWithModel:model metrics:self.class.metrics];
}

- (void)updateWithModel:(id)model metrics:(EHILayoutMetrics *)metrics
{
    // if the model is a view model, then we're going to cycle this cell's vm
    if([model isKindOfClass:[EHIViewModel class]]) {
        self.viewModel = model;
    }
    // otherwise, update our view model with whatever we've got
    else {
        [self.viewModel updateWithModel:model];
    }

    if(self.hasBecomeVisible) {
        [[MTRReactor reactor] flush];
        
        // view model changes could cause the constraints to need updating, so let's make
        // we take a pass through that method
        [self setNeedsUpdateConstraints];
    }
}

# pragma mark - EHILayoutable

+ (EHILayoutMetrics *)defaultMetrics
{
    return [EHILayoutMetrics new];
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
    
    return [metrics dynamicSizeForView:self.reference.cell containerSize:size model:model];
}

- (void)registerReactions:(id)model
{
    // reference cell never goes on screen so must manually setup reactions on custom subview
    if(self.isReferenceCell) {
        for(EHIView *view in self.customSubviews) {
            [view registerReactions:view.viewModel];
        }
    }
}

- (void)didEndDisplaying
{
    
}

# pragma mark - Section Ordering

- (void)setIsLastInSection:(BOOL)lastInSection
{
    _isLastInSection = lastInSection;
    
    self.divider.hidden = lastInSection;
}

# pragma mark - Reference Cell

+ (EHIDisposable *)reference
{
    static char ehi_referenceCellKey;
   
    EHIDisposable *disposable = objc_getAssociatedObject(self, &ehi_referenceCellKey);
   
    // lazy-load the reference cell if we don't have one yet
    if(!disposable) {
        disposable = [[EHIDisposable alloc] initWithGenerator:^{
            EHICollectionViewCell *reference = [self ehi_instanceFromNib];
            
            // the reference cell will be treated as always visible
            reference.isReferenceCell  = YES;
            reference.hasBecomeVisible = YES;
            
            // manually activate the reference cell, since it won't be added to the window
            [reference activateViewModel:reference.viewModel permitsReactivity:YES];
           
            return reference;
        }];
       
        objc_setAssociatedObject(self, &ehi_referenceCellKey, disposable, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
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

@implementation EHICollectionViewCell (SubclassingHooks)

- (void)prepareToBindViewModel:(id)viewModel { }

- (NSArray *)customSubviews
{
    return @[];
}

@end

@implementation UICollectionView (EHICollectionViewCell)

- (id)ehi_dequeueReusableCellWithClass:(Class<EHIListCell>)klass model:(id)model atIndexPath:(NSIndexPath *)indexPath
{
    return [self ehi_dequeueReusableCellWithClass:klass metrics:nil model:model atIndexPath:indexPath];
}

- (id)ehi_dequeueReusableCellWithClass:(Class<EHIListCell>)klass metrics:(EHILayoutMetrics *)metrics model:(id)model atIndexPath:(NSIndexPath *)indexPath
{
    if(!metrics) {
        metrics = [klass metrics];
    }
    
    klass = [klass subclassForModel:model];
    [self ehi_prepareToDequeueResuableCellWithClass:klass];
    
    EHICollectionViewCell *cell = [self dequeueReusableCellWithReuseIdentifier:metrics.identifier ?: [klass identifier] forIndexPath:indexPath];
    [cell updateWithModel:model metrics:metrics];
    
    return cell;
}

- (void)ehi_prepareToDequeueResuableCellWithClass:(Class<EHIListCell>)klass
{
    
}

@end

@implementation EHIDisposable (EHICollectionViewCell)

- (EHICollectionViewCell *)cell
{
    return self.element;
}

@end
