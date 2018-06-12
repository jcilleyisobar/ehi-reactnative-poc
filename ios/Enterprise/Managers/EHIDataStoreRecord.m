//
//  EHIDataStoreRecord.m
//  Enterprise
//
//  Created by Ty Cobb on 3/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDataStoreRecord.h"

@implementation EHIDataStoreRecord

- (instancetype)initWithModel:(EHIModel *)model
{
    if(self = [super init]) {
        _model = model;
        _createdAt = [NSDate date];
    }
    
    return self;
}

- (NSComparisonResult)compare:(EHIDataStoreRecord *)record
{
    if(![record isKindOfClass:[EHIDataStoreRecord class]]) {
        return NSOrderedAscending;
    }
   
    // if this is a record, sort by latest record first
    return [self.createdAt compare:record.createdAt];
}

@end
