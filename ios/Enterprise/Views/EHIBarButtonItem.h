//
//  EHIBarButtonItem.h
//  Enterprise
//
//  Created by Ty Cobb on 1/27/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIButton.h"

@interface EHIBarButtonItem : UIBarButtonItem

/** The button backing this bar button item */
@property (nonatomic, readonly) EHIButton *button;

/**
 @brief Creates a bar-button with the specified type
 
 The type-specific styling is applied to the bar-button automatically.
 
 @param type   The custom button type to create
 @param target The target to call when tapped
 @param action The action to call when tapped
 
 @return A new EHIBarButtonItem instance
*/

+ (instancetype)buttonWithType:(EHIButtonType)type target:(id)target action:(SEL)action;

+ (instancetype)backButtonWithTarget:(id)target action:(SEL)action;

/** Creates a bar-button item with the flexible space type */
+ (instancetype)flexibleSpace;
/** Creates a bar-button item with fixed space equaling @c width */
+ (instancetype)fixedSpace:(CGFloat)width;
/** Creates a bar-button item to be used as a placeholder (not spacing) with @c width */
+ (instancetype)placeholder:(CGFloat)width;

@end
