//
//  EHIBarButtonItem.m
//  Enterprise
//
//  Created by Ty Cobb on 1/27/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIBarButtonItem.h"
#import "EHIMenuButton.h"

@implementation EHIBarButtonItem

+ (instancetype)buttonWithType:(EHIButtonType)type target:(id)target action:(SEL)action
{
    EHIButton *button = [self buildButtonWithType:type target:target action:action];

    return [[EHIBarButtonItem alloc] initWithCustomView:button];
}

# pragma mark - Factory

+ (instancetype)flexibleSpace
{
    return [[EHIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:nil];
}

+ (instancetype)fixedSpace:(CGFloat)width
{
    EHIBarButtonItem *item = [[EHIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFixedSpace target:nil action:nil];
    item.width = width;
    return item;
}

+ (instancetype)placeholder:(CGFloat)width
{
    UIView *customView     = [[UIView alloc] initWithFrame:(CGRect){ .size.width = width }];
    EHIBarButtonItem *item = [[EHIBarButtonItem alloc] initWithCustomView:customView];
    return item;
}

+ (instancetype)backButtonWithTarget:(id)target action:(SEL)action
{
    EHIButton *button = [self buildButtonWithType:EHIButtonTypeBack target:target action:action];

    if(SYSTEM_VERSION_GREATER_THAN(11)) {
        CGFloat buttonWidth = 24;
        
        [button setCustomAlignmentRectInsets:(UIEdgeInsets) { .left = buttonWidth/2 }];
        [button.widthAnchor constraintEqualToConstant:buttonWidth].active = YES;
        [button.heightAnchor constraintEqualToConstant:44].active = YES;
    }
    
    return [[EHIBarButtonItem alloc] initWithCustomView:button];
}

//
// Helpers
//

+ (EHIButton *)buildButtonWithType:(EHIButtonType)type target:(id)target action:(SEL)action
{
    // customize the button
    EHIButton *button = [EHIButton ehi_buttonWithType:type];
    [button addTarget:target action:action forControlEvents:UIControlEventTouchUpInside];
    [button setTintColor:[UIColor whiteColor]];
    [button setTitleEdgeInsets:(UIEdgeInsets){ .top = 4.0f }];
    
    return button;
}

@end
