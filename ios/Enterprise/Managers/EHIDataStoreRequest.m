//
//  EHIDataStoreRequest.m
//  Enterprise
//
//  Created by Ty Cobb on 3/9/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDataStoreRequest.h"

@implementation EHIDataStoreRequest

# pragma mark - Accessors

- (Class<EHIModel>)klass
{
    return _klass ?: [self.model class];
}

- (EHICollection *)collection
{
    return _collection ?: [self.klass collection];
}

- (BOOL)isRead
{
    return self.type == EHIDataStoreRequestTypeFind || self.type == EHIDataStoreRequestTypeFirst;
}

@end

@implementation EHIDataStoreRequest (Convenience)

+ (EHIDataStoreRequest *)find:(EHICollection *)collection
{
    EHIDataStoreRequest *request = [self requestWithType:EHIDataStoreRequestTypeFind];
    request.collection = collection;
    return request;
}

+ (EHIDataStoreRequest *)first:(EHICollection *)collection
{
    EHIDataStoreRequest *request = [self requestWithType:EHIDataStoreRequestTypeFirst];
    request.collection = collection;
    return request;
}

+ (EHIDataStoreRequest *)save:(EHIModel *)model
{
    EHIDataStoreRequest *request = [self requestWithType:EHIDataStoreRequestTypeSave];
    request.model = model;
    return request;
}

+ (EHIDataStoreRequest *)remove:(EHIModel *)model
{
    EHIDataStoreRequest *request = [self requestWithType:EHIDataStoreRequestTypeDelete];
    request.model = model;
    return request;
}

+ (EHIDataStoreRequest *)purge:(EHICollection *)collection
{
    EHIDataStoreRequest *request = [self requestWithType:EHIDataStoreRequestTypeDelete];
    request.collection = collection;
    return request;
}

+ (EHIDataStoreRequest *)requestWithType:(EHIDataStoreRequestType)type
{
    EHIDataStoreRequest *request = [EHIDataStoreRequest new];
    request.type = type;
    return request;
}

@end
