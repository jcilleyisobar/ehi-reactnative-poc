//
//  EHIView.m
//  Enterprise
//
//  Created by Michael Place on 3/10/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIView.h"

@interface EHIView ()
@property (assign, nonatomic) BOOL hasRegisteredReactions;
@end

@implementation EHIView

- (id)awakeAfterUsingCoder:(NSCoder *)aDecoder
{
    if(!self.class.isReplaceable || self.subviews.count) {
        return self;
    }
    
    EHIView *replacement = [self.class ehi_instanceFromNib];
    
    // synchronize necessary properties
    replacement.tag = self.tag;
    replacement.frame = self.frame;
    replacement.translatesAutoresizingMaskIntoConstraints = self.translatesAutoresizingMaskIntoConstraints;
    
    // move all our constraints from the placeholder to this view
    [replacement addConstraints:[self ehi_migrateConstraintsToView:replacement]];
    
    return replacement;
}

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

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    // give subviews a chance to set the accessibilities identifiers
    [self registerAccessibilityIdentifiers];
}

# pragma mark - Accessibility

- (void)registerAccessibilityIdentifiers { }

# pragma mark - Replaceability

+ (BOOL)isReplaceable
{
    return NO;
}

# pragma mark - EHIUpdatable

- (void)updateWithModel:(id)model
{
    [self updateWithModel:model metrics:self.class.metrics];
}

- (void)updateWithModel:(id)model metrics:(EHILayoutMetrics *)metrics
{
    // if the model is a view model, then we're going to cycle
    if([model isKindOfClass:[EHIViewModel class]]) {
        self.viewModel = model;
    }
    // otherwise, update our view model with whatever we've got
    else {
        [self.viewModel updateWithModel:model];
    }
    
    if(self.hasRegisteredReactions) {
        [[MTRReactor reactor] flush];
        
        // view model changes could cause the constraints to need updating, so let's make
        // we take a pass through that method
        [self setNeedsUpdateConstraints];
    }
}

- (void)registerReactions:(id)model
{
    
}

- (void)resizeDynamicallyForContainer:(CGSize)size model:(id)model
{
    // bind the model and update the metrics fixed size
    [[self.class metrics] dynamicSizeForView:self containerSize:size model:model];
}

- (void)forceLayout
{
    [UIView animateWithDuration:0.01 animations:^{
        [self layoutIfNeeded];
    }];
}

# pragma mark - EHILayoutable

+ (EHILayoutMetrics *)defaultMetrics
{
    return [EHILayoutMetrics new];
}

@end
