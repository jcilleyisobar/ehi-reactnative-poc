//
//  EHICrashNetworkOperation.m
//  Enterprise
//
//  Created by Rafael Ramos on 10/04/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHICrashNetworkOperation.h"

@implementation EHICrashNetworkOperation

- (instancetype)initWithMethod:(NSString *)method
                           url:(NSURL *)url
                       latency:(NSTimeInterval)latency
                     bytesRead:(NSUInteger)bytesRead
                     bytesSent:(NSUInteger)bytesSent
                  responseCode:(NSInteger)responseCode
                         error:(nullable NSError *)error
{
    if(self = [super init]) {
        _method       = method;
        _URL          = url;
        _latency      = latency;
        _bytesRead    = bytesRead;
        _bytesSent    = bytesSent;
        _responseCode = responseCode;
        _error        = error;
    }
    
    return self;
}

@end
