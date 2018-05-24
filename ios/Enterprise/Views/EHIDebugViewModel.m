//
//  EHIDebugViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 11/24/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIDebugViewModel.h"
#import "EHIDebugOptionViewModel.h"
#import "EHISettings.h"
#import "EHIUserManager.h"
#import "EHINotificationManager+Private.h"
#import "EHIToastManager.h"
#import "EHISurvey.h"
#import "NSDate+Formatting.h"
#import "EHIInfoModalViewModel.h"

@interface EHIDebugViewModel () <EHIUserListener>

@end

@implementation EHIDebugViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title = @"Debug Options";
        
        [[EHIUserManager sharedInstance] addListener:self];
    }
    
    return self;
}

# pragma mark - EHIUserListener

- (void)manager:(EHIUserManager *)manager didChangeAuthenticationForUser:(EHIUser *)user
{
    [self invalidateViewModels];
}

# pragma mark - Actions

- (void)selectItem:(NSUInteger)item
{
    EHIDebugOptionViewModel *viewModel = self.viewModels[item];
    
    switch(viewModel.type) {
        case EHIDebugOptionTypeStringBehavior:
            [self promptStringBehavior]; break;
        case EHIDebugOptionTypeEnvironment:
            [self promptEnvironment]; break;
        case EHIDebugOptionTypeSearchEnvironment:
            [self promptSearchEnvironment]; break;
        case EHIDebugOptionTypeInvalidateAuthToken:
            [self invalidateAuthToken]; break;
        case EHIDebugOptionTypeWrongApiKey:
            [self toggleForceWrongApiKey]; break;
        case EHIDebugOptionTypeMap:
            [self showDebugMap]; break;
        case EHIDebugOptionTypeOfficeGeofence:
            [self promptOfficeGeofence]; break;
        case EHIDebugOptionTypeNotifications:
            [self showNotificationSelector]; break;
        case EHIDebugOptionTypeWeekendSpecial:
            [self promptDeleteCurrentPromoCode]; break;
        case EHIDebugOptionTypeAnalyticsReminder:
            [self makeAnalyticsBePresented]; break;
        case EHIDebugOptionTypeInAppAppleStoreRate:
        case EHIDebugOptionTypeAppleStoreRate:
            [self resetAppReviewType:viewModel.type]; break;
        case EHIDebugOptionTypePrepayNABanner:
            [self resetPrepayNABanner]; break;
        case EHIDebugOptionTypeSurveyPooling:
            [self toggleSurveySkipPooling]; break;
        case EHIDebugOptionTypeSurveyResetState:
            [self resetSurveyState]; break;
        case EHIDebugOptionTypeRewardsBenefitsState:
            [self resetSavedTier]; break;
        case EHIDebugOptionTypeLocationMapFilterTip:
            [self resetFilterTip]; break;
        case EHIDebugOptionTypePushNotificationEvent:
            [self sendPushNotificationEvent]; break;
        case EHIDebugOptionTypeIssuingAuthorityRequiredMock:
            [self forceIssuingAuthority]; break;
        case EHIDebugOptionTypeUnauthJoinModal:
            [self resetUnauthJoinModal]; break;
        case EHIDebugOptionTypeGDPRState:
            [self showGDPRStatusesModal]; break;
        case EHIDebugOptionTypeClearData:
            [self clearData]; break;
    }
}

- (void)promptStringBehavior
{
    EHIAlertViewBuilder *alertView = EHIAlertViewBuilder.new
        .title(@"Set Localized String Behavior")
        .message(@"How should localized strings and keys behave?");

    @(0).upTo(EHIStringBehaviorObscureAllKeys).each(^(NSNumber *number, int index) {
        alertView.button([EHILocalization nameForStringBehavior:index]);
    });
    
    alertView.cancelButton(@"Cancel");
    
    alertView.show(^(NSInteger index, BOOL canceled) {
        if(!canceled) {
            [EHILocalization setStringBehavior:index];
            
            EHIAlertViewBuilder *exitAlert = EHIAlertViewBuilder.new
                .title(@"The app will now exit")
                .message(@"For changes to take effect, reopen the app.")
                .cancelButton(@"Exit");
            
            exitAlert.show(^(NSInteger index, BOOL caneled) {
                exit(0);
            });
        }
    });
}

- (void)promptEnvironment
{
    [[EHIConfiguration configuration] showEnvironmentSelectionAlertWithCompletion:^{
        [self invalidateViewModels];
    }];
}

- (void)promptSearchEnvironment
{
    [[EHIConfiguration configuration] showSearchEnvironmentSelectionAlertWithCompletion:^{
        [self invalidateViewModels];
    }];
}

- (void)invalidateAuthToken
{
    EHIUser *user;
    [[EHIUserManager sharedInstance].currentUser updateWithDictionary:@{
        @key(user.authorizationToken) : EHIDebugOptionInvalidAuthToken,
    }];
    
    [self invalidateViewModels];
}

- (void)toggleForceWrongApiKey
{
    NSString *nextStatus = EHISettings.sharedInstance.forceWrongApiKey ? @"off" : @"on";
    NSString *message    = [NSString stringWithFormat:@"Do you want to turn %@ wrong API key?", nextStatus];
    EHIAlertViewBuilder *alertView = EHIAlertViewBuilder.new
        .title(@"Force wrong API key")
        .message(message)
        .button(@"Sure thing!")
        .cancelButton(@"Cancel");
    
    alertView.show(^(NSInteger index, BOOL canceled) {
        if(!canceled) {
            EHISettings.sharedInstance.forceWrongApiKey = !EHISettings.sharedInstance.forceWrongApiKey;
            
            [self invalidateViewModels];
        }
    });
}

- (void)showDebugMap
{
    self.router.transition
        .push(EHIScreenDebugMap).start(nil);
}

- (void)promptOfficeGeofence
{
    EHIAlertViewBuilder *alertView = EHIAlertViewBuilder.new
        .title(@"Monitor Office")
        .button([NSString stringWithFormat:@"Enable Pickup Radius (%dm)", EHIGeofencingPickupRadius])
        .button([NSString stringWithFormat:@"Enable Return Radius (%dm)", EHIGeofencingReturnRadius])
        .cancelButton(@"Disable");
    
    alertView.show(^(NSInteger index, BOOL canceled) {
        // always cancel old
        [[EHINotificationManager sharedInstance] removePendingNotificationRequestsMatching:^BOOL(UNNotificationRequest *notification) {
            return notification.content.userInfo[EHIDebugOptionOfficeGeofenceNotificationKey] != nil;
        }];

        if(!canceled) {
            CLLocationCoordinate2D officeCenter = CLLocationCoordinate2DMake(41.8848549, -87.6221041);
            CLLocationDistance radius = index == 0 ? EHIGeofencingPickupRadius : EHIGeofencingReturnRadius;

            CLCircularRegion *region = [[CLCircularRegion alloc] initWithCenter:officeCenter radius:radius identifier:EHIDebugOptionOfficeGeofenceNotificationKey];
            region.notifyOnExit = NO;
            
            UNMutableNotificationContent *content = [UNMutableNotificationContent new];
            content.sound    = UNNotificationSound.defaultSound;
            content.title    = @"Chicago Office Entered";
            content.body     = @"You just entered the debugging Chicago Office Geofence! This notification will continue to trigger until explcitily turned off int he debug options.";
            content.userInfo = @{
                EHIDebugOptionOfficeGeofenceNotificationKey : [NSUUID UUID].UUIDString
            };
            
            UNLocationNotificationTrigger *trigger = [UNLocationNotificationTrigger triggerWithRegion:region repeats:NO];
            UNNotificationRequest *request = [UNNotificationRequest requestWithIdentifier:EHIDebugOptionOfficeGeofenceNotificationKey
                                                                                  content:content
                                                                                  trigger:trigger];
            
            [self addRequestForLocationNotification:request];
        }
        
        [self invalidateViewModels];
    });
}

- (void)showNotificationSelector
{
    EHIAlertViewBuilder *alertView = EHIAlertViewBuilder.new
        .title(@"Fire A Notification")
        .message(@"The notification will appear when you leave the app");
    
    NSArray *categories = [UNNotificationCategory notificationCategories] ?: @[];
    categories.each(^(UNNotificationCategory *category) {
        alertView.button(category.identifier);
    });
    
    alertView.cancelButton(@"Cancel");
    
    alertView.show(^(NSInteger index, BOOL canceled) {
        [self handleNotificationAlert:canceled index:index];
    });
}

- (void)handleNotificationAlert:(BOOL)canceled index:(NSInteger)index
{
    NSArray *categories = [UNNotificationCategory notificationCategories] ?: @[];
    
    if(!canceled) {
        UNNotificationRequest *backgroundNotification = nil;
        UNNotificationCategory *category = [categories ehi_safelyAccess:index];
        
        EHIUser *user = [EHIUser currentUser];
        
        EHIUserRental *rental;
        if ([category.identifier isEqualToString:EHINotificationCategoryCurrent]) {
            rental = user.currentRentals.all.firstObject;
        }
        else if ([category.identifier isEqualToString:EHINotificationCategoryUpcoming]) {
            rental = user.upcomingRentals.all.firstObject;
        }
        else {
            rental = user.currentRentals.all.firstObject ?: user.upcomingRentals.all.firstObject;
        }
        
        if (!rental) {
            [EHIToastManager showMessage:@"No rental available for test notification!"];
            return;
        }
        
        if([category.identifier isEqualToString:EHINotificationCategoryCurrent]) {
            backgroundNotification = [UNNotificationRequest notificationForCurrentRental:rental debug:YES];
        } else if([category.identifier isEqualToString:EHINotificationCategoryUpcoming]) {
            backgroundNotification = [UNNotificationRequest notificationForUpcomingRental:rental debug:YES];
        } else if([category.identifier isEqualToString:EHINotificationCategoryAfterHours]) {
            backgroundNotification = [UNNotificationRequest notificationForGeonotification:[EHIGeonotification geonotificationForAfterHoursRental:rental]];
        } else if([category.identifier isEqualToString:EHINotificationCategoryWayfinding]) {
            backgroundNotification = [UNNotificationRequest notificationForGeonotification:[EHIGeonotification geonotificationForWayfindingRental:rental]];
        }
        
        [self addRequestForLocationNotification:backgroundNotification];
    }
}

- (void)promptDeleteCurrentPromoCode
{
    EHIAlertViewBuilder *alertView = EHIAlertViewBuilder.new
    .title(@"Delete saved promotion code?")
    .button(@"Do it!")
    .button(@"Do it! And clean first run flags")
    .cancelButton(@"Nah, who cares...");
    
    alertView.show(^(NSInteger index, BOOL canceled) {
        if(!canceled) {
            [EHISettings clearPromotionCode];
            if(index == 1) {
                [EHISettings clearFirstRunFlag];
            }
            [self invalidateViewModels];
        }
    });
}

- (void)makeAnalyticsBePresented
{
    EHIAlertViewBuilder *alertView = EHIAlertViewBuilder.new
    .title(@"Want to see the analytics reminder?\nAfter that, you should see the modal screen as soon as you land on the Dashboard.")
    .button(@"Oui!")
    .cancelButton(@"Non, merci");
    
    alertView.show(^(NSInteger index, BOOL canceled) {
        if(!canceled) {
            [EHISettings makeAnalyticsBePresented];
            [self invalidateViewModels];
        }
    });
}

- (void)resetAppReviewType:(EHIDebugOptionType)reviewType;
{
    BOOL isInAppReview = reviewType == EHIDebugOptionTypeInAppAppleStoreRate;
    EHIAlertViewBuilder *alertView = EHIAlertViewBuilder.new
    .title(@"What do you want to do?")
    .button(@"Reset Confirmation Count")
    .button(isInAppReview ? @"Change in app review debug state" : @"Reset Apple Store rate state")
    .cancelButton(@"Cancel");

    alertView.show(^(NSInteger index, BOOL canceled) {
        if(!canceled) {
            switch (index) {
                case 0:
                    [EHISettings resetConfirmationViewCount];
                    break;
                case 1:
                    if(isInAppReview) {
                        [EHISettings toggleDebugingInAppReview];
                    } else {
                        [EHISettings resetPresentedAppStoreRate];
                    }
                    break;
                default: break;
            }
            [self invalidateViewModels];
        }
    });
}

- (void)resetPrepayNABanner
{
    EHIAlertViewBuilder *alertView = EHIAlertViewBuilder.new
    .title(@"Reset Prepay NA banner status?")
    .button(@"Yes")
    .cancelButton(@"No");
    
    alertView.show(^(NSInteger index, BOOL canceled) {
        if(!canceled && index == 0) {
            [EHISettings resetPrepayBanner];
        }
        [self invalidateViewModels];
    });
}

- (void)toggleSurveySkipPooling
{
    EHIAlertViewBuilder *alertView = EHIAlertViewBuilder.new
    .title(@"Switch pooling state?")
    .button(@"Yes")
    .cancelButton(@"No");
    
    alertView.show(^(NSInteger index, BOOL canceled) {
        BOOL skip = !canceled;
        [[EHISettings sharedInstance] setSkipSurveyPoolingCheck:skip];
        [[EHISurvey sharedInstance] setSkipPoolingCheck:skip];
        
        [self invalidateViewModels];
    });
}

- (void)resetSurveyState
{
    EHIAlertViewBuilder *alertView = EHIAlertViewBuilder.new
    .title(@"Reset survey state?")
    .button(@"Yes")
    .cancelButton(@"No");
    
    alertView.show(^(NSInteger index, BOOL canceled) {
        if(!canceled) {
            [[EHISurvey sharedInstance] resetSurveyState];
        }
    });
}

- (void)resetSavedTier
{
    EHIAlertViewBuilder *alertView = EHIAlertViewBuilder.new
    .title(@"Wipe current tier?")
    .button(@"Yes")
    .cancelButton(@"No");
    
    alertView.show(^(NSInteger index, BOOL canceled) {
        if(!canceled) {
            EHIUserLoyalty *loyalty = [EHIUser currentUser].profiles.basic.loyalty;
            [EHISettings resetTierWithLoyalty:loyalty];
            
            [self invalidateViewModels];
        }
    });
}

- (void)forceIssuingAuthority
{
    BOOL isForced = [EHISettings shouldForceIssuingAuthorityRequired];
    [EHISettings setForceIssuingAuthorityRequired:!isForced];
    [self invalidateViewModels];
}

- (void)resetFilterTip
{
    EHIAlertViewBuilder *alertView = EHIAlertViewBuilder.new
        .title(@"Reset filter tip flag?")
        .button(@"Yes")
        .cancelButton(@"No");
    
    alertView.show(^(NSInteger index, BOOL canceled) {
        if(!canceled) {
            [EHISettings resetLocationsMapFilterTip];
            
            [self invalidateViewModels];
        }
    });
}

- (void)resetUnauthJoinModal
{
    EHIAlertViewBuilder *alertView = EHIAlertViewBuilder.new
        .title(@"Reset unauth join flag?")
        .button(@"Yes")
        .cancelButton(@"No");
    
    alertView.show(^(NSInteger index, BOOL canceled) {
        if(!canceled) {
            [EHISettings resetShowJoinModal];
            
            [self invalidateViewModels];
        }
    });
}

- (void)showGDPRStatusesModal
{
    NSDictionary *status = [EHIAnalytics optOutStatus];
    NSString *details = status.map((id)^(id name, id optOutStatus){
        return [NSString stringWithFormat:@"%@ %@", name, [optOutStatus boolValue] ? @"\u274C" : @"\u2705"];
    }).sort.join(@"\n");
    
    EHIInfoModalViewModel *modal = EHIInfoModalViewModel.new;
    modal.title   = @"SDK Statuses";
    modal.details = details;
    
    [modal present:nil];
}

- (void)clearData
{
    EHIAlertViewBuilder *alertView = EHIAlertViewBuilder.new
    .title(@"Are you sure you want to reset all flags?")
    .button(@"Yes")
    .cancelButton(@"No");
    
    alertView.show(^(NSInteger index, BOOL canceled) {
        if(!canceled) {
            [EHISettings resetUserDefaults];
            
            [self invalidateViewModels];
        }
    });
}

- (void)sendPushNotificationEvent
{
    if ([DEVICE_ID length] > 4) {
        NSString *deviceId = [DEVICE_ID substringToIndex:4];
        [EHIToastManager showMessage:[NSString stringWithFormat:@"Push notification event sent, DEVICE_ID is %@", deviceId]];
        [EHIAnalytics trackAction:EHIAnalyticsDebugMenuPushNotificationEvent handler:^(EHIAnalyticsContext *context) {
            context[EHIAnalyticsDebugKey] = deviceId;
        }];
    }
}

//
// Helpers
//

- (void)invalidateViewModels
{
    NSMutableIndexSet *filterItems = [NSMutableIndexSet new];

    // don't worry about auth token when unauthed
    if(![EHIUser currentUser]) {
        [filterItems addIndex:EHIDebugOptionTypeInvalidateAuthToken];
    }
    
    // only allow notifications when authed with notifications enabled
    if(![EHIUser currentUser] || ![[EHINotificationManager sharedInstance] isRegisteredForNotifications]) {
        [filterItems addIndex:EHIDebugOptionTypeNotifications];
    }
    
    // disable weekend special cell if we don't have anything
    NSString *currentPromoCode = [EHISettings savedPromotionCode];
    if(!currentPromoCode) {
        [filterItems addIndex:EHIDebugOptionTypeWeekendSpecial];
    }
    
    // disable analytics when COR is not France
    if(![NSLocale ehi_shouldShowDataCollectionReminder]) {
        [filterItems addIndex:EHIDebugOptionTypeAnalyticsReminder];
    }
    
    // disable prepay NA section when country is invalid
    if(![NSLocale ehi_shouldShowPrepayBanner]) {
        [filterItems addIndex:EHIDebugOptionTypePrepayNABanner];
    }
    
    self.viewModels = [EHIDebugOptionViewModel viewModels].select(^(EHIDebugOptionViewModel *viewModel) {
        return ![filterItems containsIndex:viewModel.type];
    });
}

- (void)addRequestForLocationNotification:(UNNotificationRequest *)notification
{
    [[EHINotificationManager sharedInstance] requestLocalNotification:notification];
}

@end
