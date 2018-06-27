//
//  EHILocationDateFilterStorage.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/30/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHILocationDateFilterStorage.h"

@implementation EHILocationDateFilterStorage

+ (EHILocationDateFilterStorage *)storage
{
    static EHILocationDateFilterStorage *_storage;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        _storage = [self new];
    });
    
    return _storage;
}

- (instancetype)init
{
    if(self = [super init]) {
        _dateStore = EHILocationDateFilterStore.new;
    }
    
    return self;
}

@end
