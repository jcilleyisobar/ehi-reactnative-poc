//
//  FSUserProfile.h
//  Foresee
//
//  Created by Wayne Burkett on 2/14/17.
//  Copyright © 2017 Foresee. All rights reserved.
//

/**
 The `FSUserProfile` provides user profile information to the ForeSee SDK
 
 Instances of this class can be registered with the SDK using @c ForeSee#setUserProfile:
 */
@interface FSUserProfile : NSObject

/** An email address for the user
 */
@property (nonatomic, copy) NSString *email;

/** A phone number for the user
 */
@property (nonatomic, copy) NSString *phoneNumber;

/** A Facebook handle for the user
 */
@property (nonatomic, copy) NSString *facebookHandle;

/** A Twitter handle for the user
 */
@property (nonatomic, copy) NSString *twitterHandle;

/** An ID for the user
 */
@property (nonatomic, copy) NSString *userId;

/** Sets a string value for the given key for this user.
 
 @param value a string value
 @param key the key
 */
- (void)setString:(NSString *)value forKey:(NSString *)key;

/** Sets a numeric value for the given key for this user.
 
 @param value a numeric value
 @param key the key
 */
- (void)setNumber:(NSNumber *)value forKey:(NSString *)key;

/** Sets a boolean value for the given key for this user.
 
 @param value a boolean value
 @param key the key
 */
- (void)setBoolean:(BOOL)value forKey:(NSString *)key;

/** Creates and returns a dictionary containing all previously set user profile values.
 
 This method returns all defined keys (i.e. those set using existing properties) and values set
 using the generic typed setters.
 
 @return a dictionary containing all user properties
 */
- (NSDictionary *)toDictionary;

@end
