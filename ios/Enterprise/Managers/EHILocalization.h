//
//  EHILocalization.h
//  Enterprise
//
//  Created by Ty Cobb on 2/11/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#define EHILocalizedString(_key, _default, _comment) ([EHILocalization localizeKey:_key fallback:_default])

typedef NS_ENUM(NSInteger, EHIStringBehavior) {
	EHIStringBehaviorDefault = 0,
	EHIStringBehaviorDisplayRawKeys,
	EHIStringBehaviorObscureMissingKeys,
	EHIStringBehaviorShowMissingKeys,
	EHIStringBehaviorObscureAllKeys
};

@interface EHILocalization : NSObject

/** 
 @brief Attempts localize the string with the given key
 
 If no localization is found, then the fallback is returned instead
*/

+ (NSString *)localizeKey:(NSString *)key fallback:(NSString *)fallback;

/**
 @brief The current string behavior
 
 */
+ (EHIStringBehavior)stringBehavior;

/**
 @brief Changes the behavior of the localizer to expose keys differently
 
 */
+ (void)setStringBehavior:(EHIStringBehavior)stringBehavior;

/**
 @brief Convenience method to convert @c EHIStringBehavior enum to display string
 
 */
+ (NSString *)nameForStringBehavior:(EHIStringBehavior)stringBehavior;

@end
