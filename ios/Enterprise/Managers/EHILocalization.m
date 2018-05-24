//
//  EHILocalization.m
//  Enterprise
//
//  Created by Ty Cobb on 2/11/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocalization.h"
#import "NSLocale+Country.h"

#define EHIStringBehaviorTypeKey @"EHIStringBehaviorTypeKey"
#define EHIDefaultStringBehaviorType EHIStringBehaviorDefault
#define EHILocalizationDefaultIdentifier @"en_GB"

@interface EHILocalization ()
@property (strong, nonatomic) NSDictionary *localizations;
@property (assign, nonatomic) BOOL localizationFileFailed;
@property (assign, nonatomic) EHIStringBehavior stringBehavior;
@end

@implementation EHILocalization

+ (EHILocalization *)sharedInstance
{
    static EHILocalization *sharedInstance;
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [self new];
    });
    
    return sharedInstance;
}

- (instancetype)init
{
    if(self = [super init]) {
		self.stringBehavior = [self unarchiveType];
        _localizations = [self loadLocalizations];
    }
    
    return self;
}

# pragma mark - String Behavior

- (EHIStringBehavior)unarchiveType
{
	NSNumber *storedType = [[NSUserDefaults standardUserDefaults] objectForKey:EHIStringBehaviorTypeKey];
	
	// if we don't have a stored type yet, use the defaults
	if(!storedType) {
		return EHIDefaultStringBehaviorType;
	}
	
	return storedType.integerValue;
}

+ (EHIStringBehavior)stringBehavior
{
    return [self sharedInstance].stringBehavior;
}

+ (void)setStringBehavior:(EHIStringBehavior)stringBehavior
{
	// update the stored type
	[[NSUserDefaults standardUserDefaults] setObject:@(stringBehavior) forKey:EHIStringBehaviorTypeKey];
	[[self sharedInstance] setStringBehavior:stringBehavior];
}

+ (NSString *)nameForStringBehavior:(EHIStringBehavior)stringBehavior
{
    switch(stringBehavior) {
        case EHIStringBehaviorDefault:
            return @"Normal Behavior";
        case EHIStringBehaviorDisplayRawKeys:
            return @"Show Keys";
        case EHIStringBehaviorObscureMissingKeys:
            return @"Mask Only Missing Keys";
        case EHIStringBehaviorShowMissingKeys:
            return @"Show Only Missing Keys";
        case EHIStringBehaviorObscureAllKeys:
            return @"Mask All Strings";
    }
}

# pragma mark - String Mapping

+ (NSString *)localizeKey:(NSString *)key fallback:(NSString *)fallback
{
    return [[self sharedInstance] localizeKey:key fallback:fallback];
}

- (NSString *)localizeKey:(NSString *)key fallback:(NSString *)fallback
{
	switch (self.stringBehavior)
	{
		case EHIStringBehaviorDefault:
			return self.localizations[key] ?: fallback;
			break;
		case EHIStringBehaviorDisplayRawKeys:
			return key;
			break;
		case EHIStringBehaviorObscureMissingKeys:
			return self.localizations[key] ?: [fallback stringByReplacingMatchesForRegex:@"." withTemplate:@"∆"];
			break;
		case EHIStringBehaviorShowMissingKeys:
			return self.localizations[key] ?: key;
			break;
		case EHIStringBehaviorObscureAllKeys:
			return [fallback stringByReplacingMatchesForRegex:@"." withTemplate:@"∆"];
	}
}

# pragma mark - Localizations

- (NSDictionary *)loadLocalizations
{
    NSDictionary *localizations = nil;

    // load the current regions localizations, or the defualt if none found
    NSURL *localizationsUrl = [self localizationUrl];
    
    // load the localization data from the bundle
    NSData *localizationsData = [NSData dataWithContentsOfURL:localizationsUrl];
   
    // deserialize the data into JSON
    NSError *error;
    if(localizationsData) {
        localizations = [NSJSONSerialization JSONObjectWithData:localizationsData options:0 error:&error];
    }
    
    if(error) {
        EHIError(@"Error deserializing localizations: %@", error.userInfo.allValues.firstObject);
    } else if(!localizations) {
        EHIError(@"No localizations found for locale: %@", [NSLocale autoupdatingCurrentLocale].localeIdentifier);
    }
    
    return localizations;
}

- (NSURL *)localizationUrl {
    return [self localizationsUrlForRegionIdentifier:NSLocale.ehi_identifier]
        //exact match for locale found in translaction
        ?:[self localizationsUrlForRegionIdentifier:NSLocale.ehi_language]
        //trying to find general language like ed,de,fr in translations
        ?:[self localizationsUrlForRegionIdentifier:EHILocalizationDefaultIdentifier];
}

//
// Helpers
//

- (NSURL *)localizationsUrlForRegionIdentifier:(NSString *)identifier
{
    NSString *resource = [NSString stringWithFormat:@"localizable.%@", identifier];
    return [[NSBundle mainBundle] URLForResource:resource withExtension:@"json"];
}

@end
