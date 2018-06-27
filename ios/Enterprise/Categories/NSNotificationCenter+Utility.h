//
//  NSNotificationCenter+Utility.h
//  Enterprise
//
//  Created by Ty Cobb on 04/13/15.
//  Copyright (c) 2014 Isobar. All rights reserved.
//

@protocol EHIKeyboardResponder;

@interface NSNotificationCenter (Utility)
/** Automatically de/registers for keyboard notifications */
+ (void)ehi_observeKeyboardNotifications:(BOOL)shouldObserve forTarget:(id<EHIKeyboardResponder>)target;
/** Pass-through to @c -addObserverForName:object:queue:usingBlock: with @c object and @c queue as @c nil */
- (id<NSObject>)ehi_observe:(NSString *)name block:(void(^)(NSNotification *))block;
/** Pass-through to @c -ehi_observe:block, but automatically de-registers itself after the first observation */
- (id<NSObject>)ehi_observeOnce:(NSString *)name block:(void(^)(NSNotification *))block;
@end

@interface NSNotification (Utility)

/** The animation duration used during the keyboard animation */
@property (nonatomic, readonly) CGFloat ehi_keyboardAnimationDuration;
/** The animation curve used during keyboard animation */
@property (nonatomic, readonly) UIViewAnimationCurve ehi_keyboardAnimationCurve;

/** 
 @brief Runs the given animations in sync with the keyboard 
    
 The animation has its duration / easing set automatically to match the system's
 animation. This method should be called from a keyboard notification to animate
 in-step.
*/

- (void)ehi_animateWithKeyboard:(void(^)(void))animations;

/** Returns the frame of the keyboard in the given view */
- (CGRect)ehi_keyboardFrameInView:(UIView *)view;
/** Returns the height of the view that is still visible after the keyboard is presented */
- (CGFloat)ehi_keyboardOverlapInView:(UIView *)view;

@end

@interface UIScrollView (Keyboard)

- (void)ehi_animateKeyboardNotification:(NSNotification *)notification additionalInset:(CGFloat)inset animations:(void(^)(void))animations;

@end

@protocol EHIKeyboardResponder <NSObject>
- (void)keyboardWillShow:(NSNotification *)notification;
- (void)keyboardWillHide:(NSNotification *)notification;
@end
