//
//  EHIConfigurationHandler.h
//  Enterprise
//
//  Created by Ty Cobb on 6/19/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

NS_ASSUME_NONNULL_BEGIN

@interface EHIConfigurationHandler : NSObject

typedef void(^EHIConfigurationCallback)(BOOL isReady);

/** The block to call back when complete */
@property (copy  , nonatomic, readonly) EHIConfigurationCallback block;
/** @c YES if the handler should ignore callbacks that aren't ready */
@property (assign, nonatomic) BOOL waitsUntilReady;

/** Constructs a new callback with the @c block */
- (instancetype)initWithBlock:(EHIConfigurationCallback)block;

@end

NS_ASSUME_NONNULL_END
