//
//  EHIDisposable.m
//  Enterprise
//
//  Created by Ty Cobb on 7/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "Reactor.h"
#import "EHIDisposable.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIDisposable ()
@property (copy, nonatomic) id(^generator)(void);
@end

@implementation EHIDisposable

- (instancetype)initWithGenerator:(id(^)(void))generator
{
    if(self = [super init]) {
        _generator = generator;
    }
    
    return self;
}

- (void)setReferents:(NSInteger)referents
{
    _referents = referents;
   
    // construct / destruct the element as necessary
    if(!_referents && _element) {
        EHIDomainDebug(EHILogDomainMemory, @"%@ disposed: %@", self, [self.element class]);
        self.element = nil;
    }
}

- (nullable id)element
{
    // lazy load element we have referrers
    if(!_element && self.referents) {
        // if this happens in a reaction, we don't want the generator to attach any dependencies
        [MTRReactor nonreactive:^{
            _element = self.generator();
            EHIDomainVerbose(EHILogDomainMemory, @"%@ created: %@", self, [self.element class]);
        }];
    }
    
    return _element;
}

@end

NS_ASSUME_NONNULL_END
