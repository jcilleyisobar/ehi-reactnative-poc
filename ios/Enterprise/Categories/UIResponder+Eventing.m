//
//  UIResponder+Eventing.m
//  Enterprise
//
//  Created by Ty Cobb on 3/30/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "UIResponder+Eventing.h"

@implementation UIResponder (Eventing)

- (void)ehi_performAction:(SEL)action withSender:(id)sender
{
    id target = [self targetForAction:action withSender:sender];
    IGNORE_PERFORM_SELECTOR_WARNING(
        [target performSelector:action withObject:sender];
    );
}

@end
