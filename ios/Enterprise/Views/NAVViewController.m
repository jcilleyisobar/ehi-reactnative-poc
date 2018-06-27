//
//  NAVTransitionController.m
//  Enterprise
//
//  Created by Alex Koller on 11/16/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "NAVViewController.h"
#import "NAVPreview.h"

@interface NAVViewController () <UIViewControllerPreviewingDelegate>
@property (strong, nonatomic) NAVPreview *preview;
@end

@implementation NAVViewController

+ (instancetype)instance
{
    UIStoryboard *storyboard = [UIStoryboard storyboardWithName:self.storyboardName bundle:nil];
    return [storyboard instantiateViewControllerWithIdentifier:self.storyboardIdentifier];
}

+ (NSString *)storyboardIdentifier
{
    return NSStringFromClass(self);
}

+ (NSString *)storyboardName
{
    NSAssert(false, @"NAVTransitionController must specify a storyboard name");
    return nil;
}

+ (NSString *)screenName
{
    return nil;
}

# pragma mark - NAVTransitionDestination

- (void)updateWithAttributes:(NAVAttributes *)attributes
{
    
}

# pragma mark - Lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self registerForPreviewing];
}

# pragma mark - NAVTransitionPerformer

- (void)transition:(NAVTransition *)transition
{
    [self transition:transition completion:nil];
}

- (void)transition:(NAVTransition *)transition completion:(void (^)(void))completion
{
    [transition prepareWithController:self];
    [transition performWithCompletion:completion];
}

# pragma mark - Previewing

- (void)registerForPreviewing
{
    // skip pre 9.0 devices or those uncapable of detected 3D touch
    if(![self.traitCollection respondsToSelector:@selector(forceTouchCapability)]
       || self.traitCollection.forceTouchCapability != UIForceTouchCapabilityAvailable) {
        return;
    }
    
    // register force touch responsiveness for each view
    for(UIView *view in self.previewingSourceViews) {
        [self registerForPreviewingWithDelegate:self sourceView:view];
    }
}

- (NSArray *)previewingSourceViews
{
    return nil;
}

- (NAVPreview *)previewingContext:(id<UIViewControllerPreviewing>)previewingContext previewForLocation:(CGPoint)location
{
    return nil;
}

# pragma mark - NAVViewControllerPreviewingDelegate

- (UIViewController *)previewingContext:(id<UIViewControllerPreviewing>)previewingContext viewControllerForLocation:(CGPoint)location
{
    // apply automatic focusing
    [self focusRectForPreviewingContext:previewingContext atLocation:location];
    
    // get peek pop data from subclass
    self.preview = [self previewingContext:previewingContext previewForLocation:location];
    
    // prepare our peek transition
    [self.preview.peekTransition prepareWithController:self];
    
    return self.preview.peekViewController;
}

- (void)previewingContext:(id<UIViewControllerPreviewing>)previewingContext commitViewController:(UIViewController *)viewControllerToCommit
{
    if(!self.preview.hasSamePeekPop) {
        [self.preview.popTransition prepareWithController:self];
    }
    
    [self.preview.popTransition performWithCompletion:nil];
}

//
// Helpers
//

- (void)focusRectForPreviewingContext:(id<UIViewControllerPreviewing>)previewingContext atLocation:(CGPoint)location
{
    UIView *sourceView = previewingContext.sourceView;
    
    // automatically focus cells
    if([sourceView isKindOfClass:UICollectionView.class]) {
        previewingContext.sourceRect = [(UICollectionView *)sourceView ehi_rectForCellAtLocation:location];
    }
}

@end
