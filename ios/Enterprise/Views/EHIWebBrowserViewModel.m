//
//  EHIWebBrowserViewModel.m
//  Enterprise
//
//  Created by cgross on 1/27/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIWebBrowserViewModel.h"
#import "EHI3DSData.h"

@interface EHIWebBrowserViewModel ()
@property (strong, nonatomic) NSURLRequest *request;
@end

@implementation EHIWebBrowserViewModel

- (instancetype)initWithUrl:(NSURL *)url body:(NSString *)body title:(NSString *)title
{
    if(self = [self initWithUrl:url body:body]) {
        _title = title;
    }
    
    return self;
}

- (instancetype)initWithUrl:(NSURL *)url body:(NSString *)body
{
    if(self = [self initWithModel:[EHIModel placeholder]]) {
        NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:url];
        
        if (body) {
            [request setHTTPMethod:@"POST"];
            [request setHTTPBody:[body dataUsingEncoding:NSUTF8StringEncoding]];
        }
        
        self.request = request;
    }
    return self;
}

- (BOOL)is3dsRequest:(NSURLRequest *)request
{
    if ([request.URL.absoluteString isEqualToString:EHI3dsTermUrl]) {
        NSString *validationData = [self parseHttpBodyForRequest:request forKey:EHI3dsPaResKey];
        [self closeWithReturnValue:validationData];
        
        return YES;
    }

    return NO;
}

# pragma mark - Dismiss

- (void)close
{
    [self closeWithReturnValue:nil];
}

- (void)closeWithReturnValue:(id)returnValue
{
    ehi_call(self.handler)(returnValue);
    [EHIMainRouter currentRouter].transition.dismiss.start(nil);
}

# pragma mark - Helpers

- (NSString *)parseHttpBodyForRequest:(NSURLRequest *)request forKey:(NSString *)key
{
    NSString *httpBody = [[NSString alloc] initWithData:request.HTTPBody encoding:NSUTF8StringEncoding];
    
    __block NSString *value;
    NSArray *urlComponents = [httpBody componentsSeparatedByString:@"&"];
    [urlComponents enumerateObjectsUsingBlock:^(NSString *keyValuePair, NSUInteger idx, BOOL * _Nonnull stop) {
        NSArray *pairComponents = [keyValuePair componentsSeparatedByString:@"="];
        NSString *keyForPair = [[pairComponents firstObject] stringByRemovingPercentEncoding];
        NSString *valueForPair = [[pairComponents lastObject] stringByRemovingPercentEncoding];
        
        if ([keyForPair isEqualToString:key]) {
            value = valueForPair;
            *stop = YES;
        }
    }];
    
    return value;
}

@end
