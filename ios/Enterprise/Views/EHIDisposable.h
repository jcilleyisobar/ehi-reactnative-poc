//
//  EHIDisposable.h
//  Enterprise
//
//  Created by Ty Cobb on 7/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

NS_ASSUME_NONNULL_BEGIN

@interface EHIDisposable : NSObject

/** 
 @brief The number of objects currently referring to this disposable

 If this number ever drops to @c 0, and there is non-nil @c element, it will be
 removed immediately.
*/

@property (assign, nonatomic) NSInteger referents;

/**
 @brief The object this disposable is managing
 
 The element is automatically removed whenever the @c referenceCount drops to 0.
*/

@property (strong, nonatomic, nullable) id element;

/**
 @brief Intiailizes a new disposable with an element @c generator

 The @c generator is a block responsible for constructing an @c element instance when
 asked by the disposable.

 @param generator A block that generates @c elements
*/

- (instancetype)initWithGenerator:(id(^)(void))generator;

@end

NS_ASSUME_NONNULL_END
