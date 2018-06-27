//
//  EHIFunctions.h
//  Enterprise
//
//  Created by Ty Cobb on 1/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@import Foundation;

# pragma mark - GCD

NS_INLINE void dispatch_after_seconds(double seconds, void(^block)(void))
{
    dispatch_time_t time = dispatch_time(DISPATCH_TIME_NOW, (int64_t)(seconds * NSEC_PER_SEC));
    dispatch_after(time, dispatch_get_main_queue(), block);
}

NS_INLINE void optionally_dispatch_async(BOOL async, dispatch_queue_t queue, dispatch_block_t block)
{
    if(async) {
        dispatch_async(queue, block);
    }
    else if(block) {
        block();
    }
}

NS_INLINE void dispatch_main_async(void(^block)(void))
{
    dispatch_async(dispatch_get_main_queue(), ^{
        block();
    });
}

NS_INLINE void dispatch_once_on_main_thread(dispatch_once_t *predicate, dispatch_block_t block)
{
    if(NSThread.isMainThread) {
        dispatch_once(predicate, block);
    } else {
        if(DISPATCH_EXPECT(*predicate == 0L, NO)) {
            dispatch_sync(dispatch_get_main_queue(), ^{
                dispatch_once(predicate, block);
            });
        }
    }
}

typedef void(^EHIDispatchSyncCompletionBlock)(void);

NS_INLINE void ehi_dispatch_sync(void(^block)(EHIDispatchSyncCompletionBlock))
{
    dispatch_group_t group = dispatch_group_create();
    dispatch_group_enter(group);
    ehi_call(block)(^{
        dispatch_group_leave(group);
    });
    
    dispatch_group_wait(group, DISPATCH_TIME_FOREVER);
}

# pragma mark - Timing Curves

NS_INLINE CGFloat ehi_quadraticEaseOut(NSTimeInterval time, CGFloat start, CGFloat delta, NSTimeInterval duration)
{
    time /= duration / 2;
    if(time < 1.0) return delta / 2 * time * time + start;
    time--;
    return -delta / 2 * (time * (time - 2) - 1) + start;
}

NS_INLINE NSString * EHIStringifyFlag(BOOL flag)
{
    return flag ? @"true" : @"false";
}
