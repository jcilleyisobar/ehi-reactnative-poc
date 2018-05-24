//
//  MTRReactor.m
//  Reactor
//
//  Created by Ty Cobb on 1/5/15.
//  Copyright (c) 2015 cobb. All rights reserved.
//

#import "MTRReactor_Private.h"
#import "MTRComputation_Private.h"

@implementation MTRReactor

+ (instancetype)reactor
{
    static MTRReactor *reactor;
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        reactor = [self new];
    });
    
    return reactor;
}

- (instancetype)init
{
    if(self = [super init]) {
        _pendingComputations = [NSMutableArray new];
        _afterFlushHandlers  = [NSMutableArray new];
        _clearFlushHandlers  = [NSMutableArray new];
    }
    
    return self;
}

# pragma mark - Registration

+ (void)engage
{
    [MTRReactiveEngine engage];
}

+ (void)reactify:(Class<MTRReactive>)klass
{
    [MTRReactiveEngine reactify:klass];
}

# pragma mark - Execution

+ (MTRComputation *)autorun:(void (^)(MTRComputation *))block
{
    return [[self reactor] autorun:block];
}

+ (MTRComputation *)autorun:(id)target action:(SEL)action
{
    return [[self reactor] autorun:^(MTRComputation *computation) {
# pragma clang diagnostic push
# pragma clang diagnostic ignored "-Warc-performSelector-leaks"
        [target performSelector:action withObject:computation];
# pragma clang diagnosic pop
    }];
}

+ (void)nonreactive:(void (^)(void))block
{
    [[self reactor] nonreactive:block];
}

- (MTRComputation *)autorun:(void (^)(MTRComputation *))block
{
    id identifier = @(self.nextId++);
    
    // create a new computation
    MTRComputation *computation =
        [[MTRComputation alloc] initWithId:identifier block:block parent:self.currentComputation];
    
    if(self.isActive) {
        MTRLogComputation(computation, @"will stop on invalidation of: %@", self.currentComputation.identifier);
        [self onInvalidate:^(MTRComputation *parent) {
            [computation stop];
        }];
    }
    
    return computation;
}

- (void)nonreactive:(void (^)(void))block
{
    // save the current computation and remove it to avoid dependency attachment
    MTRComputation *previous = self.currentComputation;
    self.currentComputation  = nil;
    
    block();
    
    // reset the computation
    self.currentComputation = previous;
}

- (void)disabled:(void (^)(void))block
{
    [self nonreactive:^{
        self.isDisabled = YES;
        block();
        self.isDisabled = NO;
    }];
}

- (void)computation:(MTRComputation *)computation executeWithBlock:(void (^)(void))block
{
    // save the current computation
    MTRComputation *previous = self.currentComputation;
    BOOL previousIsComputing = self.isComputing;
    
    // update the current computation
    self.currentComputation = computation;
    self.isComputing = YES;
    
    block();
    
    // reset the computation
    self.currentComputation = previous;
    self.isComputing = previousIsComputing;
}

# pragma mark - Scheduling/Flushing

- (void)scheduleComputation:(MTRComputation *)computation
{
    MTRLogComputation(computation, @"was scheduled");
    
    [self scheduleFlush];
    [self.pendingComputations addObject:computation];
}

- (void)scheduleFlush
{
    if(!self.flushScheduled) {
        MTRLog(@"scheduled flush");
        dispatch_async(dispatch_get_main_queue(), ^{
            [self flush];
        });
        self.flushScheduled = YES;
    }
}

- (NSError *)flush
{
    // check flushing preconditions
    NSError *error = [self flushPermissionsFailure];
    
    // if we encountered an error then just log it
    if(error) {
        MTRLog(@"%@", error);
    }
    // otherwise run through the flush cycle
    else {
        self.flushScheduled = YES;
        self.isFlushing = YES;
        
        while(self.pendingComputations.count || self.afterFlushHandlers.count) {
            // recompute all the computations
            while(self.pendingComputations.count) {
                // shift off the first computation
                MTRComputation *computation = [self.pendingComputations mtr_shift];
                [computation recompute];
            }
            
            if(self.afterFlushHandlers.count) {
                // after flush handlers can invalidate more computations
                void(^handler)(void) = [self.afterFlushHandlers mtr_shift];
                handler();
            }
        }
        
        // mark the flush as complete
        self.flushScheduled = NO;
        self.isFlushing = NO;
        
        [self didClearFlush];
    }
    
    return error;
}

- (void)didClearFlush
{
    // grab a copy of the handlers and remove them, in case one re-flushes
    NSArray *handlers = [self.clearFlushHandlers copy];
    [self.clearFlushHandlers removeAllObjects];
    
    // call all the clear flush handlers
    for(void(^handler)(void) in handlers) {
        handler();
    }
}

- (NSError *)flushPermissionsFailure
{
    if(self.isDisabled) {
        return [NSError errorWithDomain:MTRReactorFlushError code:-1 userInfo:@{ NSLocalizedDescriptionKey: @"Attempted to -flush while reactor is disabled" }];
    } else if(self.isFlushing) {
        return [NSError errorWithDomain:MTRReactorFlushError code:-1 userInfo:@{ NSLocalizedDescriptionKey: @"Can't call -flush while already flushing" }];
    } else if(self.isComputing) {
        return [NSError errorWithDomain:MTRReactorFlushError code:-1 userInfo:@{ NSLocalizedDescriptionKey: @"Can't call -flush inside a computation." }];
    }
    
    return nil;
}

# pragma mark - Hooks

- (void)onInvalidate:(void (^)(MTRComputation *))handler
{
    MTRAssert(self.isActive, @"Can't add an onInvalidate handler if there's no computation");
    [self.currentComputation onInvalidate:handler];
}

- (void)afterFlush:(void (^)(void))handler
{
    NSParameterAssert(handler);
    
    [self.afterFlushHandlers addObject:handler];
    [self scheduleFlush];
}

- (void)clearFlush:(void (^)(void))handler
{
    NSParameterAssert(handler);
    
    if(!self.flushScheduled) {
        handler();
    } else {
        [self.clearFlushHandlers addObject:handler];
        [self flush];
    }
}

# pragma mark - Setters

- (void)setCurrentComputation:(MTRComputation *)currentComputation
{
    if(!self.isDisabled) {
        _currentComputation = currentComputation;
    }
}

# pragma mark - Accessors

- (BOOL)isActive
{
    return self.currentComputation != nil;
}

//
// Errors
//

NSString * const MTRReactorError = @"MTRReactorError";
NSString * const MTRReactorFlushError = @"MTRReactorFlushError";

void MTRAssert(BOOL condition, NSString *format, ...)
{
    if(condition) {
        return;
    }
    
    va_list args;
    va_start(args, format);
    
    NSString *reason = [[NSString alloc] initWithFormat:format arguments:args];
    [[NSException exceptionWithName:MTRReactorError reason:reason userInfo:nil] raise];
    
    va_end(args);
}

@end

@implementation NSMutableArray (Utility)

- (id)mtr_shift
{
    if(self.count == 0) {
        return nil;
    }
    
    id object = [self objectAtIndex:0];
    [self removeObjectAtIndex:0];
    
    return object;
}

@end
