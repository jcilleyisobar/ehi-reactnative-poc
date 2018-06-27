//
//  UIView+Exploration.m
//  Enterprise
//
//  Created by Ty Cobb on 1/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "UIView+Exploration.h"

@implementation UIView (Exploration)

# pragma mark - Logging

- (NSString *)recursiveDescriptionForDepth:(NSInteger)maximumDepth
{
    return [self recursiveDescriptionForDepth:maximumDepth withDescriptionGenerator:^NSString *(UIView *view) {
        return [NSString stringWithFormat:@"frame = (%.1f %.1f, %.1f %.1f); visible: %d; interactable: %d; autolayout: %d needsLayout:%d",
            view.frame.origin.x, view.frame.origin.y, view.frame.size.width, view.frame.size.height,
            !view.hidden && view.alpha != 0.0f,
            view.userInteractionEnabled,
            !view.translatesAutoresizingMaskIntoConstraints,
            view.layer.needsLayout
        ];
    }];
}

- (NSString *)recursiveDescriptionForDepth:(NSInteger)maximumDepth withDescriptionGenerator:(NSString *(^)(UIView *))generator
{
    // blocks that will allow to perform recursion without allocating an enormous
    // number of strings or passing a dozen different parameters
    typedef void(^EHIStringBuilder)(UIView *view, NSInteger depth);
    EHIStringBuilder builder;
    __weak __block EHIStringBuilder weakBuilder;
    
    // container to append to inside the block
    NSMutableString *string = [NSMutableString string];
    
    weakBuilder = builder = ^(UIView *view, NSInteger depth) {
        NSString *marginString = [@"" stringByPaddingToLength:depth * 5 withString:@"   | " startingAtIndex:0];
        [string appendFormat:@"%@<%@: %p; %@>\n", marginString, view.class, view, generator(view)];
        
        NSInteger nextDepth = depth + 1;
        for(UIView *subview in view.subviews) {
            if(nextDepth < maximumDepth) {
                weakBuilder(subview, nextDepth);
            }
        }
    };
    
    builder(self, 0);
    
    return string;
}

# pragma mark - Exploration

- (id)recursiveSubviewForPath:(NSString *)path
{
    return [self recursiveSubviewForIndices:[path componentsSeparatedByString:@"."].map(^(NSString *string) {
        return @([string integerValue]);
    })];
}

- (id)recursiveSubviewForIndices:(NSArray *)indices
{
    return [self recursiveSubviewForIndices:indices index:0];
}

- (id)recursiveSubviewForIndices:(NSArray *)indices index:(NSInteger)index
{
    if(index >= indices.count) {
        return self;
    }
    
    NSNumber *subviewIndex = indices[index];
    UIView *subview = self.subviews[[subviewIndex integerValue]];
    
    return [subview recursiveSubviewForIndices:indices index:index + 1];
}

- (id)find:(Class)klass
{
    return [self findChildViewPassingTest:^BOOL(UIView *view, NSInteger depth, BOOL *stop) {
        return [view isKindOfClass:klass];
    }];
}

- (id)findChildViewPassingTest:(BOOL(^)(UIView *view, NSInteger depth, BOOL *stop))predicate
{
    return [self findChildViewInSet:self.subviews atDepth:0 passingTest:predicate];
}

- (id)findChildViewInSet:(NSArray *)exploreSet atDepth:(NSInteger)depth passingTest:(BOOL(^)(UIView *view, NSInteger depth, BOOL *stop))predicate
{
    if(exploreSet.count == 0) {
        return nil;
    }
    
    BOOL stop = NO;
    for(UIView *view in exploreSet) {
        if(predicate(view, depth, &stop)) {
            return view;
        }
        if(stop) {
            return nil;
        }
    }
    
    NSMutableArray *nextExploreSet = [NSMutableArray new];
    for(UIView *view in exploreSet) {
        [nextExploreSet addObjectsFromArray:view.subviews];
    }
    
    return [self findChildViewInSet:nextExploreSet atDepth:depth + 1 passingTest:predicate];
}

@end
