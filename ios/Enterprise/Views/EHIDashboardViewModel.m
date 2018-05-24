//
//  EHIDashboardViewModel.m
//  Enterprise
//
//  Created by Ty Cobb on 1/9/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIDashboardViewModel.h"
#import "EHIInfoModalViewModel.h"
#import "EHIHistoryManager.h"
#import "EHIFavoritesManager.h"
#import "EHIUserManager.h"
#import "EHIReservationBuilder+Analytics.h"
#import "EHIServices+Location.h"
#import "EHINotificationManager.h"
#import "EHISettings.h"
#import "EHISurvey.h"
#import "EHIGeofenceManager.h"
#import "EHIToastManager.h"

@interface EHIDashboardViewModel () <EHIUserListener>
@property (strong, nonatomic) EHIModel *heroImageModel;
@property (strong, nonatomic) EHIModel *contentModel;
@property (strong, nonatomic) EHIModel *loyaltyPromptModel;
@property (assign, nonatomic) EHIContentSectionType contentType;
@property (copy  , nonatomic) NSArray *quickstartModels;
@property (copy  , nonatomic) NSString *clearActivityTitle;
@property (assign, nonatomic) BOOL showsHistoryFallback;
@property (nonatomic, readonly) EHIReservationBuilder *builder;
@end

@implementation EHIDashboardViewModel

- (instancetype)init
{
    if(self = [super init]) {
        _quickstartTitle = EHILocalizedString(@"dashboard_quickstart_title", @"QUICK START", @"Title for the dashboard quick start section");
        _loyaltyPromptModel = [EHIModel placeholder];
        
        // opt-in to side-effects for our initial state
        [self setQuickstartModels:nil];
        [self setContentModel:nil forType:EHIContentSectionTypeNone handler:nil];
    }
    
    return self;
}

- (void)didBecomeActive
{
    [super didBecomeActive];

    // listen for login updates
    [[EHIUserManager sharedInstance] addListener:self];
    // start listening to the application lifecycle
    [self registerForApplicationNotifications:YES];
    
    // load the quickstart models
    __weak typeof(self) welf = self;
    [self quickstartModelsWithHandler:^(NSArray *models) {
        welf.quickstartModels = models;
    }];
    
    [[EHIConfiguration configuration] onReady:^(BOOL isReady) {
        if(isReady) {
            [welf presentModals];
        }
    }];
}

- (void)presentModals
{
    BOOL shouldPresentAnalyticsReminder = [EHISettings shouldRemindAnalytics] || [EHISettings shouldShowGDPRModal];
    
    __weak __typeof(self) welf = self;
    void (^completionBlock)() = ^{
        [welf presentPromotionModalIfNeeded];
        [welf presentSurveyInviteIfNeeded];
    };
    if(shouldPresentAnalyticsReminder) {
        [self presentAnalyticsReminderIfNeeded:completionBlock];
    } else {
        completionBlock();
    }
}

- (void)presentAnalyticsReminderIfNeeded:(void (^)())completion
{
    [EHIAnalytics trackState:^(EHIAnalyticsContext * _Nonnull context) {
        context.screen = EHIAnalyticsDataCollectionReminderModalScreen;
        context.state = EHIAnalyticsDataCollectionReminderModalState;
    }];

    EHIInfoModalViewModel *model = [EHIInfoModalViewModel new];
    model.title             = EHILocalizedString(@"modal_analytics_reminder_title", @"Data Collection Reminder", @"");
    model.details           = EHILocalizedString(@"modal_analytics_reminder_details", @"In order to improve our services to you we...", @"");
    model.firstButtonTitle  = EHILocalizedString(@"modal_analytics_reminder_continue_button_title", @"CONTINUE", @"");
    model.secondButtonTitle = EHILocalizedString(@"modal_analytics_reminder_change_settings_button", @"CHANGE PRIVACY SETTINGS", @"");

    [model present:^BOOL(NSInteger index, BOOL canceled) {
        [EHISettings presentedAnalyticsReminder];
        [EHISettings didShowGDPRModal];
        if(!canceled && index == 1) {
            [self pushSettingsScreen];
            return YES;
        } else {
            [model dismissWithCompletion:completion];
            return NO;
        }
    }];
}

- (void)presentPromotionModalIfNeeded
{
    EHIPromotionContract *promo = [NSLocale ehi_country].weekendSpecial;
    self.promotionModel = promo ? [EHIDashboardPromotionCellViewModel new] : nil;
    BOOL shouldPresentModal = promo && [EHISettings shouldPresentPromotion:promo.code];
    if(shouldPresentModal) {
        [EHIAnalytics trackState:^(EHIAnalyticsContext * _Nonnull context) {
            context.screen = EHIAnalyticsWkndPromoScreenModal;
            context.state = EHIAnalyticsWkndPromoStateModal;
        }];
        
        EHIInfoModalViewModel *model = [[EHIInfoModalViewModel alloc] initWithModel:promo];
        model.firstButtonTitle  = EHILocalizedString(@"weekend_special_educational_dialog_get_started_but", @"GET STARTED", @"");
        model.secondButtonTitle = EHILocalizedString(@"weekend_special_educational_dialog_close_button_ti", @"CLOSE", @"");
        [model present:^BOOL(NSInteger index, BOOL canceled) {
            NSString *trackAction = index == 0 ? EHIAnalyticsWkndPromoModalActionLearnMore : EHIAnalyticsWkndPromoModalActionClose;
            [EHIAnalytics trackAction:trackAction handler:^(EHIAnalyticsContext * _Nonnull context) {
                context.screen = EHIAnalyticsWkndPromoScreenModal;
                context.state = EHIAnalyticsWkndPromoStateModal;
            }];

            if(index == 0 && !canceled) {
                [self pushPromotionDetails];
            }
            
            return YES;
        }];
        
        [EHISettings presentedPromotionWithCode:promo.code];
    }
}

- (void)presentSurveyInviteIfNeeded
{
    [EHISurvey showInviteIfNeeded];
}

- (void)didResignActive
{
    [super didResignActive];
    
    // stop listening for login updates
    [[EHIUserManager sharedInstance] removeListener:self];
    // stop listenting to application lifecycle 
    [self registerForApplicationNotifications:NO];
}

# pragma mark - Content

- (void)setContentModel:(id)contentModel forType:(EHIContentSectionType)type handler:(void (^)(void))handler
{
    [self updateLocationsDetailsForModel:contentModel handler:^{
        self.contentModel = contentModel;
        self.contentType  = type;

        // show the hero image if we have no content
        self.heroImageModel = (type == EHIContentSectionTypeNone) ? [EHIModel placeholder] : nil;
        
        ehi_call(handler)();
    }];
}

- (void)updateLocationsDetailsForModel:(id)model handler:(void (^)(void))handler
{
    if (!model || ![model isKindOfClass:[EHIUserRental class]]) {
        ehi_call(handler)();
        return;
    }
    
    EHIUserRental *userRental = (EHIUserRental *)model;
    EHILocation *pickupLocation = userRental.pickupLocation;
    EHILocation *returnLocation = userRental.returnLocation;
    
    // Services are not retrieving the airport code when the pickup location is an airport, so we have to call the locations endpoint
    BOOL isAirport = pickupLocation.type == EHILocationTypeAirport;
    
    //currently we are just ensuring that location details is called once per rental retrieve.
    //we are using airportCode to ensure that we only call it once for a location
    if (!isAirport || pickupLocation.airportCode.length) {
        ehi_call(handler)();
        return;
    }
    
    BOOL isOneWay = userRental.isOneWay;
    
    dispatch_group_t group = dispatch_group_create();
    
    dispatch_group_enter(group);
    [[EHIServices sharedInstance] updateDetailsForLocation:isOneWay ? returnLocation : pickupLocation
                                                   handler:^(EHILocation *location, EHIServicesError *error) {
        [error consume];
        dispatch_group_leave(group);
    }];
    
    dispatch_group_enter(group);
    [[EHIServices sharedInstance] updateHoursForLocation:isOneWay ? returnLocation :pickupLocation
                                                fromDate:[userRental.returnDate ehi_addDays:-1]
                                                  toDate:[userRental.returnDate ehi_addDays:1]
                                                 handler:^(EHILocation *location, EHIServicesError *error) {
        [error consume];
        dispatch_group_leave(group);
    }];
    
    dispatch_group_notify(group, dispatch_get_main_queue(), ^{
        ehi_call(handler)();
        return;
    });
}

# pragma mark - Quickstart

- (void)setQuickstartModels:(NSArray *)models
{
    // show clear footer if any past/abandoned rentals
    BOOL hasReservations = (models ?: @[]).any(^(id model) {
        return [model isKindOfClass:[EHIReservation class]];
    });
    
    self.clearActivityTitle = hasReservations
        ? EHILocalizedString(@"dashboard_quickstart_delete_title", @"CLEAR ACTIVITY", @"Title for the dashboard quick start clear button") : nil;
    
    // show the fallback if we don't find anything
    self.showsHistoryFallback = !models.count;
    if(self.showsHistoryFallback) {
        models = @[ [EHIModel placeholder] ];
    }
   
    _quickstartModels = models;
}

- (void)quickstartModelsWithHandler:(void(^)(NSArray *models))handler
{
    NSMutableArray *result = [NSMutableArray new];
   
    // fetch all these sequentially; ugly but makes it easier to preserve ordering
    // start with the abandoned rentals
    [[EHIHistoryManager sharedInstance] abandonedRentalsWithHandler:^(NSArray *rentals) {
        [result addObjectsFromArray:rentals];
        
        // then add the past rentals
        [[EHIHistoryManager sharedInstance] pastRentalsWithHandler:^(NSArray *rentals) {
            [result addObjectsFromArray:rentals];
            
            // then add the favorites, and call the handler
            [result addObjectsFromArray:[EHIFavoritesManager sharedInstance].favoriteLocations];
            ehi_call(handler)([result copy]);
        }];
    }];
}

# pragma mark - Selection

- (BOOL)shouldSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    return (indexPath.section == EHIDashboardSectionHeader && [EHIUser currentUser] != nil)
           || (indexPath.section == EHIDashboardSectionQuickstart && !self.showsHistoryFallback);
}

- (void)selectItemAtIndexPath:(NSIndexPath *)indexPath
{
    switch((EHIDashboardSection)indexPath.section) {
        case EHIDashboardSectionQuickstart:
            [self selectQuickstartItemAtIndexPath:indexPath]; break;
        default: break;
    }
}

//
// Helpers
//

- (void)selectQuickstartItemAtIndexPath:(NSIndexPath *)indexPath
{
    id model = [self modelAtIndexPath:indexPath];
  
    // if this is a location, we selected a favorite
    if([model isKindOfClass:[EHILocation class]]) {
        [self trackQuickstartLocation:model];
        [self.builder selectLocation:model];
    }
    // otherwise, this is a past/abandoned rental
    else if([model isKindOfClass:[EHIReservation class]]) {
        [self trackQuickstartReservation:model];
        [self.builder restartReservation:model];
    }
}

- (void)trackQuickstartLocation:(EHILocation *)location
{
    [EHIAnalytics trackAction:EHIAnalyticsDashActionFavorites handler:^(EHIAnalyticsContext *context) {
        [self.builder encodeLocationSelection:location context:context];
    }];
}

- (void)trackQuickstartReservation:(EHIReservation *)reservation
{
    // determine the correct action based on res type
    NSString *action = reservation.isPast ? EHIAnalyticsDashActionPast : EHIAnalyticsDashActionAbandonded;
    // and fire the analytics action
    [EHIAnalytics trackAction:action handler:^(EHIAnalyticsContext *context) {
        [context encodePickupLocation:reservation.pickupLocation returnLocation:reservation.returnLocation];
    }];
}

# pragma mark - EHIUserListener

- (void)manager:(EHIUserManager *)manager didChangeAuthenticationForUser:(EHIUser *)user
{
    // only show the prompt when we're not authenticated
    self.loyaltyPromptModel = [EHIUser currentUser] ? nil : [EHIModel placeholder];

    [self reloadRentals];
}

#pragma mark - Actions

- (void)acceptNotifications
{
    dispatch_group_t group = dispatch_group_create();

    dispatch_group_enter(group);
    [[EHINotificationManager sharedInstance] registerForNotificationsWithDefaults:^(BOOL allowed) {
        dispatch_group_leave(group);
    }];

    dispatch_group_enter(group);
    [[EHIGeofenceManager sharedInstance] enableGeofencingWithCompletion:^(BOOL allowed) {
        dispatch_group_leave(group);
    }];

    dispatch_group_notify(group, dispatch_get_main_queue(), ^{
        [self reloadRentals];

        [EHIToastManager showMessage:EHILocalizedString(@"notifications_accepted", @"Great, you'll receive the default notifications. Notifications can be adjusted anytime in App Settings.", @"")];
    });
}

- (void)denyNotifications
{
    // reload rentals after viewing one time notification prompt
    [self reloadRentals];
}

- (void)clearQuickstart
{
    [EHIAnalytics trackAction:EHIAnalyticsDashActionClear handler:nil];
    
    EHIAlertViewBuilder *alert = EHIAlertViewBuilder.new
        .title(EHILocalizedString(@"dashboard_clear_quickstart_alert_title", @"Remove?", @""))
        .message(EHILocalizedString(@"dashboard_clear_quickstart_alert_details", @"Do you want to remove all recent activity? (Your favorites will not be removed).", @""))
        .button(EHILocalizedString(@"dashboard_clear_quickstart_alert_remove", @"Remove", @""))
        .button(EHILocalizedString(@"dashboard_clear_quickstart_alert_keep", @"Keep", @""));
    
    alert.show(^(NSInteger index, BOOL canceled) {
        if(index == 0) {
            // clear quickstart and fetch any remaining models
            [[EHIHistoryManager sharedInstance] clearHistory];
            [self quickstartModelsWithHandler:^(NSArray *models) {
                self.quickstartModels = models;
            }];
            
            self.router.transition
                .dismiss.start(nil);
        }
    });
}

- (void)pushPromotionDetails
{
    [EHIAnalytics trackAction:EHIAnalyticsDashActionWkndSpecial handler:nil];

    self.router.transition.push(EHIScreenPromotionDetails).start(nil);
}

- (void)pushSettingsScreen
{
    self.router.transition
    .root(EHIScreenSettings).start(nil);
}

# pragma mark - Rentals

- (void)reloadRentals
{
    [self prepareToFetchRentalsWithHandler:^{
        [[EHIUserManager sharedInstance] currentAndUpcomingRentalsWithHandler:[self handleUserRentals:nil]];
    }];
    
}

- (void)refreshRentals
{
    [self refreshRentals:nil];
}

- (void)refreshRentals:(EHIUserHandler)handler
{
    [self prepareToFetchRentalsWithHandler:^{
        [[EHIUserManager sharedInstance] refreshCurrentAndUpcomingRentalsWithHandler:[self handleUserRentals:handler]];
    }];
}

//
// Helpers
//

- (void)prepareToFetchRentalsWithHandler:(void (^)(void))handler
{
    [self setContentModel:[EHIModel placeholder] forType:EHIContentSectionTypeLoading handler:^{
        ehi_call(handler)();
    }];
}

- (EHIUserHandler)handleUserRentals:(EHIUserHandler)handler
{
    return ^(EHIUser *user, EHIServicesError *error) {
        BOOL promptNotifications = (![EHISettings sharedInstance].didPromptDashboardNewFeature || ![EHISettings sharedInstance].didPromptDashboardNotificationsUnuath)
                                    && [[EHINotificationManager sharedInstance] shouldPromptNotifications];
        BOOL hasCurrentRentals   = user.currentRentals.count > 0;
        BOOL hasUpcomingRentals  = user.upcomingRentals.count > 0;
        
        // show notification prompt once even is user have no rentals
        if(promptNotifications) {
            [EHISettings sharedInstance].didPromptDashboardNewFeature = YES;
            [EHISettings sharedInstance].didPromptDashboardNotificationsUnuath = YES;
            
            [self setContentModel:[EHIModel placeholder] forType:EHIContentSectionTypeNotifications handler:^{
                ehi_call(handler)(user, error);
            }];
        }
        // if we have a user with current rentals, show the current rental
        else if(hasCurrentRentals) {
            [self setContentModel:user.currentRentals.firstRental forType:EHIContentSectionTypeCurrent handler:^{
                ehi_call(handler)(user, error);
            }];
        }
        // if we have a user with upcoming rentals, show the first upcoming rental
        else if(hasUpcomingRentals) {
            [self setContentModel:user.upcomingRentals.firstRental forType:EHIContentSectionTypeUpcoming handler:^{
                ehi_call(handler)(user, error);
            }];
        }
        // otherwise show the default authenticated state
        else {
            [self setContentModel:nil forType:EHIContentSectionTypeNone handler:^{
                ehi_call(handler)(user, error);
            }];
        }
    };
}

# pragma mark - Notifications

- (void)registerForApplicationNotifications:(BOOL)shouldRegister
{
    if(shouldRegister) {
        // observe countries endpoint to make sure that we are always up to date with the weekend specials object
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(presentPromotionModalIfNeeded) name:EHICountriesRefreshedNotification object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(willEnterForeground:) name:UIApplicationWillEnterForegroundNotification object:nil];
    } else {
        [[NSNotificationCenter defaultCenter] removeObserver:self name:EHICountriesRefreshedNotification object:nil];
        [[NSNotificationCenter defaultCenter] removeObserver:self name:UIApplicationWillEnterForegroundNotification object:nil];
    }
}

- (void)willEnterForeground:(NSNotification *)notification
{
    [self refreshRentals:^(EHIUser *user, EHIServicesError *error) {
        // prevent the default error handling
        [error consume];
    }];
}

# pragma mark - Accessors

- (id)modelAtIndexPath:(NSIndexPath *)indexPath
{
    switch((EHIDashboardSection)indexPath.section) {
        case EHIDashboardSectionQuickstart:
            return self.quickstartModels[indexPath.item];
        default: return nil;
    }
}

- (BOOL)isLoading
{
    return self.contentType == EHIContentSectionTypeLoading;
}

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

# pragma mark - Analytics

-(void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    context[EHIAnalyticsDashStatusKey] = [self currentStatus];
    context[EHIAnalyticsDashDaysUntilRentalKey] = [self daysUntilRental];

}

//
// Helpers
//

- (NSString *)currentStatus
{
    switch(self.contentType) {
        case EHIContentSectionTypeNone:
            return EHIUser.currentUser ? EHIAnalyticsDashStateNone : EHIAnalyticsDashStateUnauth;
        case EHIContentSectionTypeCurrent:
            return EHIAnalyticsDashStateCurrent;
        case EHIContentSectionTypeUpcoming:
            return EHIAnalyticsDashStateUpcoming;
        case EHIContentSectionTypeLoading:
        case EHIContentSectionTypeNotifications:
            return EHIAnalyticsDashStateNone;
    }
}

- (NSNumber *)daysUntilRental
{
    if(self.contentType != EHIContentSectionTypeUpcoming) {
        return nil;
    }
    
    EHIUserRental *rental = (EHIUserRental *)self.contentModel;
    return @([[NSDate ehi_today] ehi_daysUntilDate:rental.pickupDate]);
}

# pragma mark - MTRReactive

+ (NSArray *)nonreactiveProperties:(EHIDashboardViewModel *)model
{
    return @[
        @key(model.searchTitle),
    ];
}

@end
