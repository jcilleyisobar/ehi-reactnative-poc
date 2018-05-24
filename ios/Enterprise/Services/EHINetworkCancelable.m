//
//  EHINetworkCancelable.m
//  Enterprise
//
//  Created by Ty Cobb on 4/21/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHINetworkCancelable.h"

@interface EHINetworkCancelableGroup ()
@property (strong, nonatomic) NSMutableArray *cancelables;
@end

@implementation EHINetworkCancelableGroup

- (instancetype)init
{
    if(self = [super init]) {
        _cancelables = [NSMutableArray new];
    }
    
    return self;
}

- (void)addCancelable:(id<EHINetworkCancelable>)cancelable
{
    [self.cancelables addObject:cancelable];
}

# pragma mark - EHINetworkCancelable

- (BOOL)cancel
{
    BOOL didCancel = YES;
    
    for(id<EHINetworkCancelable> cancelable in self.cancelables) {
        didCancel &= [cancelable cancel];
    }
    
    return didCancel;
}

@end
