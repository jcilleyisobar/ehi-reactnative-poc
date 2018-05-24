//
//  NSMutableArray+Queue.h
//  Enterprise
//
//  Created by Alex Koller on 12/7/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSMutableArray (Queue)

- (NSMutableArray *(^)(NSArray *objects))enqueueAll;

- (NSMutableArray *(^)(id object))enqueue;

- (id)dequeue;

@end
