//
//  EHINotificationSettingsViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 11/30/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSUInteger, EHINotificationSettingsType) {
    EHINotificationSettingsTypePickup,
    EHINotificationSettingsTypeReturn
};

@interface EHINotificationSettingsViewModel : EHIViewModel <MTRReactive>

@property (copy, nonatomic, readonly) NSString *title;
@property (copy, nonatomic, readonly) NSArray *options;

- (instancetype)initWithType:(EHINotificationSettingsType)type;
- (void)selectOptionAtIndex:(NSUInteger)index;

@end

NS_ASSUME_NONNULL_END