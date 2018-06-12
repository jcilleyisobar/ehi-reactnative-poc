//
//  UIView+Exploration.h
//  Enterprise
//
//  Created by Ty Cobb on 1/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@interface UIView (Exploration)

/**
 Traverses the view hierarchy to the specified depth and appends a terse desription of each view.
 @param depth The maximum depth to recurse to in the view hierarchy
 @return A string containing the recursive description
 */

- (NSString *)recursiveDescriptionForDepth:(NSInteger)depth;

/**
 Traverses the view hierarchy to the specified depth and calls the paramtereized description
 generator, appending its result to the description.
 
 @param depth     The maximum depth to recurse to in the view hierarchy
 @param generator A block which generates string desrciptions from a given view
 
 @return A string containing the recursive description
 */

- (NSString *)recursiveDescriptionForDepth:(NSInteger)depth withDescriptionGenerator:(NSString *(^)(UIView *view))generator;

/**
 Finds the subview specified by the stringified, period-delmited "index path". For example, if the
 string passed is "0.2", method will find the 0th subview of the callee, and then return the 2nd subview
 of that view.
 
 @param path A stringified "index path" of the form "<i_0>.<i_1>. ... <i_n>"
 @return The subview at the specified index path
 */

- (id /* UIView */)recursiveSubviewForPath:(NSString *)path;

/**
 Finds the subview specified by following the parametereized array of indices through the view hierarchy.
 For example, if the indices passed are @[ 0, 2 ], the method will find the 0th subview of the callee,
 and then return the 2nd subview of that view.
 
 @param indicies An array of indices
 @return The subview specified by the indices
 */

- (id /* UIView */)recursiveSubviewForIndices:(NSArray *)indices;

/**
 Traverses the view hierarchy of the callee in a depth-first manner, calling the predicate on each
 view in turn and returning the first view to pass the test (if any). If the stop parameter of the
 block is set to YES, the traversal terminates and the method returns nil.
 
 @param predicate The test to locate the correct subview
 @return The first subview passing the test, or nil if none is found
 */

- (id /* UIView */)findChildViewPassingTest:(BOOL(^)(UIView *view, NSInteger depth, BOOL *stop))predicate;

/**
 Traverses the view hierarchy of the callee breadth-first, returning the first view of that descends from
 the parameterized class (if any).
 
 @param klass The class of the subview to locate
 @return The first subview of this class, or nil if none is found
 */

- (id /* UIView */)find:(Class)klass;

@end
