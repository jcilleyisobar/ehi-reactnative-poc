//
//  EHINotificationManager.m
//  Enterprise
//
//  Created by fhu on 11/20/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

@import Localytics;

#import "EHINotificationManager+Private.h"
#import "EHINotificationManager.h"
#import "EHIInfoModalViewModel.h"
#import "EHITransitionManager.h"
#import "EHIDataStore.h"
#import "EHIServices+Location.h"
#import "EHISettings.h"
#import "EHIUser.h"
#import "EHICacheLocation.h"
#import "EHIAnalytics.h"

#define EHINotificationSystemAlertShownKey @"EHINotificationSystemAlertShownKey"

@interface EHINotificationManager () <UNUserNotificationCenterDelegate>
@property (copy, nonatomic) EHINotificationEnableHandler completion;
@end

@implementation EHINotificationManager

+ (instancetype)sharedInstance
{
    static EHINotificationManager *instance;
    static dispatch_once_t onceToken;
    
    dispatch_once(&onceToken, ^{
        instance = [EHINotificationManager new];
    });
    
    return instance;
}

- (instancetype)init
{
    if(self = [super init]) {
        self.center.delegate = self;
    }
    
    return self;
}

# pragma mark - Launch

+ (void)prepareToLaunch
{
    EHINotificationManager *instance = [self sharedInstance];
    
    [instance.center setNotificationCategories:UNNotificationCategory.allCategories];
    (instance.pendingNotifications ?: @[]).each(^(UNNotificationRequest *request){
        [instance handleLocalNotification:request.content];
    });
    
    //register for remote notifications (ask for token)
    dispatch_async(dispatch_get_main_queue(), ^{
        [[UIApplication sharedApplication] registerForRemoteNotifications];
    });
}

# pragma mark - Notification Handling

- (void)handleLocalNotification:(UNNotificationContent *)notification
{
    [Localytics didReceiveNotificationResponseWithUserInfo:notification.userInfo];
    // did select notification while in background
    if([UIApplication sharedApplication].applicationState != UIApplicationStateActive) {
        [self performRoutingForNotification:notification];
    }
    // received notification while in foreground
    else {
        [EHIAlertViewBuilder showWithNotification:notification];
    }
}

- (void)performRoutingForNotification:(UNNotificationContent *)notification
{
    [EHIAnalytics updateSessionSource:EHIAnalyticsActionNotificationDashboard];

    // always route to dashboard on selection (even for geonotifications)
    [EHITransitionManager transitionToScreen:EHIScreenDashboard asModal:NO];
}

- (void)requestLocalNotification:(UNNotificationRequest *)notification
{
    [self.center addNotificationRequest:notification withCompletionHandler:nil];
}

# pragma mark - Notification Actions

- (void)handleLocalNotification:(UNNotificationContent *)notification withActionIdentifier:(NSString *)identifier handler:(void (^)())handler
{
    if([identifier isEqualToString:EHINotificationActionCallBranch]) {
        [self callBranch:notification handler:handler];
    } else if([identifier isEqualToString:EHINotificationActionLocation]) {
        [self getDirections:notification handler:handler];
    } else if ([identifier isEqualToString:EHINotificationActionGasStations]) {
        [self findGasStations:notification handler:handler];
    } else if ([identifier isEqualToString:EHINotificationActionAfterHours]) {
        [self showAfterHoursInstructions:notification handler:handler];
    } else if ([identifier isEqualToString:EHINotificationActionWayfinding]) {
        [self getTerminalDirections:notification handler:handler];
    } else {
        ehi_call(handler)();
    }
}

- (void)callBranch:(UNNotificationContent *)notification handler:(void (^)())handler
{
    [EHIAnalytics updateSessionSource:EHIAnalyticsActionNotificationCallBranch];

    NSString *phoneNumber = [notification.userInfo valueForKey:EHINotificationRentalPickupLocationPhoneKey];
    
    dispatch_main_async(^{
        [UIApplication ehi_promptPhoneCall:phoneNumber];
    });
    
    ehi_call(handler)();
}

- (void)getDirections:(UNNotificationContent *)notification handler:(void (^)())handler
{
    [EHIAnalytics updateSessionSource:EHIAnalyticsActionNotificationGetDirections];

    EHILocation *location = [self locationFromNotification:notification];
    
    dispatch_main_async(^{
        [UIApplication ehi_openMapsWithLocation:location];
    });
    
    ehi_call(handler)();
}

- (void)findGasStations:(UNNotificationContent *)notification handler:(void (^)())handler
{
    [EHIAnalytics updateSessionSource:EHIAnalyticsActionNotificationFindGasStations];

    NSString *query = EHILocalizedString(@"rental_find_gas_search_query", @"gas", @"map app gas search query");
    EHILocation *location = [self locationFromNotification:notification];
    
    dispatch_main_async(^{
        [UIApplication ehi_openMapsWithSearchQuery:query atLocation:location];
    });
    
    ehi_call(handler)();
}

- (void)showAfterHoursInstructions:(UNNotificationContent *)notification handler:(void(^)())handler
{
    [EHIAnalytics updateSessionSource:EHIAnalyticsActionNotificationAfterHourInstructions];

    NSString *locationId  = notification.userInfo[EHINotificationRentalReturnLocationIdKey];
    EHILocation *location = [EHILocation modelWithDictionary:@{
        @key(location.uid) : locationId ?: @"",
    }];
    
    [[EHIServices sharedInstance] updateDetailsForLocation:location handler:^(EHILocation *location, EHIServicesError *error) {
        if(!error.hasFailed) {
            EHIInfoModalViewModel *viewModel = [[EHIInfoModalViewModel alloc] initWithModel:location.afterHoursPolicy];

            [EHITransitionManager transitionToScreen:EHIScreenInfoModal object:viewModel asModal:YES];
        }
        
        ehi_call(handler)();
    }];
}

- (void)getTerminalDirections:(UNNotificationContent *)notification handler:(void(^)())handler
{
    [EHIAnalytics updateSessionSource:EHIAnalyticsActionNotificationTerminalDirections];

    NSString *locationId  = notification.userInfo[EHINotificationRentalPickupLocationIdKey];
    EHILocation *location = [EHILocation modelWithDictionary:@{
        @key(location.uid) : locationId ?: @"",
    }];
    
    [[EHIServices sharedInstance] updateDetailsForLocation:location handler:^(EHILocation *location, EHIServicesError *error) {
        if(!error.hasFailed) {
            [EHITransitionManager transitionToScreen:EHIScreenLocationWayfinding object:location.wayfindings asModal:NO];
        }
        
        ehi_call(handler)();
    }];
}

//
// Helpers
//

- (EHILocation *)locationFromNotification:(UNNotificationContent *)notification
{
    NSString *localizedName     = notification.userInfo[EHINotificationRentalPickupLocationNameKey];
    CLLocationDegrees latitude  = [notification.userInfo[EHINotificationRentalPickupLocationLatitudeKey] doubleValue];
    CLLocationDegrees longitude = [notification.userInfo[EHINotificationRentalPickupLocationLongitudeKey] doubleValue];
    
    if(localizedName.length == 0 || latitude == 0 || longitude == 0) {
        return nil;
    }
    
    EHILocationCoordinate *coordinate;
    EHILocation *location = [EHILocation modelWithDictionary:@{
        @key(location.localizedName) : localizedName,
        @key(location.position)      : @{
            @key(coordinate.latitude)  : @(latitude),
            @key(coordinate.longitude) : @(longitude),
        },
    }];
    
    return location;
}

# pragma mark - Registration

- (void)promptRegistrationIfNeeded:(void (^)(BOOL shouldNotify))handler
{
    if([self isRegisteredForNotifications] || [self hasShownSystemNotificationAlert]) {
        ehi_call(handler)(YES);
        return;
    }
    
    EHIInfoModalViewModel *model = [EHIInfoModalViewModel new];
    model.title             = EHILocalizedString(@"notifications_prompt_title", @"GET NOTIFIED ABOUT YOUR RESERVATION", @"");
    model.details           = EHILocalizedString(@"notifications_prompt_content", @"By opting in, you will receive notifications for when to pick up and return your vehicle. You can change your notification settings in your App Settings.", @"");
    model.firstButtonTitle  = EHILocalizedString(@"notification_prompt_accept", @"NOTIFY ME", @"");
    model.secondButtonTitle = EHILocalizedString(@"notifications_prompt_deny", @"NOT NOW", @"");
    
    __weak typeof(model) wodel = model;
    [model present:^BOOL(NSInteger index, BOOL canceled) {
        BOOL shouldNotify = index == 0;
        
        if(shouldNotify) {
            [self registerForNotifications];
        }
        
        // call handler after dismissal
        [wodel dismissWithCompletion:^{
            ehi_call(handler)(shouldNotify);
        }];
        
        [EHIAnalytics trackAction:shouldNotify ? EHIAnalyticsActionNotificationAllow : EHIAnalyticsActionNotificationDontAllow handler:nil];
        
        return NO;
    }];
    
    [EHIAnalytics trackState:^(EHIAnalyticsContext * _Nonnull context) {
        context.state = EHIAnalyticsNotificationState;
    }];
}

- (void)registerForNotifications
{
    __block BOOL outerGranted = NO;
    ehi_dispatch_sync(^(EHIDispatchSyncCompletionBlock completion) {
        UNAuthorizationOptions options = UNAuthorizationOptionAlert | UNAuthorizationOptionBadge | UNAuthorizationOptionSound;
        [self.center requestAuthorizationWithOptions:options
                                   completionHandler:^(BOOL granted, NSError * _Nullable error) {
            [Localytics didRequestUserNotificationAuthorizationWithOptions:options granted:granted];
            outerGranted = granted;
            [[NSUserDefaults standardUserDefaults] setBool:YES forKey:EHINotificationSystemAlertShownKey];
            ehi_call(completion)();
        }];
    });
    
    if(outerGranted) {
        //register for remote notifications (ask for token)
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication] registerForRemoteNotifications];
        });
        
        [self scheduleRentalNotificationsForUser:[EHIUser currentUser]];
    }

    ehi_call(self.completion)(outerGranted);

    // dispose completion
    self.completion = nil;
}

- (void)registerForNotificationsWithDefaults:(EHINotificationEnableHandler)completion
{
    self.completion = [completion copy];

    [self registerForNotifications];
    
    [EHISettings sharedInstance].currentRentalReminderTime  = EHIRentalReminderTimeTwoHours;
    [EHISettings sharedInstance].upcomingRentalReminderTime = EHIRentalReminderTimeTwoHours;
    [EHISettings sharedInstance].useRentalAssistant         = YES;

}

- (BOOL)shouldPromptNotifications
{
    return !self.hasShownSystemNotificationAlert && !self.isRegisteredForNotifications;
}

- (BOOL)hasShownSystemNotificationAlert
{
    return [[NSUserDefaults standardUserDefaults] boolForKey:EHINotificationSystemAlertShownKey];
}

- (BOOL)isRegisteredForNotifications
{
    __block BOOL isRegistered;

    ehi_dispatch_sync(^(EHIDispatchSyncCompletionBlock completion) {
        [self.center getNotificationSettingsWithCompletionHandler:^(UNNotificationSettings * _Nonnull settings) {
            isRegistered = settings.alertSetting == UNNotificationSettingEnabled;
            ehi_call(completion)();
        }];
    });

    return isRegistered;
}

# pragma mark - Creation

- (void)scheduleRentalNotificationsForUser:(EHIUser *)user
{
    // always wipe when attempting to modify notifications
    [self clearRentalNotifications];
    
    // don't make notifications if we're not registered or we have no rentals
    if(![self isRegisteredForNotifications] || (user.currentRentals.all.count == 0 && user.upcomingRentals.all.count == 0)) {
        return;
    }

    [self scheduleNotificationsForCurrentRentals:user.currentRentals.all upcomingRentals:user.upcomingRentals.all];
}
       
- (void)scheduleNotificationsForCurrentRentals:(NSArray *)currentRentals upcomingRentals:(NSArray *)upcomingRentals
{
    BOOL ignoreCurrent  = [EHISettings sharedInstance].currentRentalReminderTime  == EHIRentalReminderTimeNone;
    BOOL ignoreUpcoming = [EHISettings sharedInstance].upcomingRentalReminderTime == EHIRentalReminderTimeNone;

    // prune rentals that have invalid data
    currentRentals = ignoreCurrent ? @[] : (currentRentals ?: @[]).select(^(EHIUserRental *rental) {
        return rental.returnLocation.uid != nil && rental.returnLocation.timeZoneId != nil;
    });
    upcomingRentals = ignoreUpcoming ? @[] : (upcomingRentals ?: @[]).select(^(EHIUserRental *rental) {
        return rental.pickupLocation.uid != nil && rental.pickupLocation.timeZoneId != nil;
    });
    
    // create notifications from valid rentals
    NSArray *current = currentRentals.map(^(EHIUserRental *rental) {
        return [UNNotificationRequest notificationForCurrentRental:rental];
    });
    NSArray *upcoming = upcomingRentals.map(^(EHIUserRental *rental) {
        return [UNNotificationRequest notificationForUpcomingRental:rental];
    });
    
    @[current, upcoming].flatten.each(^(UNNotificationRequest *request) {
        [self.center addNotificationRequest:request withCompletionHandler:nil];
    });
}

# pragma mark - Removal

- (void)clearRentalNotifications
{
    [self removePendingNotificationRequestsMatching:^BOOL(UNNotificationRequest *notification) {
        NSString *identifier = notification.content.categoryIdentifier;
        return [identifier isEqualToString:EHINotificationCategoryCurrent]
            || [identifier isEqualToString:EHINotificationCategoryUpcoming];
    }];
}

# pragma mark - UNUserNotificationCenterDelegate

- (void)userNotificationCenter:(UNUserNotificationCenter *)center willPresentNotification:(UNNotification *)notification withCompletionHandler:(void (^)(UNNotificationPresentationOptions))completionHandler
{
    __block UNNotificationPresentationOptions options = UNNotificationPresentationOptionNone;
    
    ehi_dispatch_sync(^(EHIDispatchSyncCompletionBlock completion) {
        [self.center getNotificationSettingsWithCompletionHandler:^(UNNotificationSettings * _Nonnull settings) {
            ehi_call(completion)();
        }];
    });
    
    UNNotificationContent *content = notification.request.content;
    
    [self handleLocalNotification:content];
    
    ehi_call(completionHandler)(options);
}

- (void)userNotificationCenter:(UNUserNotificationCenter *)center didReceiveNotificationResponse:(UNNotificationResponse *)response withCompletionHandler:(void(^)(void))completionHandler
{
    UNNotificationContent *content = response.notification.request.content;
    
    [self handleLocalNotification:content];

    ehi_call(completionHandler)();
}

@end
