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
#import "EHIServicesEnvironment.h"

#define EHISettingsEnvironmentTypeKey @"EHISettingsEnvironmentTypeKey"
#define EHISettingsEnvironmentBetaPassword @"Lex5My7H"

#ifdef UAT
#define EHIDefaultEnvironmentType EHIEnvironmentTypeRcQa
#elif DEBUG
#define EHIDefaultEnvironmentType EHIEnvironmentTypeRcQa
#elif PENTEST
#define EHIDefaultEnvironmentType EHIEnvironmentTypePenTest
#else
#define EHIDefaultEnvironmentType EHIEnvironmentTypeProd
#endif

@interface EHISettingsEnvironment ()
@property (strong, nonatomic) EHISearchEnvironment *searchEnvironment;
@property (copy  , nonatomic) NSString *services;
@property (copy  , nonatomic) NSString *search;
@property (copy  , nonatomic) NSString *searchApiKey;
@property (copy  , nonatomic) NSString *displayName;
@end

@implementation EHISettingsEnvironment

# pragma mark - Unarchiving

+ (instancetype)unarchive
{
    EHISettingsEnvironment *environment = [EHISettingsEnvironment new];
    
    environment.searchEnvironment = [EHISearchEnvironment unarchive];

    // on launch we're going to load the environment synchronously and _then_ authenticate. this allows the app
    // to start in a consistent state in the general case.
    environment.internalType = [self unarchiveType];
    
    // then authenticate, and if we fail revert to the default environment
    [environment authenticateEnvironment:environment.type withHandler:^(BOOL didAuthenticate) {
        if(!didAuthenticate) {
            environment.type = EHIDefaultEnvironmentType;
            // destroy the current user / credentials (if any)
            [[EHIUserManager sharedInstance] logoutCurrentUser];
        }
    }];
    
    return environment;
}

+ (EHIEnvironmentType)unarchiveType
{
	// if we are not DEBUG or UAT, we never archive our environment -- we should always be PROD
#if !defined(DEBUG) && !defined(UAT)
	return EHIDefaultEnvironmentType;
#else
    NSNumber *storedType = [[NSUserDefaults standardUserDefaults] objectForKey:EHISettingsEnvironmentTypeKey];
	
    // if we don't have a stored type yet, use the defaults
    if(!storedType) {
        return EHIDefaultEnvironmentType;
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

- (void)setType:(EHIEnvironmentType)type
{
    [self setType:type handler:nil];
}

- (void)setType:(EHIEnvironmentType)type handler:(void (^)(EHIEnvironmentType, BOOL))handler
{
    [self authenticateEnvironment:type withHandler:^(BOOL didAuthenticate) {
        if(didAuthenticate) {
            self.internalType = type;
        }
        
        ehi_call(handler)(self.type, didAuthenticate);
    }];
}

- (void)setInternalType:(EHIEnvironmentType)type
{
    _type = type;

    // update the stored type
    [[NSUserDefaults standardUserDefaults] setObject:@(type) forKey:EHISettingsEnvironmentTypeKey];

    // set a friendly name for debug display
    self.displayName = [EHISettingsEnvironment nameForEnvironment:type];
    
    // notify listeners of the environment change
    [[NSNotificationCenter defaultCenter] postNotificationName:EHISettingsEnvironmentChangedNotification object:nil];
}

- (void)setServices:(NSString *)services
{
    // ensure we have a trailing slash
    if(![services hasSuffix:@"/"]) {
        services = [services stringByAppendingString:@"/"];
    }

    if([_services isEqualToString:services]) {
        return;
    }
   
    _services = services;
    EHIDomainInfo(EHILogDomainNetwork, @"services base url: %@", services);
}

- (NSString *)serviceWithType:(EHIServicesEnvironmentType)servicesType
{
    NSString *domainURL = [EHIServicesEnvironment serviceWithType:servicesType forEnvironment:self.type].domainURL;

    // ensure we have a trailing slash
    if(![domainURL hasSuffix:@"/"]) {
        domainURL = [domainURL stringByAppendingString:@"/"];
    }

    return domainURL;
}

- (NSString *)servicesApiKeyWithType:(EHIServicesEnvironmentType)servicesType
{
    return [EHIServicesEnvironment serviceWithType:servicesType forEnvironment:self.type].apiKey;
}

- (NSString *)search
{
    return self.searchEnvironment.serviceURL;
}

- (NSString *)searchApiKey
{
    return self.searchEnvironment.apiKey;
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
        case EHIEnvironmentTypeEast:
        case EHIEnvironmentTypeWest:
            return YES;
        default:
            return NO;
    }
}

+ (NSString *)nameForEnvironment:(EHIEnvironmentType)environment
{
    switch (environment) {
		case EHIEnvironmentTypeSvcsQa:
			return @"SVCSQA";
		case EHIEnvironmentTypeRcQa:
			return @"RCQA";
        case EHIEnvironmentTypeHotHot:
            return @"HOT HOT";
		case EHIEnvironmentTypePrdSuPqa:
			return @"PRDSUPQA";
		case EHIEnvironmentTypeDev:
			return @"DEV";
        case EHIEnvironmentTypeDevQa:
            return @"DEV QA";
		case EHIEnvironmentTypeRcDev:
			return @"RCDEV";
        case EHIEnvironmentTypeTmpEnv:
            return @"TMPENV";
		case EHIEnvironmentTypePrdSuPdev:
			return @"PRDSUPDEV";
        case EHIEnvironmentTypePenTest:
            return @"PEN_TEST";
        case EHIEnvironmentTypeEast:
            return @"PROD_EAST";
        case EHIEnvironmentTypeWest:
            return @"PROD_WEST";
        case EHIEnvironmentTypeBeta:
		case EHIEnvironmentTypeProd:
			return @"PROD";
        case EHIEnvironmentTypePrdsup:
            return @"PRDSUP";
        case EHIEnvironmentTypeNumEnvironments:
            return nil;
    }
}

- (void)update
{
    self.searchEnvironment = [EHISearchEnvironment unarchive];
}

@end
