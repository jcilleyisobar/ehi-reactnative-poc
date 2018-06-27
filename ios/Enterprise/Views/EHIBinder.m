//
//  EHIBinder.m
//  Enterprise
//
//  Created by Ty Cobb on 1/28/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "Reactor.h"
#import "EHIBinder.h"

@interface EHIBinder ()
@property (strong, nonatomic) id source;
@end

@implementation EHIBinder

- (instancetype)initWithSource:(id)source
{
    if(self = [super init]) {
        _source = source;
    }
    
    return self;
}

- (EHIBinder *(^)(NSDictionary *))map
{
    return ^(NSDictionary *map) {
        for(NSString *key in map) {
            [self bind:key toFlexibleDestination:map[key]];
        }
        
        return self;
    };
}

- (EHIBinder *(^)(NSString *, id))pair
{
    return ^(NSString *key, id destination){
        [self bind:key toFlexibleDestination:destination];
        return self;
    };
}

- (void)bind:(NSString *)sourcePath toFlexibleDestination:(id)destinationOrDestinations
{
    NSAssert(sourcePath != nil, @"Must have a key to access on the source");
    
    // if this is a destination object, then it can handle the updating
    if([destinationOrDestinations isKindOfClass:[EHIBinderDestination class]]) {
        [self bind:sourcePath toDestination:destinationOrDestinations];
    }
    // if this is an array, it should be an array of destinations
    else if([destinationOrDestinations isKindOfClass:[NSArray class]]) {
        [self bind:sourcePath toDestinations:destinationOrDestinations];
    }
    // otherwise, this must be a block
    else {
        [self bind:sourcePath toBlock:destinationOrDestinations];
    }
}

- (void)bind:(NSString *)sourcePath toDestination:(EHIBinderDestination *)destination
{
    id source = self.source;
    [MTRReactor autorun:^(MTRComputation *computation) {
        [destination updateWithValue:[source valueForKeyPath:sourcePath]];
    }];
}

- (void)bind:(NSString *)sourcePath toDestinations:(NSArray *)destinations
{
    id source = self.source;
    [MTRReactor autorun:^(MTRComputation *computation) {
        id value = [source valueForKeyPath:sourcePath];
        for(EHIBinderDestination *destination in destinations) {
            [destination updateWithValue:value];
        }
    }];
}

- (void)bind:(NSString *)sourcePath toBlock:(void(^)(id))block
{
    NSAssert(block, @"The destination must be a block if it's not a target-keypath");
   
    id source = self.source;
    
    [MTRReactor autorun:^(MTRComputation *computation) {
        block([source valueForKeyPath:sourcePath]);
    }];
}

@end
