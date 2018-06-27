//
//  CALayer+Utility.h
//  Enterprise
//
//  Created by Ty Cobb on 4/1/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@interface CALayer (Utility)

/** Factory for creating a layer with the specified frame. Layers have a black background. */
+ (CALayer *)ehi_layerWithFrame:(CGRect)frame;
/** Performs the block, disabling implicit animations if @c animated is @c NO */
+ (void)ehi_performAnimated:(BOOL)animated transform:(void(^)(void))transform;
/** Performs the @c transform with layer animations disabled */
+ (void)ehi_performUnanimated:(void(^)(void))transform;
/** Performs the @c transform using the given @c duration, optionally animated */
+ (void)ehi_animate:(BOOL)animated duration:(NSTimeInterval)duration transform:(void (^)(void))transform;

/**
 @brief Returns a basic animation for the given key
 
 The @c fromValue for the animation is set from whatever the current value is on the
 layer's @c presentationLayer.
 
 @param key The key to create the animation for
*/

- (CAAnimation *)ehi_implicitAnimationForKey:(NSString *)key;

@end
