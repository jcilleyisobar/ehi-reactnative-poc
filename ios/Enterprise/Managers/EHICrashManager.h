//
//  EHICrashManager.h
//  Enterprise
//
//  Created by Rafael Ramos on 06/04/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHICrashNetworkOperation.h"

@interface EHICrashManager : NSObject

+ (void)prepareToLauch;
+ (void)logHandledException:(NSException *)exception;
+ (void)leaveBreadcrumb:(NSString *)breadcrumb;
+ (void)logNetworkOperation:(EHICrashNetworkOperation *)operation;

+ (void)enableDataCollection:(BOOL)enable;
+ (BOOL)isOptedOut;

@end
