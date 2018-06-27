//
//  EHIConfiguration.m
//  Enterprise
//
//  Created by Ty Cobb on 6/15/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel_Subclass.h"
#import "EHIConfiguration.h"
#import "EHIServices+Config.h"
#import "EHIServices+User.h"
#import "EHISettings.h"
#import "EHIUserManager.h"
#import "EHISearchEnvironment.h"

NS_ASSUME_NONNULL_BEGIN

NSString * const EHICountriesRefreshedNotification = @"EHICountriesRefreshed";

#define EHIAlamoRedirectFallback    @"https://www.alamo.com/en_US/car-rental/home.html"
#define EHINationalRedirectFallback @"https://www.nationalcar.com/en_US/car-rental/home.html"
#define EHIFeedbackUrlFallback      @"https://secure.opinionlab.com/ccc01/o.asp?id=mOSGxAOb"

@interface EHIConfiguration ()
@property (assign, nonatomic) BOOL isReady;
@property (assign, nonatomic) BOOL isFetching;
@property (strong, nonatomic) NSMutableArray *readinessHandlers;
@property (copy  , nonatomic) NSString *nationalReservationUrl;
@property (copy  , nonatomic) NSString *alamoReservationUrl;
@property (copy  , nonatomic) NSString *feedbackUrl;
@end

@implementation EHIConfiguration

- (instancetype)init
{
    if(self = [super init]) {
        _readinessHandlers = [NSMutableArray new];
        
        // whenever the apps enters the foreground, we must have the countries collection up to date (weekend special)
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshCountries:) name:UIApplicationWillEnterForegroundNotification object:nil];
    }
    
    return self;
}

+ (instancetype)configuration
{
    static EHIConfiguration *configuration;
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        configuration = [self new];
    });
    
    return configuration;
}

# pragma mark - EHICountriesRefreshedNotification

- (void)refreshCountries
{
    [self refreshCountries:nil];
}

- (void)refreshCountries:(NSNotification * _Nullable)notification
{
    [[EHIServices sharedInstance] fetchCountriesPurgingData:YES handler:^(NSArray *countries, EHIServicesError *error) {
        // prevent the default error handling
        if(notification) {
            [error consume];
        }

        [[NSNotificationCenter defaultCenter] postNotificationName:EHICountriesRefreshedNotification object:nil];
    }];
}

# pragma mark - Accessors

- (null_unspecified NSString *)feedbackUrl
{
    return _feedbackUrl ?: EHIFeedbackUrlFallback;
}

- (null_unspecified EHIPhone *)primarySupportPhone
{
    return (self.supportNumbers ?: @[]).find(^(EHIPhone *phone) {
        // TODO: this may not turn out to be right
        return phone.type == EHIPhoneTypeContactUs;
    });
}

- (null_unspecified EHIPhone *)roadsideAssistancePhone
{
    return (self.supportNumbers ?: @[]).find(^(EHIPhone *phone) {
        // TODO: this may not turn out to be right
        return phone.type == EHIPhoneTypeRoadside;
    });
}

- (null_unspecified EHIPhone *)eplusPhone
{
    return (self.supportNumbers ?: @[]).find(^(EHIPhone *phone) {
        return phone.type == EHIPhoneTypeEPlus;
    });
}

- (null_unspecified EHIPhone *)dnrNumber
{
    return (self.supportNumbers ?: @[]).find(^(EHIPhone *phone) {
        return phone.type == EHIPhoneTypeDnr;
    });
}

- (null_unspecified NSArray *)customerSupportNumbers
{
    return (self.supportNumbers ?: @[]).select(^(EHIPhone *phone){
        return phone.type == EHIPhoneTypeContactUs
            || phone.type == EHIPhoneTypeDisabilities
            || phone.type == EHIPhoneTypeEPlus
            || phone.type == EHIPhoneTypeRoadside;
    });
}

- (null_unspecified NSString *)alamoReservationUrl
{
    return _alamoReservationUrl ?: EHIAlamoRedirectFallback;
}

- (null_unspecified NSString *)nationalReservationUrl
{
    return _nationalReservationUrl ?: EHINationalRedirectFallback;
}

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHIConfiguration *)model
{
    return @{
        @"support_phone_numbers"    : @key(model.supportNumbers),
        @"support_send_message_url" : @key(model.sendMessageUrl),
        @"support_answers_url"      : @key(model.searchAnswersUrl),
        @"forgot_password_url"      : @key(model.forgotPasswordUrl),
        @"print_receipt_url"        : @key(model.printReceiptUrl),
        @"activate_url"             : @key(model.activateUrl),
        @"national_reservation_url" : @key(model.nationalReservationUrl),
        @"ec_forgot_password_url"   : @key(model.nationalForgotPasswordUrl),
        @"feedback_url"             : @key(model.feedbackUrl),
        @"alamo_reservation_url"    : @key(model.alamoReservationUrl),
        @"mapped_cor"               : @key(model.mappedCountryOfResidence),
        @"mapped_locale"            : @key(model.mappedLocale),
        @"supported_locales"        : @key(model.supportedLocales)
    };
}

@end

@implementation EHIConfiguration (Readiness)

- (nullable EHIConfigurationHandler *)onReady:(EHIConfigurationCallback)callback
{
    // make the network call if we haven't already
    if(!self.isReady && !self.isFetching) {
        self.isFetching = YES;

        void (^fetchConfigHandler)(void) = ^(void) {
            [self fetchConfiguration];
        };

#if defined(DEBUG) || defined(UAT)
        if ([EHISettings sharedInstance].isFirstRun) {
            [[EHISettings sharedInstance].environment showEnvironmentSelectionAlertForService:EHIServicesEnvironmentTypeGBOProfile withCompletion:fetchConfigHandler];
        } else {
            fetchConfigHandler();
        }
#else
        fetchConfigHandler();
#endif
    }
    
    EHIConfigurationHandler *handler = nil;
    
    // if we're ready, then just call the handler
    if(self.isReady) {
        callback(YES);
    }
    // otherwise store the callback for later
    else {
        handler = [[EHIConfigurationHandler alloc] initWithBlock:callback];
        [self.readinessHandlers addObject:handler];
    }
    
    return handler;
}

//
// Helpers
//

- (void)fetchConfiguration
{    
    dispatch_group_t group = dispatch_group_create();
    __block BOOL success = YES;
    
    // common handler for leaving dispatch group
    void (^sharedHandler)(id, EHIServicesError *) = ^(id content, EHIServicesError *error) {
        [error consume];
        success &= !error.hasFailed;
        dispatch_group_leave(group);
    };
    
    dispatch_group_enter(group);
    [[EHIServices sharedInstance] fetchConfigurationSupport:self handler:sharedHandler];
    
    dispatch_group_enter(group);
    [[EHIServices sharedInstance] fetchCountriesWithHandler:sharedHandler];
    
    dispatch_group_notify(group, dispatch_get_main_queue(), ^{
        // we're ready as long as we didn't error
        self.isReady    = success;
        self.isFetching = NO;
        
        // call back the appropriate handlers
        [self notifyHandlers:self.isReady];
    });
}

- (void)notifyHandlers:(BOOL)isReady
{
    // record which handlers we call so that we can remove them
    NSMutableIndexSet *indicesForNotifiedHandlers = [NSMutableIndexSet new];
  
    self.readinessHandlers.each(^(EHIConfigurationHandler *handler, NSInteger index) {
        // callback the handler if we're ready or if it doesn't care to wait
        if(isReady || !handler.waitsUntilReady) {
            handler.block(isReady);
            // and then remove it once its called
            [indicesForNotifiedHandlers addIndex:index];
        } 
    });
   
    // remove the handlers
    [self.readinessHandlers removeObjectsAtIndexes:indicesForNotifiedHandlers];
}

@end

NS_ASSUME_NONNULL_END
