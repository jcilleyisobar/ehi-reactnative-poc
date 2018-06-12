//
//  NAVAnimationTransition.h
//  Enterprise
//
//  Created by Alex Koller on 12/4/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "NAVTransition.h"

typedef NS_OPTIONS(NSUInteger, NAVAnimationOptions) {
    NAVAnimationOptionsHidden  = 0,
    NAVAnimationOptionsVisible = 1 << 0,
    NAVAnimationOptionsAsync   = 1 << 1,
    NAVAnimationOptionsModal   = 1 << 2,
};

@interface NAVAnimationTransition : NAVTransition

@property (assign, nonatomic) NAVAnimationOptions options;

@end