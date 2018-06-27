//
//  UIApplication+Shortcuts.h
//  Enterprise
//
//  Created by Alex Koller on 10/1/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "EHIUserRental.h"

NS_ASSUME_NONNULL_BEGIN

extern NSString * const EHIShortcutTypeSearchLocationsKey;
extern NSString * const EHIShortcutTypeNearbyLocationsKey;
extern NSString * const EHIShortcutTypeLocationDetailsKey;
extern NSString * const EHIShortcutTypeRentalDetailsKey;

@interface UIApplication (Shortcuts)

/**
 @brief Adds a shortcut to the given rental's details screen
 
 Creates a shortcut item for @c rental and adds it to the top
 of all current dynamic shortcut items. There can only be 1 rental
 shortcut at any given time. As a result, any previous existing rental
 shortcuts will be removed in the process.
 */

+ (void)addRentalShortcut:(EHIUserRental *)rental;

/**
 @brief Adds a shortcut to the given location's details screen
 
 Creates a shortcut item for @c location and adds it to the list of
 dynamic shortcut items. If the location shortcut already exists, it
 is moved above all other location shortcuts. Location shortcuts are
 placed below rental shortcuts.
*/

+ (void)addLocationShortcut:(EHILocation *)location;

/**
 @brief Removes all shortcuts of the given type
 
 Removes all dynamic shortcuts whose type is equal to
 @c type.
*/

+ (void)removeShortcutsWithType:(NSString *)type;

/**
 @brief Removes all shortcuts
 
 All dynamic shortcuts are removed for this app.
*/

+ (void)removeAllShortcuts;

@end

NS_ASSUME_NONNULL_END