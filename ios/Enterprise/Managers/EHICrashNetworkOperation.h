//
//  EHICrashNetworkOperation.h
//  Enterprise
//
//  Created by Rafael Ramos on 10/04/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

NS_ASSUME_NONNULL_BEGIN

@interface EHICrashNetworkOperation : NSObject

@property (copy  , nonatomic, readonly) NSString *method;
@property (strong, nonatomic, readonly) NSURL* URL;
@property (assign, nonatomic, readonly) NSTimeInterval latency;
@property (assign, nonatomic, readonly) NSUInteger bytesRead;
@property (assign, nonatomic, readonly) NSUInteger bytesSent;
@property (assign, nonatomic, readonly) NSInteger responseCode;
@property (strong, nonatomic, readonly) NSError *error;

- (instancetype)initWithMethod:(NSString *)method
                           url:(NSURL *)url
                       latency:(NSTimeInterval)latency
                     bytesRead:(NSUInteger)bytesRead
                     bytesSent:(NSUInteger)bytesSent
                  responseCode:(NSInteger)responseCode
                         error:(nullable NSError *)error;

@end

NS_ASSUME_NONNULL_END
