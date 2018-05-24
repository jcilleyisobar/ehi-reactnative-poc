//
//  MTRComputation.m
//  Reactor
//
//  Created by Ty Cobb on 1/5/15.
//  Copyright (c) 2015 cobb. All rights reserved.
//

#import "MTRComputation_Private.h"
#import "MTRReactor_Private.h"

@interface MTRComputation ()
/** A list of handlers to call after the computation has invalidated */
@property (strong, nonatomic) NSMutableArray *invalidateHandlers;
/** The computation's runnable code */
@property (copy, nonatomic) void(^block)(MTRComputation *);
// read-write version of public property
@property (assign, nonatomic) BOOL isStopped;
// read-write version of public property
@property (assign, nonatomic) BOOL isInvalid;
/** True while the computation is being re-run */
@property (assign, nonatomic) BOOL isRecomputing;
@end

@implementation MTRComputation

- (instancetype)initWithId:(id)identifier block:(void (^)(MTRComputation *))block parent:(MTRComputation *)parent
{
    NSParameterAssert(identifier);
    NSParameterAssert(block);
    
    if(self = [super init]) {
        _identifier = identifier;
        _block = block;
        _parent = parent;
        _isFirstRun = YES;
        _invalidateHandlers = [NSMutableArray new];
       
        // execute the computations initial run 
        [self compute];
        
        _isFirstRun = NO;
    }
    
    return self;
}

# pragma mark - Execution

- (void)compute
{
    self.isInvalid = NO;
    [[MTRReactor reactor] computation:self executeWithBlock:^{
        MTRLogComputation(self, @"computing");
        self.block(self);
    }];
}

- (void)recompute
{
    self.isRecomputing = YES;
    
    // repeatedly run the computation until it stops invalidating itself or is stopped
    // if a computation invalidates itself every time, this will be an infinite loop
    while(self.isInvalid && !self.isStopped) {
        MTRLogComputation(self, @"recomupting");
        [self compute];
    }
    
    self.isRecomputing = NO;
}

- (void)stop
{
    if(!self.isStopped) {
        MTRLogComputation(self, @"was stopped");
        // we only need to stop once
        self.isStopped = YES;
        // invalidate to clean up any relationships holding on to this computation
        [self invalidate];
    }
}

# pragma mark - Invalidation

- (void)invalidate
{
    if(self.isInvalid) {
        return;
    }
   
    MTRLogComputation(self, @"was invalidated");
    if(!self.isRecomputing && !self.isStopped) {
        [[MTRReactor reactor] scheduleComputation:self];
    }
    
    self.isInvalid = YES;
   
    // call all our onInvalidate handlers
    for(void(^handler)(MTRComputation *) in self.invalidateHandlers) {
        [MTRReactor nonreactive:^{
            handler(self);
        }];
    }
    
    [self.invalidateHandlers removeAllObjects];
}

- (void)onInvalidate:(void (^)(MTRComputation *))handler
{
    NSParameterAssert(handler);
    
    // if we're not invalid then add this to our handlers
    if(!self.isInvalid) {
        [self.invalidateHandlers addObject:handler];
    }
    // otherwise call this immediately
    else {
        [MTRReactor nonreactive:^{
            handler(self);
        }];
    }
}

@end

@implementation MTRComputation (Debugging)

- (void)track
{
    [[MTRLogger logger] track:self];
}

@end
