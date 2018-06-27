//
//  EHIReservationBuilder+Listeners.m
//  Enterprise
//
//  Created by Ty Cobb on 6/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationBuilder_Private.h"

@implementation EHIReservationBuilder (Listeners)

- (void)waitForReadiness:(id<EHIReservationBuilderReadinessListener>)listener
{
    // if we're ready, call the listener and don't store it
    if(self.isReady) {
        [listener builderIsReady:self];
    }
    // otherwise, add it to our set
    else {
        [self.listeners addObject:listener];
    }
}

- (void)resignReady
{
    self.isReady = NO;
}

- (void)becomeReady
{
    self.isReady = YES;
    
    // destroy old dependencies
    [self destroyDependencies];
    
    // notify our listeners and then remove them
    [self notifyListeners];
    [self.listeners removeAllObjects];
}

- (void)notifyListeners
{
    for(id<EHIReservationBuilderReadinessListener> listener in self.listeners) {
        [listener builderIsReady:self];
    }
}

@end
