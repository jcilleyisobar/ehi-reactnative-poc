//
//  EHISurvey.m
//  Enterprise
//
//  Created by Rafael Ramos on 12/2/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHISurvey.h"
#import <ForeSee/ForeSee.h>
#import <ForeSee/FSInviteHandler.h>
#import "EHISurveyInviteViewModel.h"
#import "EHISettings.h"
#import "EHIMainRouter.h"
#import "EHIAnalytics.h"

@interface EHISurvey () <FSInviteHandler>
@property (strong, nonatomic) EHISurveySession *session;
@property (strong, nonatomic) EHISurveyInviteViewModel *customInviteModal;
@property (copy  , nonatomic) EHISurveyCustomerDetailsValidationHandler validationBlock;
@end

@implementation EHISurvey

+ (instancetype)sharedInstance
{
    static EHISurvey *sharedInstance;
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [self new];
    });
    
    return sharedInstance;
}

+ (void)prepareToLaunch
{
    BOOL loggingEnabled = EHISurveyLogging != 0;
    [ForeSee setDebugLogEnabled:loggingEnabled];
    [ForeSee setEventLogEnabled:loggingEnabled];
    
#if defined(DEBUG) || defined(UAT)
    EHISurvey *instance = [self sharedInstance];
    [ForeSee setSkipPoolingCheck:loggingEnabled || instance.skipPoolingCheck];
#endif
    
    [ForeSee start];

    [ForeSee setInviteHandler:[self sharedInstance]];
}

+ (void)showInviteIfNeeded
{
    [[self sharedInstance] checkIfEligibleForSurvey];
}

- (instancetype)init
{
    if(self = [super init]) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(checkIfEligibleForSurvey) name:UIApplicationWillEnterForegroundNotification object:nil];
    }
    return self;
}

- (void)requestSurveyWithCustomerDetails:(NSString *)phoneOrEmail validation:(EHISurveyCustomerDetailsValidationHandler)validationBlock;
{
    self.validationBlock = validationBlock;
    
    // add CPP before requesting the survey
    [self encodeSession];
    
    [ForeSee customInviteAcceptedWithContactDetails:phoneOrEmail];
}

# pragma mark - Accessors

- (NSURL *)policiesURL
{
    NSString *url = @"https://www.foresee.com/about-us/privacy-policy/";
    return [NSURL URLWithString:url];
}

- (void)setSkipPoolingCheck:(BOOL)skipPoolingCheck
{
    [ForeSee setSkipPoolingCheck:skipPoolingCheck];
}

- (BOOL)skipPoolingCheck
{
    return [EHISettings sharedInstance].skipSurveyPoolingCheck;
}

- (EHISurveySession *)session
{
    if(!_session) {
        _session = [EHISurveySession new];
    }
    
    return _session;
}

- (EHISurveyInviteViewModel *)customInviteModal
{
    if(!_customInviteModal) {
        _customInviteModal = [EHISurveyInviteViewModel new];
    }
    
    return _customInviteModal;
}

# pragma mark - FSInviteHandler

- (void)show
{
    __weak __typeof(self) welf = self;
    [self.customInviteModal present:^BOOL(NSInteger index, BOOL canceled) {
        if(index == 0 && !canceled) {
            [welf didAcceptedInvite];
        } else {
            [welf didRejectedInvite];
        }

        return YES;
    }];
}

- (void)hideWithAnimation:(BOOL)animate
{
    ehi_call(self.validationBlock)(NO);
}

- (void)setInvalidInput:(BOOL)isInvalid
{
    ehi_call(self.validationBlock)(isInvalid);
}

# pragma mark - Survey Lifecycle

- (void)checkIfEligibleForSurvey
{
    BOOL shouldShowSurvey = [NSLocale ehi_shouldShowSurveyPrompt];
    if(shouldShowSurvey) {
        [ForeSee checkIfEligibleForSurvey];
    }
}

- (void)didAcceptedInvite
{
    [EHIAnalytics trackAction:EHIAnalyticsSurveyActionYes handler:nil];

    [EHIMainRouter router].transition.push(EHIScreenSurvey).start(nil);
}

- (void)didRejectedInvite
{
    [EHIAnalytics trackAction:EHIAnalyticsSurveyActionNo handler:nil];

    [ForeSee customInviteDeclined];
}

- (void)resetSurveyState
{
#if defined(DEBUG) || defined(UAT)
    [ForeSee resetState];
#endif
}

- (void)dealloc
{
   [[NSNotificationCenter defaultCenter] removeObserver:self name:UIApplicationWillEnterForegroundNotification object:nil];
}

//
// Helpers
//

- (void)encodeSession
{
#if defined(DEBUG) || defined(UAT)
    self.session[EHISurveyDebugTag] = @(YES);
#else
    self.session[EHISurveyDebugTag] = @(NO);
#endif
    
    (self.session.decodeSession ?: @{}).each(^(NSString *key, NSString *value) {
        [ForeSee setCPPValue:value forKey:key];
    });
}

@end
