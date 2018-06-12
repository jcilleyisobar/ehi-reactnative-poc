//
//  UIApplicationShortcutItem+Utility.h
//  Enterprise
//
//  Created by Alex Koller on 10/1/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIUserRental.h"

extern NSString * const EHIShortcutVersionUserInfoKey;

@interface UIApplicationShortcutItem (Utility)

+ (instancetype)itemForLocation:(EHILocation *)location;

+ (instancetype)itemForRental:(EHIUserRental *)rental;

@end
