//
//  EHIWebBrowserViewModel.h
//  Enterprise
//
//  Created by cgross on 1/27/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIWebBrowserViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic) void (^handler)(id returnValue);
@property (strong, nonatomic, readonly) NSURLRequest *request;
@property (copy  , nonatomic, readonly) NSString *title;

- (instancetype)initWithUrl:(NSURL *)url body:(NSString *)body;
- (instancetype)initWithUrl:(NSURL *)url body:(NSString *)body title:(NSString *)title;

/** 
 check if it's a redirect from a bank for 3DS validation
 and parse the http body to get the validation data (paRes),
 then pass the data back and dismiss the webview
 */
- (BOOL)is3dsRequest:(NSURLRequest *)request;

- (void)close;

@end
