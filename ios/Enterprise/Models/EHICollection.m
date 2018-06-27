//
//  EHICollection.m
//  Enterprise
//
//  Created by Ty Cobb on 1/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICollection.h"

#define EHICollectionHistoryLimitNone (NSIntegerMax)

@implementation EHICollection

- (instancetype)init
{
    if(self = [super init]) {
        _historyLimit = EHICollectionHistoryLimitNone;
    }
    
    return self;
}

- (BOOL)hasHistoryLimit
{
    return self.historyLimit != EHICollectionHistoryLimitNone;
}

@end
