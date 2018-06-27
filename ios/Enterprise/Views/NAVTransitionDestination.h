//
//  NAVTransitionDestination.h
//  Enterprise
//
//  Created by Alex Koller on 12/8/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "NAVAttributes.h"

@protocol NAVTransitionDestination <NSObject>

/**
 @brief Updates the destination with the executing transition's attributes
 
 Attributes contain the transitioninging URLs as well as any user object the initiator
 of the transition may want to pass to the animation.
 
 Subclasses should override this method to capture information passed to them by other views.
 
 @param attribtues The attributes sent along by the transition initiator.
 */

- (void)updateWithAttributes:(NAVAttributes *)attributes;

@end
