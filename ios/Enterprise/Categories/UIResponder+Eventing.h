//
//  UIResponder+Eventing.h
//  Enterprise
//
//  Created by Ty Cobb on 3/30/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@interface UIResponder (Eventing)

/**
 @brief Attempts to send a message up the responder chain
 
 The first responder in the chain who responds to the @c action will receive
 the message. If no responder is found, this method does nothing.
 
 You should pass yourself as the @c sender.
 
 @param action The action to call on the responder
 @param sender The member of the chain that delivered this message
*/

- (void)ehi_performAction:(SEL)action withSender:(id)sender;

@end
