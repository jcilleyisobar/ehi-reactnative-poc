//
//  EHIAnalyticsContext.h
//  Enterprise
//
//  Created by Ty Cobb on 5/28/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@protocol EHIAnalyticsEncodable;

typedef NS_ENUM(NSInteger, EHIAnalyticsActionType) {
    EHIAnalyticsActionTypeNone,
    EHIAnalyticsActionTypeTap,
    EHIAnalyticsActionTypeType,
    EHIAnalyticsActionTypeScroll,
    EHIAnalyticsActionTypeError,
};

NS_ASSUME_NONNULL_BEGIN

@interface EHIAnalyticsContext : EHIEncodableObject 

typedef void(^EHIAnalyticsContextHandler)(EHIAnalyticsContext *);

/** The analytics tag for the currently tracked screen */
@property (copy  , nonatomic, nullable) NSString *screen;
/** The analytics tag for the currently tracked state */
@property (copy  , nonatomic, nullable) NSString *state;
/** The analytics tag for the current action; @c nil'd out immediately after tracking */
@property (copy  , nonatomic, nullable) NSString *action;
/** The type of action, if any, for the current action; zero'd after tracking */
@property (assign, nonatomic) EHIAnalyticsActionType actionType;
/** The customer value delta to send; not sent if zero, and reset after each call */
@property (assign, nonatomic) NSInteger customerValue;
/** The macro event name to send on top of normal events; reset after each call */
@property (copy  , nonatomic, nullable) NSString *macroEvent;

/** The previous screen path, or @c nil of none existed */
@property (copy  , nonatomic, nullable) NSString *previousPath;
/** The key for the internal, router screen name */
@property (copy  , nonatomic, nullable) NSString *screenKey;

/**
 @brief Updates the analytics screen from the provided router screen
 
 The context maintains an internal map of router screen -> analytics tag. If no mapping 
 exists for the parameterized @c screen, this method throws an exception
*/

- (void)setRouterScreen:(NSString *)screen;

/**
 @brief Updates the analytics state from the provided router screen
 
 The context maintains an internal map of router screen -> analytics tag. If no mapping 
 exists for the parameterized @c screen, this method throws an exception
 
 @param screen The screen to track
*/

- (void)setRouterState:(NSString *)screen;

/**
 @brief Updates the state, optionall updating @c previousPath.
 
 If @c isSilent is @c YES, then the previous path is left intact. Otherwise, it is
 updated to whatever the current @c path value is.
 
 @param state    The state to update to
 @param isSilent @c NO if the previous path should be updated
*/

- (void)setState:(nullable NSString *)state silent:(BOOL)isSilent;

/** 
 @brief Updates the context with the given encodable object
 
 This method is a pass-through to @c -encode:instance:prefix, with @c prefix as @c nil. See
 that method for complete documentation.
*/

- (void)encode:(Class<EHIAnalyticsEncodable>)klass encodable:(nullable id<EHIAnalyticsEncodable>)encodable;

/**
 @brief Updates the context with the given encodable object
 
 A default implementation of @c EHIAnaylticsEncodable is provided for @c NSDictionary, but
 custom objects should roll their own implementation.
 
 If @c prefix is specified, it will be prepended onto every key added to the attributes.

 @param klass     The type of object to encode
 @param encodable The object to encode into the attributes, or @c nil
 @param prefix    The string prefix for all the added keys
*/

- (void)encode:(Class<EHIAnalyticsEncodable>)klass encodable:(nullable id<EHIAnalyticsEncodable>)encodable prefix:(nullable NSString *)prefix;

/**
 @brief Encodes all updates in the block using the specified key prefix
 
 The prefix is restored to it's previous value once the handler completes. The prefix
 is applied to all keys set on the context
 
 @param prefix  The prefix for all keys set in the handler
 @param handler The handler that updates the context
*/

- (void)encodeWithPrefix:(nullable NSString *)prefix handler:(EHIAnalyticsContextHandler)handler;

/**
 + Deep copy all the properties
 + */
- (EHIAnalyticsContext *)clone;

@end

@interface EHIAnalyticsContext (Subscripting)

/**
 @brief Updates the analytics context for a specific key-value pair

 If the value of @c object is @c nil, the value will be removed from the context data. If
 @c object conforms to @c EHIAnalyticsEncodable, it will be encoded into a sub-context at
 the given @c key.
 
 @param object The object to set for the given key, or @c nil
 @param key    The key for the the object to add to the analytics dictionary
*/

- (void)setObject:(nullable id)object forKeyedSubscript:(NSString *)key;

/** 
 Reads a value from the current analytics context
 
 @param key The key for the analytics key-value pair to read
 @return The current value in the analytics context, or @c nil
*/

- (nullable id)objectForKeyedSubscript:(NSString *)key;

@end

@interface EHIAnalyticsContext (Temporary)

/**
 @brief Adds temporary attributes to the handler
 
 This method accepts a context handler block, and any attributes added within that 
 block are held in temporary storage.
 
 The temporary storage is destroyed after every tracking call, or when @c clearTemporaryAttributes
 is called.
 
 @param handler The handler to update temporary attributes
*/

- (void)applyTemporaryAttributes:(EHIAnalyticsContextHandler)handler;

/**
 @brief Removes any existing temporary attributes
 
 This is called as part of the tracking cycle after each tracking call, but can be
 called manually with caution.
*/

- (void)clearTemporaryAttributes;

@end

NS_ASSUME_NONNULL_END
