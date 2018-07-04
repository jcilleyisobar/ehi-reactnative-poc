//
//  EHIRNEventDispatcher.m
//  Enterprise
//
//  Created by Jeff Cilley on 6/29/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIRNEventDispatcher.h"

@interface EHIRNEventDispatcher ()

@property(assign) BOOL hasListeners;

@end

@implementation EHIRNEventDispatcher

RCT_EXPORT_MODULE();

- (NSArray<NSString *> *)supportedEvents
{
    return @[@"EventAEMEnvironmentUpdate"];
}

// Will be called when this module's first listener is added.
-(void)startObserving {
    self.hasListeners = YES;
    // Set up any upstream listeners or background tasks as necessary
}

// Will be called when this module's last listener is removed, or on dealloc.
-(void)stopObserving {
    self.hasListeners = NO;
    // Remove upstream listeners, stop unnecessary background tasks
}
#pragma mark - Events -

- (void)updateAEMEnvironment
{
    [self sendEventWithName:@"EventAEMEnvironmentUpdate" body:@{@"msg": @"Hello React Native land!"}];
}

@end



