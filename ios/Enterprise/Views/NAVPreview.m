//
//  NAVPeekPopTransition.m
//  Enterprise
//
//  Created by Alex Koller on 12/4/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "NAVPreview.h"

@interface NAVPreview ()
@property (strong, nonatomic) NAVTransition *peekTransition;
@property (strong, nonatomic) NAVTransition *popTransition;
@end

@implementation NAVPreview

- (instancetype)initWithPeekTransition:(NAVTransition *)peekTransition
{
    return [self initWithPeekTransition:peekTransition popTransition:nil];
}

- (instancetype)initWithPeekTransition:(NAVTransition *)peekTransition popTransition:(NAVTransition *)popTransition
{
    if(self = [super init]) {
        self.peekTransition = peekTransition;
        self.popTransition  = popTransition;
    }
    
    return self;
}

# pragma mark - Getter

- (BOOL)hasSamePeekPop
{
    return [self.peekTransition isEqual:self.popTransition];
}

- (NAVTransition *)popTransition
{
    return  _popTransition ?: _peekTransition;
}

# pragma mark - Passthrough

- (UIViewController *)peekViewController
{
    return (UIViewController *)self.peekTransition.destination;
}

@end
