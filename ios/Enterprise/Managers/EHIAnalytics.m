//
//  EHIAnalytics.m
//  Enterprise
//
//  Created by Ty Cobb on 5/28/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@import Localytics;

#import "EHIAnalytics.h"
#import "EHIAnalyticsContext+Serialization.h"
#import "EHIUserManager.h"
#import "EHILocationManager.h"
#import "EHISettings.h"
#import "EHICrashManager.h"
#import "EHIPaymentManager.h"
#import <Appsee/Appsee.h>

typedef NS_ENUM(NSInteger, EHIAnalyticsDimension) {
    EHIAnalyticsDimensionLanguage,
    EHIAnalyticsDimensionLocationServices,
    EHIAnalyticsDimensionCountry,
    EHIAnalyticsDimensionLoyaltyTier,
    EHIAnalyticsDimensionLoyaltyPoints,
    EHIAnalyticsDimensionMemberType,
    EHIAnalyticsDimensionCustomerId,
    EHIAnalyticsDimensionVisitorType,
    EHIAnalyticsDimensionSessionSource,
    EHIAnalyticsDimensionApplePay,
};

NS_ASSUME_NONNULL_BEGIN

@interface EHIAnalytics () <EHIUserListener, LLAnalyticsDelegate>
@property (strong, nonatomic) EHIAnalyticsContext *context;
@end

@implementation EHIAnalytics

+ (instancetype)sharedInstance
{
    static EHIAnalytics *sharedInstance;
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [self new];
    });
    
    return sharedInstance;
}

- (instancetype)init
{
    if(self = [super init]) {
        self.context = [self nextContext];
    }
    
    return self;
}

# pragma mark - Launch

+ (void)prepareToLaunch
{
    [self bootstrap];
    [self setUpLogging];
    [self setUpOptOutStatus];
    
    EHIDomainInfo(EHILogDomainAnalytics, @"key: %@", [EHISettings environment].analyticsKey);
    
    [[NSNotificationCenter defaultCenter] addObserver:[self sharedInstance] selector:@selector(didEnterBackground:) name:UIApplicationDidEnterBackgroundNotification object:nil];

    [[self sharedInstance] launch];
}

+ (void)bootstrap
{
    [Localytics setAnalyticsDelegate:[self sharedInstance]];
    // TODO: if the environment changes, the anayltics env won't update until restart
    [Localytics autoIntegrate:[EHISettings environment].analyticsKey withLocalyticsOptions:nil launchOptions:nil];
    
    [Appsee start:[EHISettings environment].appSeeKey];
}

+ (void)setUpLogging
{
    BOOL loggingEnabled = EHILocalyticsLogging != 0 && (EHILogDomains & EHILogDomainAnalytics) != 0;
    
    [Localytics setLoggingEnabled:loggingEnabled];
    [Appsee setDebugToNSLog:loggingEnabled];
}

+ (void)setUpOptOutStatus
{
    [self enableDataCollection:[EHISettings sharedInstance].allowDataCollection];
    [Appsee setOptOutStatus:NO];
    [EHICrashManager enableDataCollection:YES];
}

- (void)launch
{
    [[EHIUserManager sharedInstance] addListener:self];
}

- (void)didEnterBackground:(NSNotification *)notification
{
    // clear the session source
    [self updateSessionSource:EHIAnalyticsDimensionFallbackValue];
}

# pragma mark - LLAnayticsDelegate

- (void)localyticsSessionWillOpen:(BOOL)isFirst isUpgrade:(BOOL)isUpgrade isResume:(BOOL)isResume
{
    if (isFirst) {
        [self updateCustomDimensionsForManager:[EHIUserManager sharedInstance] user:[EHIUserManager sharedInstance].currentUser];
    }
}

# pragma mark - Privacy

+ (void)enableDataCollection:(BOOL)enabled
{
    [Localytics setOptedOut:!enabled];
    [Localytics pauseDataUploading:!enabled];
    
    if(enabled) {
        [Localytics setPrivacyOptedOut:NO];
        [Appsee setOptOutStatus:NO];
    }
}

+ (void)forgetMe
{
    [Localytics upload];
    
    // run it in on a background thread as per Appsee documentation
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0);
    dispatch_async(queue, ^{
        [self enableDataCollection:NO];
        [Localytics setPrivacyOptedOut:YES];
        [Appsee deleteCurrentUserData];
    });
}

# pragma mark - Tracking

+ (void)markViewAsSensitive:(UIView *)view
{
    [Appsee markViewAsSensitive:view];
}

+ (void)trackState:(nullable EHIAnalyticsContextHandler)handler
{
    [[self sharedInstance] trackContextWithAction:nil type:EHIAnalyticsActionTypeNone handler:handler];
}

+ (void)trackAction:(NSString *)action handler:(nullable EHIAnalyticsContextHandler)handler
{
    [self trackAction:action type:EHIAnalyticsActionTypeTap handler:handler];
}

+ (void)trackAction:(NSString *)action type:(EHIAnalyticsActionType)type handler:(nullable EHIAnalyticsContextHandler)handler
{
    [[self sharedInstance] trackContextWithAction:action type:type handler:handler];
}

+ (void)updateSessionSource:(NSString *)source
{
    [[self sharedInstance] updateSessionSource:source];
    [EHIAnalytics trackState:nil];
}

//
// Track
//

- (void)trackContextWithAction:(nullable id)action type:(EHIAnalyticsActionType)type handler:(nullable EHIAnalyticsContextHandler)handler
{
    EHIAnalyticsContext *context = [self.context clone];
    
    // update action if necessary
    context.action     = action;
    context.actionType = type;
    
    // add any temporary attributes after updating the action
    if(handler) {
        [context applyTemporaryAttributes:handler];
    }
    
    [self trackContext:context];
}

- (void)trackContext:(EHIAnalyticsContext *)context
{
    // serialize the context
    NSString *path  = [context path];
    NSString *macro = [context macroEvent];
    NSNumber *value = context.customerValue ? @(context.customerValue) : nil;
    NSDictionary *attributes = [context dictionaryRepresentation];

    EHIDomainInfo(EHILogDomainAnalytics,  @"tracking - %@", context);

    if(macro) {
        EHIDomainDebug(EHILogDomainAnalytics, @"---macro - %@", macro);
    }

    EHIDomainDebug(EHILogDomainAnalytics, @"----data - %@", attributes);

    // only track screens and events if it's not an error we are tracking
    if (![macro isEqualToString:EHIAnalyticsMacroEventError]) {
        // and track it
        [Localytics tagScreen:path];
        [Localytics tagEvent:path attributes:attributes customerValueIncrease:value];

        if(context.state && context.screen) {
            if(context.action) {
                [Appsee addEvent:path withProperties:attributes];
            } else {
                [Appsee startScreen:path];
            }
        }
    }

    // track the macro event if necessary
    if(macro) {
        [Localytics tagEvent:macro attributes:attributes];
        [Appsee addEvent:macro withProperties:attributes];
    }

    // if this was not an action event, then upload
    if(!context.action) {
        [Localytics upload];
    }

    // add breadcrumb to crittercism
    [EHICrashManager leaveBreadcrumb:path];

    EHIDomainDebug(EHILogDomainAnalytics, @"===============================");
}

- (void)updateSessionSource:(nullable NSString *)source
{
    self[EHIAnalyticsDimensionSessionSource] = source;
}

# pragma mark - Context

+ (void)changeScreen:(NSString *)screen
{
    [self changeScreen:screen state:nil];
}

+ (void)changeScreen:(NSString *)screen state:(nullable NSString *)state
{
    EHIAnalytics *instance = [self sharedInstance];
    EHIAnalyticsContext *previousContext = instance.context.clone;
    EHIAnalyticsContext *context = [instance nextContext];
   
    // update the new context with this screen / state
    [context setRouterScreen:screen];
    if(state) {
        [context setRouterState:state];
    }

    // update the previous path of this new context
    context.previousPath = previousContext.path;
   
    EHIDomainVerbose(EHILogDomainAnalytics, @"update - %@", context);
    
    instance.context = context;
}

// the screen names and states for the watch are not mapped to any router screen names
+ (void)changeWatchScreen:(NSString *)screen state:(NSString *)state
{
    EHIAnalytics *instance = [self sharedInstance];
    EHIAnalyticsContext *previousContext = instance.context.clone;
    EHIAnalyticsContext *context = [instance nextContext];
    
    // update the new context with this screen / state
    context.screenKey = screen;
    context.screen    = screen;
    [context setState:state silent:YES];
    
    // update the previous path of this new context
    context.previousPath = previousContext.path;
    
    EHIDomainVerbose(EHILogDomainAnalytics, @"update - %@", context);

    instance.context = context;
}

+ (EHIAnalyticsContext *)context
{
    EHIAnalytics *instance = [self sharedInstance];
    return instance.context;
}

- (EHIAnalyticsContext *)nextContext
{
    return [EHIAnalyticsContext new];
}

# pragma mark - EHIUserListener

- (void)manager:(EHIUserManager *)manager didChangeAuthenticationForUser:(EHIUser *)user
{
    [self updateCustomDimensionsForManager:manager user:user];
}

//
// Helpers
//

- (void)updateCustomDimensionsForManager:(EHIUserManager *)manager user:(nullable EHIUser *)user
{
    // update region dimensions
    self[EHIAnalyticsDimensionLanguage]   = NSLocale.ehi_identifier;
    self[EHIAnalyticsDimensionCountry]    = NSLocale.ehi_region;
   
    // update user dimensions
    self[EHIAnalyticsDimensionCustomerId]    = user ? user.profiles.basic.loyalty.number : EHIAnalyticsDimensionFallbackValue;
    self[EHIAnalyticsDimensionLoyaltyTier]   = user ? EHILoyaltyTierTitleForTier(user.profiles.basic.loyalty.tier) : EHIAnalyticsDimensionFallbackValue;
    self[EHIAnalyticsDimensionLoyaltyPoints] = user ? @(user.profiles.basic.loyalty.pointsToDate) : EHIAnalyticsDimensionFallbackValue;
    self[EHIAnalyticsDimensionMemberType]    = [self memberTypeFromUserManager:manager];
    self[EHIAnalyticsDimensionVisitorType]   = [self visitorTypeFromUser:user];
    self[EHIAnalyticsDimensionApplePay]      = @([EHIPaymentManager canPayWithApplePay]);
    
    // update location services dimensions
    self[EHIAnalyticsDimensionLocationServices] = [self locationTypeFromStatus:[EHILocationManager sharedInstance].locationStatus];
    
    NSString *source = [Localytics valueForCustomDimension:EHIAnalyticsDimensionSessionSource];
    if (!source) {
        self[EHIAnalyticsDimensionSessionSource] = EHIAnalyticsDimensionFallbackValue;
    }
}

- (void)setObject:(nullable id)object atIndexedSubscript:(EHIAnalyticsDimension)dimension
{
    // serialize the value if it exists
    if(object) {
        object = [EHIAnalyticsContext serializeValue:object];
    }
   
    // update the custom dimension
    [Localytics setValue:object forCustomDimension:dimension];
}

- (NSString *)locationTypeFromStatus:(CLAuthorizationStatus)status
{
    switch(status) {
        case kCLAuthorizationStatusAuthorizedWhenInUse:
            return EHIAnalyticsDimensionLocationInApp;
        case kCLAuthorizationStatusAuthorizedAlways:
            return EHIAnalyticsTrueValue;
        default:
            return EHIAnalyticsFalseValue;
    }
}

- (NSString *)memberTypeFromUserManager:(EHIUserManager *)manager
{
    if(!manager.currentUser) {
        return EHIAnalyticsDimensionMemberTypeGuest;
    } else if(manager.isEmeraldUser) {
        return EHIAnalyticsDimensionMemberTypeLoyaltyAlt;
    } else {
        return EHIAnalyticsDimensionMemberTypeLoyalty;
    }
}

- (NSString *)visitorTypeFromUser:(nullable EHIUser *)user
{
    if(!user) {
        return EHIAnalyticsDimensionVisitorTypeGuest;
    } else if(user.corporateContract) {
        return EHIAnalyticsDimensionVisitorTypeCorp;
    } else {
        return EHIAnalyticsDimensionVisitorTypeMember;
    }
}

@end

@implementation EHIAnalytics (Debug)

+ (NSDictionary *)optOutStatus
{
    return @{
        @"Localytics [DataCollection]" : @([Localytics isOptedOut]),
        @"Localytics [GDPR]"           : @([Localytics isPrivacyOptedOut]),
        @"AppSee"                      : @([Appsee getOptOutStatus]),
        @"Apteligent"                  : @([EHICrashManager isOptedOut]),
    };
}

@end

NS_ASSUME_NONNULL_END
