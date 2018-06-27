//
//  UIView+Autolayout.m
//  Enterprise
//
//  Created by Ty Cobb on 1/27/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "UIView+Autolayout.h"

@implementation UIView (Autolayout)

- (void)ehi_migrateToSuperview:(UIView *)superview
{
    // capture the previous superview
    UIView *previousSuperview = self.superview;
    
    // find any constraints on the old superview involving this view
    NSArray *constraints = [previousSuperview ehi_removeConstraintsInvolvingView:self];
    NSArray *migratedConstratints = [previousSuperview ehi_migrateConstraints:constraints toView:superview];
    
    // move the view into the new superview
    [superview addSubview:self];
    [superview addConstraints:migratedConstratints];
}

- (NSArray *)ehi_migrateConstraintsToView:(UIView *)view
{
    return [self ehi_migrateConstraints:self.constraints toView:view];
}

- (NSArray *)ehi_migrateConstraints:(NSArray *)constraints toView:(UIView *)view
{
    if(!constraints) {
        return @[];
    }
    
    return constraints.map(^(NSLayoutConstraint *constraint){
        return [self ehi_migrateConstraint:constraint toView:view];
    });
}

- (NSArray *)ehi_removeConstraintsInvolvingView:(UIView *)view
{
    NSArray *constraints = [self ehi_constraintsInvolvingView:view];
    [self removeConstraints:constraints];
    return constraints;
}

- (NSArray *)ehi_constraintsInvolvingView:(UIView *)view
{
    return self.constraints.select(^(NSLayoutConstraint *constraint) {
        return constraint.firstItem == view || constraint.secondItem == view;
    });
}

- (NSLayoutConstraint *)ehi_firstConstraintInvolvingAttribute:(NSLayoutAttribute)attribute
{
    return self.constraints.find(^(NSLayoutConstraint *constraint) {
        return constraint.firstAttribute == attribute || constraint.secondAttribute == attribute;
    });
}

- (void)ehi_animateConstraintChangeWithDuration:(float)duration completion:(void (^)())completion;
{
    [self setNeedsUpdateConstraints];
    
    [UIView animateWithDuration:duration animations:^{
        [self layoutIfNeeded];
    } completion:^(BOOL finished) {
        ehi_call(completion)();
    }];
}

//
// Helpers
//

- (NSLayoutConstraint *)ehi_migrateConstraint:(NSLayoutConstraint *)constraint toView:(UIView *)view;
{
    NSLayoutAttribute firstAttribute  = constraint.firstAttribute;
    NSLayoutAttribute secondAttribute = constraint.secondAttribute;
    
    id firstItem  = [self ehi_migrateItem:constraint.firstItem attribute:&firstAttribute toView:view];
    id secondItem = [self ehi_migrateItem:constraint.secondItem attribute:&secondAttribute toView:view];
    
    // autolayout is so beautiful :|
    return [NSLayoutConstraint constraintWithItem:firstItem attribute:firstAttribute relatedBy:constraint.relation
                                           toItem:secondItem attribute:secondAttribute
                                       multiplier:constraint.multiplier constant:constraint.constant];
}

- (id)ehi_migrateItem:(id)item attribute:(NSLayoutAttribute *)attribute toView:(UIView *)view
{
    id result = item;
    
    // if the item is us, migrate to the view
    if(item == self) {
        result = view;
    }
    else if([item conformsToProtocol:@protocol(UILayoutSupport)]) {
        result = view;
        ehi_invertAttribute(attribute);
    }
    
    return result;
}

NS_INLINE void ehi_invertAttribute(NSLayoutAttribute *attribute)
{
    *attribute = ehi_inversionForAttribute(*attribute);
}

NS_INLINE NSLayoutAttribute ehi_inversionForAttribute(NSLayoutAttribute attribute)
{
    switch(attribute) {
        case NSLayoutAttributeBottom:
            return NSLayoutAttributeTop;
        case NSLayoutAttributeTop:
            return NSLayoutAttributeBottom;
        default: return attribute;
    }
}

@end
