//
//  NAVTransition_Subclass.h
//  Enterprise
//
//  Created by Alex Koller on 12/7/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "NAVTransition.h"

@interface NAVTransition ()
/** The destination associated with this transition. @c nil if destructive. */
@property (strong, nonatomic) id<NAVTransitionDestination> destination;
@end