//
//  EHICrashManager.m
//  Enterprise
//
//  Created by Rafael Ramos on 06/04/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHICrashManager.h"
#import <Crittercism/Crittercism.h>
#import "EHISettings.h"

@implementation EHICrashManager

+ (void)prepareToLauch
{
    if(IS_DEVICE) {
        // TODO - same as analytics, this will not update until the app is restarted
        [Crittercism enableWithAppID:[EHISettings environment].crittercismKey];
    }
}

+ (void)logHandledException:(NSException *)exception
{
    [Crittercism logHandledException:exception];
}

+ (void)leaveBreadcrumb:(NSString *)breadcrumb
{
    if(IS_DEVICE) {
        [Crittercism leaveBreadcrumb:breadcrumb];
    }
}

+ (void)logNetworkOperation:(EHICrashNetworkOperation *)operation
{
    [Crittercism logNetworkRequest:operation.method
                               url:operation.URL
                           latency:operation.latency
                         bytesRead:operation.bytesRead
                         bytesSent:operation.bytesSent
                      responseCode:operation.responseCode
                             error:operation.error];
}

+ (void)enableDataCollection:(BOOL)enable
{
    [Crittercism setOptOutStatus:!enable];
}

+ (BOOL)isOptedOut
{
    return [Crittercism getOptOutStatus];
}

@end
