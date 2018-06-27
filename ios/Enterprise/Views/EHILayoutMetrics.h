//
//  EHILayoutMetrics.h
//  Enterprise
//
//  Created by Ty Cobb on 1/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@protocol EHILayoutable, EHIUpdatable;

@interface EHILayoutMetrics : NSObject <NSCopying>

/** A custom cell identifier to use; may be nil */
@property (copy  , nonatomic) NSString *identifier;
/** Property for passing additional information */
@property (assign, nonatomic) NSInteger tag;

/** Fixed dimensions independent of container */
@property (assign, nonatomic) CGSize fixedSize;
/** Offset from the container center */
@property (assign, nonatomic) UIOffset centerOffset;
/** Insets to the edge of the container */
@property (assign, nonatomic) UIEdgeInsets insets;

/** The primary font to use for layout; may be nil */
@property (copy, nonatomic) UIFont *primaryFont;
/** The secondary font to use for layout; may be nil */
@property (copy, nonatomic) UIFont *secondaryFont;
/** The primary color to use for layout; may be nil */
@property (copy, nonatomic) UIColor *primaryColor;
/** The background color to use for layout; may be nil */
@property (copy, nonatomic) UIColor *backgroundColor;

/** @c YES if storyboard placeholders for this class should be unarchived from a nib in -awakeAfterUsingCoder: */
@property (assign, nonatomic) BOOL isReplaceable;
/** @c YES if this class should be unarchived from a device-specific nib */
@property (assign, nonatomic) BOOL isDeviceSpecific;
/** @c YES if this class should be automatically reigstered when it's added to a @c EHIListCollectionView */
@property (assign, nonatomic) BOOL isAutomaticallyRegisterable;

@end

@interface EHILayoutMetrics (Factory)

/** Performs startup tasks to accomodate layout metrics */
+ (void)prepareToLaunch;

/**
 @brief Returns the shared layout metrics for the class
 
 Metrics are cached after initial creation, so callers should expect to get back the same
 instance between calls. Metrics are copyable, so if customization is required the instance
 returned from this class can be modified after copying.
 
 @param klass The class to fetch metrics for
 
 @return The shared layout metrics for this class
*/

+ (instancetype)metricsForClass:(Class<EHILayoutable>)klass;

@end

@interface EHILayoutMetrics (Calculation)

/**
 @brief Calculates the expected size for the container
 @param size The size of the container
 @return An expected size given these metrics
*/

- (CGSize)sizeForContainerSize:(CGSize)size;

/**
 @brief Updates the metrics dynamically for the given context
 
 If the view is @c nil, this method returns @c CGSizeZero. This method modifies the @c fixedSize
 property of the metrics to match the return value of this method.
 
 @c intrinsicContentSize should be implemented on the @c view to return the correct size based 
 on its current state.
 
 @param view    The view to size
 @param size    The size of the containing view
 @param model   The model to update the view with before sizing
 
 @return The size of the view in the given context
*/

- (CGSize)dynamicSizeForView:(UIView<EHIUpdatable> *)view containerSize:(CGSize)size model:(id)model;

@end

@interface MASConstraintMaker (Metrics)

/**
 @brief Constrains the view to its superview, according to the metrics
 
 Fixed size constraints are added to the view itself, and edge constraints are added
 to the superview when a fixed size is not present.
 
 @param block:metrics The metrics to apply ot the view
*/

- (void(^)(EHILayoutMetrics *))metrics;

@end
