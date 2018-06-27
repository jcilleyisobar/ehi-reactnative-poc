//
//  EHISettingsEnvironment.m
//  Enterprise
//
//  Created by Ty Cobb on 1/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHISettingsEnvironment.h"
#import "EHISearchEnvironment.h"
#import "EHIUserManager.h"
#import "EHIConfiguration.h"
#import "EHIGBOEnvironment.h"
#import "EHIAEMEnvironment.h"

#define EHISettingsGBOEnvironmentTypeKey @"EHISettingsEnvironmentTypeKey"
#define EHISettingsAEMEnvironmentTypeKey @"EHISettingsAEMEnvironmentTypeKey"
#define EHISettingsEnvironmentBetaPassword @"Lex5My7H"

#ifdef UAT
#define EHIDefaultGBOEnvironmentType EHIEnvironmentTypeRcQaInt1
#define EHIDefaultAEMEnvironmentType EHIEnvironmentTypeRcQaInt1
#elif DEBUG
#define EHIDefaultGBOEnvironmentType EHIEnvironmentTypeRcQaInt1
#define EHIDefaultAEMEnvironmentType EHIEnvironmentTypeRcQaInt1
#elif PENTEST
#define EHIDefaultGBOEnvironmentType EHIEnvironmentTypePenTest
#define EHIDefaultAEMEnvironmentType EHIEnvironmentTypeRcQaInt1
#else
#define EHIDefaultGBOEnvironmentType EHIEnvironmentTypeProd
#define EHIDefaultAEMEnvironmentType EHIEnvironmentTypeProd
#endif

@interface EHISettingsEnvironment ()
@property (assign, nonatomic) EHIEnvironmentType gboEnvironment;
@property (assign, nonatomic) EHIEnvironmentType aemEnvironment;
@property (strong, nonatomic) EHISearchEnvironment *searchEnvironment;
@property (copy  , nonatomic) NSString *search;
@property (copy  , nonatomic) NSString *searchApiKey;
@end

@implementation EHISettingsEnvironment

# pragma mark - Unarchiving

+ (instancetype)unarchive
{
    EHISettingsEnvironment *environment = [EHISettingsEnvironment new];
    
    environment.searchEnvironment = [EHISearchEnvironment unarchive];

    // on launch we're going to load the environment synchronously and _then_ authenticate. this allows the app
    // to start in a consistent state in the general case.
    environment.internalGBOEnvironment = [self unarchivedEnvironmentForKey:EHISettingsGBOEnvironmentTypeKey defaultEnvironment:EHIDefaultGBOEnvironmentType];
    environment.internalAEMEnvironment = [self unarchivedEnvironmentForKey:EHISettingsAEMEnvironmentTypeKey defaultEnvironment:EHIDefaultAEMEnvironmentType];

    // then authenticate, and if we fail revert to the default environment
    [environment authenticateEnvironment:environment.gboEnvironment withHandler:^(BOOL didAuthenticate) {
        if(!didAuthenticate) {
            environment.gboEnvironment = EHIDefaultGBOEnvironmentType;
            // destroy the current user / credentials (if any)
            [[EHIUserManager sharedInstance] logoutCurrentUser];
        }
    }];
    
    return environment;
}

+ (EHIEnvironmentType)unarchivedEnvironmentForKey:(NSString *)key defaultEnvironment:(EHIEnvironmentType)defaultValue
{
    // if we are not DEBUG or UAT, we never archive our environment -- we should always be PROD
#if !defined(DEBUG) && !defined(UAT)
    return defaultValue;
#else
    NSNumber *storedType = [[NSUserDefaults standardUserDefaults] objectForKey:key];

    // if we don't have a stored type yet, use the defaults
    if(!storedType) {
        return defaultValue;
    }

    return (EHIEnvironmentType)storedType.integerValue;
#endif
}

#pragma mark - Getters

- (NSString *)analyticsKey
{
#ifdef UAT
	return @"d9b143ed58e986fdea51f1a-dc172236-1f48-11e5-44ff-006918dcf667";
#elif DEBUG
	return @"9f349e57237455b0a6caf91-dd5f6c48-eaa6-11e4-59a2-00a426b17dd8";
#elif PENTEST
    return @"d9b143ed58e986fdea51f1a-dc172236-1f48-11e5-44ff-006918dcf667";
#else
	return @"85e980361996e1d7843f58b-2c31e068-eaa7-11e4-59a3-00a426b17dd8";
#endif
}

- (NSString *)crittercismKey
{
#ifdef UAT
	return @"52fb93bc97c8f26539000004";
#elif DEBUG
	return @"52fb92f58b2e332f65000001";
#elif PENTEST
    return @"52fb93bc97c8f26539000004";
#else
	return @"52fb9395558d6a6d1b000006";
#endif
}

- (NSString *)appSeeKey
{
#ifdef UAT
    return @"75eb76b3b2234965a4f149b9a1295a9a";
#elif DEBUG
    return @"";
#elif PENTEST
    return @"";
#else
    return @"208d61b556544b7e9fc9ee2e103baabb";
#endif
}

- (NSString *)iTunesLink
{
    return @"itms-apps://itunes.apple.com/app/id1020641417";
}

- (NSString *)farepaymentUrl
{
    return @"farepayment";
}

# pragma mark - Accessors

- (void)setGBOEnvironment:(EHIEnvironmentType)type handler:(void (^)(EHIEnvironmentType, BOOL))handler
{
    [self authenticateEnvironment:type withHandler:^(BOOL didAuthenticate) {
        if(didAuthenticate) {
            self.internalGBOEnvironment = type;
        }
        
        ehi_call(handler)(self.gboEnvironment, didAuthenticate);
    }];
}

- (void)setInternalGBOEnvironment:(EHIEnvironmentType)type
{
    _gboEnvironment = type;

    // update the stored type
    [[NSUserDefaults standardUserDefaults] setObject:@(type) forKey:EHISettingsGBOEnvironmentTypeKey];
    
    // notify listeners of the environment change
    [[NSNotificationCenter defaultCenter] postNotificationName:EHISettingsEnvironmentChangedNotification object:nil];
}

- (void)setAEMEnvironment:(EHIEnvironmentType)type handler:(void (^)(EHIEnvironmentType, BOOL))handler
{
    [self authenticateEnvironment:type withHandler:^(BOOL didAuthenticate) {
        if(didAuthenticate) {
            self.internalAEMEnvironment = type;
        }

        ehi_call(handler)(self.aemEnvironment, didAuthenticate);
    }];
}

- (void)setInternalAEMEnvironment:(EHIEnvironmentType)type
{
    _aemEnvironment = type;

    // update the stored type
    [[NSUserDefaults standardUserDefaults] setObject:@(type) forKey:EHISettingsAEMEnvironmentTypeKey];
}

- (NSString *)serviceWithType:(EHIServicesEnvironmentType)servicesType
{
    NSString *domainURL = [self configurationForService:servicesType].domainURL;

    // ensure we have a trailing slash
    if(![domainURL hasSuffix:@"/"]) {
        domainURL = [domainURL stringByAppendingString:@"/"];
    }

    return domainURL;
}

- (NSString *)servicesApiKeyWithType:(EHIServicesEnvironmentType)servicesType
{
    return [self configurationForService:servicesType].apiKey;
}

- (NSString *)search
{
    return self.searchEnvironment.serviceURL;
}

- (NSString *)searchApiKey
{
    return self.searchEnvironment.apiKey;
}

- (NSString *)displayNameForService:(EHIServicesEnvironmentType)service
{
    return [self configurationForService:service].name;
}

# pragma mark - Beta Validation

- (void)authenticateEnvironment:(EHIEnvironmentType)environment withHandler:(void(^)(BOOL didAuthenticate))handler
{
    BOOL shouldAuthenticate = [self environmentRequireAuthentication:environment];
    if(!shouldAuthenticate) {
        ehi_call(handler)(YES);
        return;
    }

    // otherwise, prompt for a password
    EHIAlertViewBuilder *alert = EHIAlertViewBuilder.new
        .style(EHIAlertViewStyleSecureTextInput)
        .title(@"Please enter password")
        .message(@"You must authenticate to use BETA")
        .button(@"Submit")
        .cancelButton(nil);

    alert.show(^(NSString *text, NSInteger index, BOOL canceled) {
        BOOL didValidate = [text isEqualToString:EHISettingsEnvironmentBetaPassword];

        // if the password was correct, update the type
        if(!didValidate && !canceled) {
            EHIAlertViewBuilder.new
                .title(@"Invalid login")
                .message(@"Your environment has not been changed, please try again.")
                .button(@"Okay").show(nil);
        }

        ehi_call(handler)(didValidate);
    });
}

- (BOOL)environmentRequireAuthentication:(EHIEnvironmentType)environment
{
    switch(environment) {
        case EHIEnvironmentTypeBeta:
            return YES;
        default:
            return NO;
    }
}

- (void)showEnvironmentSelectionAlertWithCompletion:(void(^ __nullable)(void))handler
{
    EHIGBOEnvironment *gbo = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:self.gboEnvironment];

    [gbo showEnvironmentSelectionAlertWithCompletion:^(BOOL canceled, EHIEnvironmentType environmentType) {
        if(canceled) {
            ehi_call(handler)();
            return;
        }

        [self setGBOEnvironment:environmentType handler:^(EHIEnvironmentType type, BOOL didUpdate) {
            [[EHIConfiguration configuration] refreshCountries];
            if (didUpdate) {
                [[EHIUserManager sharedInstance] logoutCurrentUser];
            }

            ehi_call(handler)();
        }];
    }];
}

- (void)showAEMEnvironmentSelectionAlertWithCompletion:(void(^ __nullable)(void))handler
{
    EHIAEMEnvironment *aem = [EHIAEMEnvironment serviceWithEnvironment:self.aemEnvironment];

    [aem showEnvironmentSelectionAlertWithCompletion:^(BOOL canceled, EHIEnvironmentType environmentType) {
        if(canceled) {
            ehi_call(handler)();
            return;
        }

        [self setAEMEnvironment:environmentType handler:^(EHIEnvironmentType type, BOOL didUpdate) {
            ehi_call(handler)();
        }];
    }];
}

- (void)showEnvironmentSelectionAlertForService:(EHIServicesEnvironmentType)service withCompletion:(void(^ __nullable)(void))handler
{
    void (^completion)(BOOL canceled, EHIEnvironmentType environmentType);

    if(service == EHIServicesEnvironmentTypeAEM) {
        completion = [^(BOOL canceled, EHIEnvironmentType environmentType) {
            if(canceled) {
                ehi_call(handler)();
                return;
            }
            
            [self setAEMEnvironment:environmentType handler:^(EHIEnvironmentType type, BOOL didUpdate) {
                ehi_call(handler)();
            }];
        } copy];
    } else {
        completion = [^(BOOL canceled, EHIEnvironmentType environmentType) {
            if(canceled) {
                ehi_call(handler)();
                return;
            }
            
            [self setGBOEnvironment:environmentType handler:^(EHIEnvironmentType type, BOOL didUpdate) {
                [[EHIConfiguration configuration] refreshCountries];
                if (didUpdate) {
                    [[EHIUserManager sharedInstance] logoutCurrentUser];
                }
                
                ehi_call(handler)();
            }];
        } copy];
    }

    [[self configurationForService:service] showEnvironmentSelectionAlertWithCompletion:completion];
}

- (void)showSearchEnvironmentSelectionAlertWithCompletion:(void(^ __nullable)(void))handler
{
#if defined(DEBUG) || defined(UAT)
    EHIAlertViewBuilder *alertView = EHIAlertViewBuilder.new
    .title(@"Set Search Environment")
    .message(@"Which search environment should be used?");
    
    // the count is the number of envs less the final environment (for debug we are assuming last
    // env is unprotected PROD)
    @(0).upTo(EHISearchEnvironmentTypeNumEnvironments - 1).each(^(NSNumber *number, int index) {
        NSString *name = [EHISearchEnvironmentTypeTransformer() reverseTransformedValue:@(index)];
        alertView.button(name);
    });
    
    alertView.cancelButton(@"Cancel");
    
    alertView.show(^(NSInteger index, BOOL canceled) {
        // ignore and call handler immediately on cancel
        if(canceled) {
            ehi_call(handler)();
            return;
        }
        
        // attempt to update environment
        EHISearchEnvironmentType updatedType = (EHISearchEnvironmentType)index;
        EHISearchEnvironment *current = [EHISearchEnvironment unarchive];
        [current setType:updatedType];
        
        [self update];
        
        ehi_call(handler)();
    });
#endif
}

- (void)update
{
    self.searchEnvironment = [EHISearchEnvironment unarchive];
}

- (id<EHIServicesEnvironmentConfiguration>)configurationForService:(EHIServicesEnvironmentType)service
{
    if(service == EHIServicesEnvironmentTypeAEM) {
        return [EHIAEMEnvironment serviceWithEnvironment:self.aemEnvironment];
    }

    return [EHIGBOEnvironment serviceWithType:service forEnvironment:self.gboEnvironment];
}

@end
