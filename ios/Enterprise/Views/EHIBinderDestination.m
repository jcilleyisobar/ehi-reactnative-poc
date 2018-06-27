//
//  EHIBinderDestination.m
//  Enterprise
//
//  Created by Ty Cobb on 3/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIBinderDestination.h"

@interface EHIBinderDestination ()
@property (strong, nonatomic) id target;
@property (copy  , nonatomic) NSString *keypath;
@property (copy  , nonatomic) EHIBinderDestinationTransform transformer;
@end

@implementation EHIBinderDestination

- (instancetype)initWithTarget:(id)target keypath:(NSString *)keypath
{
    if(self = [super init]) {
        _target = target;
        _keypath = keypath;
    }
    
    return self;
}

- (void)updateWithValue:(id)value
{
    // apply a transform if possible
    if(self.transformer) {
        value = self.transformer(value);
    }
   
    // then update the target
    [self.target setValue:value forKeyPath:self.keypath];
}

- (EHIBinderDestination *(^)(EHIBinderDestinationTransform))transform
{
    return ^(EHIBinderDestinationTransform transformer) {
        self.transformer = transformer;
        return self;
    };
}

@end
