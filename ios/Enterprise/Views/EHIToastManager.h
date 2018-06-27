//
//  EHIToastManager.h
//  Enterprise
//
//  Created by Alex Koller on 4/7/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIToast.h"

@interface EHIToastManager : NSObject

/** Singleton accessor for the toast manager */
+ (instancetype)sharedInstance;
/** Convenience method to show a toast for the given message, using the default configuration */
+ (void)showMessage:(NSString *)message;
/** Show the parameterized toast using its specified configuration */
+ (void)showToast:(EHIToast *)toast;
/** Hide the active toast, bypassing the regular hide mechanism, which is touch / time */
+ (void)hideActiveToast;

@end
