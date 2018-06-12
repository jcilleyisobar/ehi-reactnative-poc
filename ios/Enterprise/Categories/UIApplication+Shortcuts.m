//
//  UIApplication+Shortcuts.m
//  Enterprise
//
//  Created by Alex Koller on 10/1/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "UIApplication+Shortcuts.h"

NSString * const EHIShortcutTypeSearchLocationsKey = @"ShortcutTypeSearchLocations";
NSString * const EHIShortcutTypeNearbyLocationsKey = @"ShortcutTypeNearbyLocations";
NSString * const EHIShortcutTypeLocationDetailsKey = @"ShortcutTypeLocationDetails";
NSString * const EHIShortcutTypeRentalDetailsKey   = @"ShortcutTypeRentalDetails";

@implementation UIApplication (Shortcuts)

# pragma mark - Creation

+ (void)addRentalShortcut:(EHIUserRental *)rental
{
    // only allow 1 rental shortcut
    [self removeShortcutsWithType:EHIShortcutTypeRentalDetailsKey];
    
    // create new rental shortcut
    UIApplicationShortcutItem *shortcut = [UIApplicationShortcutItem itemForRental:rental];
    
    // insert shortcut in front of list
    NSMutableArray *shortcuts = [self.sharedApplication.shortcutItems mutableCopy];
    self.sharedApplication.shortcutItems = @[shortcut].concat(shortcuts);
}

+ (void)addLocationShortcut:(EHILocation *)location
{
    UIApplicationShortcutItem *shortcut = [self existingShortcutWithUid:location.uid type:EHIShortcutTypeLocationDetailsKey];
    
    // prepare to move to top
    if(shortcut != nil) {
        [self removeExistingShortcut:shortcut];
    }
    // create new shortcut
    else {
        shortcut = [UIApplicationShortcutItem itemForLocation:location];
    }
    
    // insert shortcut at appropriate position
    NSMutableArray *shortcuts = [self.sharedApplication.shortcutItems mutableCopy];
    UIApplicationShortcutItem *rentalItem = shortcuts.find(^(UIApplicationShortcutItem *item) {
        return [item.type isEqualToString:EHIShortcutTypeRentalDetailsKey];
    });
    
    // if no rental item, insert at front
    if(!rentalItem) {
        self.sharedApplication.shortcutItems = @[shortcut].concat(shortcuts);
    }
    // otherwise, insert after rental
    else {
        NSUInteger rentalIndex = shortcuts.indexOf(rentalItem);
        [shortcuts insertObject:shortcut atIndex:rentalIndex + 1];
        
        self.sharedApplication.shortcutItems = shortcuts;
    }
}

//
// Helpers
//

+ (UIApplicationShortcutItem *)existingShortcutWithUid:(NSString *)uid type:(NSString *)type
{
    return (self.sharedApplication.shortcutItems ?: @[]).find(^(UIApplicationShortcutItem *item) {
        // only check for certain type
        if(![item.type isEqualToString:type]) {
            return NO;
        }
        
        EHIModel *model;
        id shortcutUid = item.userInfo[@key(model.uid)];
        return [uid isEqualToString:shortcutUid];
    });
}

# pragma mark - Removal

+ (void)removeShortcutsWithType:(NSString *)type
{
    // select all that don't match type
    NSArray *newShortcuts = (self.sharedApplication.shortcutItems ?: @[]).select(^(UIApplicationShortcutItem *item) {
        return ![item.type isEqualToString:type];
    });
    
    self.sharedApplication.shortcutItems = newShortcuts;
}

+ (void)removeExistingShortcut:(UIApplicationShortcutItem *)shortcut
{
    NSMutableArray *shortcuts = [self.sharedApplication.shortcutItems mutableCopy];
    [shortcuts removeObject:shortcut];
    
    self.sharedApplication.shortcutItems = shortcuts;
}

+ (void)removeAllShortcuts
{
    self.sharedApplication.shortcutItems = nil;
}

@end
