//
//  NSMutableArray+Queue.m
//  Enterprise
//
//  Created by Alex Koller on 12/7/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "NSMutableArray+Queue.h"

@implementation NSMutableArray (Queue)

- (NSMutableArray *(^)(id object))enqueue
{
    return ^(id object) {
        [self insertObject:object atIndex:0];
        
        return self;
    };
}

- (NSMutableArray *(^)(NSArray *objects))enqueueAll
{
    return ^(NSArray *objects) {
        for(int x=0 ; x<objects.count ; x++) {
            self.enqueue(objects[x]);
        }
        
        return self;
    };
}

- (id)dequeue
{
    if(!self.count) {
        return nil;
    }
    
    NSUInteger lastIndex = self.count - 1;
    id object = self[lastIndex];
    
    [self removeObjectAtIndex:lastIndex];
    
    return object;
}

@end
