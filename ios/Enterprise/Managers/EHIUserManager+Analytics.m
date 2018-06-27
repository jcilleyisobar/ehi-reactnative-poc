//
//  EHIUserManager+Analytics.m
//  Enterprise
//
//  Created by Ty Cobb on 6/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIUserManager+Analytics.h"
#import "EHISettings.h"

@implementation EHIUserManager (Analytics)

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    [context encode:[EHIUser class] encodable:self.currentUser];
    [context encode:[EHIUserCredentials class] encodable:self.credentials];
    
    // Notifications
    [[EHISettings sharedInstance] updateAnalyticsContext:context];
}

@end
