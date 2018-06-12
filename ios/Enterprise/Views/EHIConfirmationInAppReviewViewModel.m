//
//  EHIConfirmationInAppReviewViewModel.m
//  Enterprise
//
//  Created by Rafael Machado on 19/07/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIConfirmationInAppReviewViewModel.h"
#import "EHIToastManager.h"
#import "EHISettings.h"
#import <StoreKit/StoreKit.h>

@implementation EHIConfirmationInAppReviewViewModel

+ (BOOL)canShowInAppReview;
{
    return NSStringFromClass(SKStoreReviewController.class) != nil;
}

+ (void)requestInAppReview
{
    [SKStoreReviewController requestReview];
    
    #if defined(DEBUG) || defined(UAT)
    if([EHISettings isDebugingInAppReview]) {
        [EHIToastManager showMessage:@"Requested App Store review"];
    }
    #endif
}

@end
