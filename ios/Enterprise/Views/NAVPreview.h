//
//  NAVPeekPopTransition.h
//  Enterprise
//
//  Created by Alex Koller on 12/4/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "NAVTransition.h"

@interface NAVPreview : NSObject

@property (assign, nonatomic, readonly) BOOL hasSamePeekPop;

/**
 @brief transition used during peek phase of preview
 
 This transition is used when constructing the @c peekViewController to be passed
 to the system during @c previewingContext:viewControllerForLocation.
 */

@property (strong, nonatomic, readonly) NAVTransition *peekTransition;

/**
 @brief transition used during the pop phase of preview
 
 If @c nil, the peek transition, along with it's already existing controller, are
 reused for the pop transition. Otherwise, a new controller is instantiated from this
 transition and committed to the view controller stack.
 */

@property (strong, nonatomic, readonly) NAVTransition *popTransition;

/** 
 @brief the view controller associated with @c peekTransition. @c nil before transition is prepared.
 */

@property (weak  , nonatomic, readonly) UIViewController *peekViewController;

/**
 brief Passthrough to @c -initWithPeekTransition:popTransition using @c peekTransition and @c nil respectively
 
 @return A ready to be used @c NAVPreview
 */

- (instancetype)initWithPeekTransition:(NAVTransition *)peekTransition;

/**
 @brief constructs a fully configured @c NAVPreview to be used for previewing
 
 @return A ready to be used @c NAVPreview
 */

- (instancetype)initWithPeekTransition:(NAVTransition *)peekTransition popTransition:(NAVTransition *)popTransition;

@end