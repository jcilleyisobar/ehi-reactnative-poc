//
//  EHIUserManager+DNR.h
//  Enterprise
//
//  Created by Ty Cobb on 7/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIUserManager.h"

@interface EHIUserManager (DNR)

/** Shows "Do Not Rent" modal with a "Continue" button if user is on DNR list */
+ (void)attemptToShowContinueDnrModalWithHandler:(void (^)(BOOL shouldContinue))handler;
/** Shows "Do Not Rent" modal with a "Return" button if user is on DNR list */
+ (void)attemptToShowReturnDnrModalWithHandler:(void (^)(BOOL shouldContinue))handler;

@end
