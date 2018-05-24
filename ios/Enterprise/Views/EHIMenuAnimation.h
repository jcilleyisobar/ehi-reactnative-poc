//
//  EHIMenuAnimation.h
//  Enterprise
//
//  Created by Ty Cobb on 1/12/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "NAVAnimation.h"

@interface EHIMenuAnimation : NAVAnimation

/** @c YES if the animation is enabled, allowing its gesture to fire */
@property (assign, nonatomic) BOOL isEnabled;
/** The view containing the primary UI content */
@property (weak, nonatomic) UIView *contentView;
/** The view containing the menu; hidden/shown appropriately during transition */
@property (weak, nonatomic) UIView *drawerView;

@end
