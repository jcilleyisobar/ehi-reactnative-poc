//
//  EHIViewController.h
//  Enterprise
//
//  Created by Ty Cobb on 1/9/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIAnalytics.h"
#import "EHIAnalyticsUpdater.h"
#import "EHIMainRouter.h"
#import "EHINavigationAnimation.h"
#import "NAVViewController.h"

#define EHISearchFieldInsets ((UIOffset){ .horizontal = 42.0f, .vertical = 6.0f })

typedef NS_ENUM(NSUInteger, EHIModalTransitionStyle) {
    EHIModalTransitionStyleDefault,
    EHIModalTransitionStyleOverlay,
    EHIModalTransitionStyleOverlayFullscreen,
};

@class EHIModalOverlayTransitioningDelegate;
@interface EHIViewController : NAVViewController

/**
 @brief The view model backing this view controller
 
 Subclasses with a corresponding view model subclass should redeclare this property
 in their extension with the correct type.
*/

@property (strong, nonatomic) EHIViewModel *viewModel;

/**
 @brief Indicates the view has been laid out at least once
 @c YES if the view has been configured and is ready for interaction.
*/

@property (nonatomic, readonly) BOOL isReady;

/**
 @brief The modal transition to use when presenting this view controller
 
 If the value of this property is anything other than @c EHIModalTransitionStyleDefault, the
 value of @c modalPresentationStyle is implicitly set to @c UIModalPresentationStyleCustom.
*/

@property (assign, nonatomic) EHIModalTransitionStyle customModalTransitionStyle;

/**
 @brief Indicates if a view controller needs to be dismissed when the user tap outside of it's bounds.
 Typically used with EHIModalTransitionStyleOverlay
 */

@property (assign, nonatomic) BOOL needsAutoDismiss;

/**
 @brief The background for the view controller's view.
 Default is [UIColor ehi_greenColor].
 */

@property (strong, nonatomic) UIColor *backgroundColor;

/**
 @brief @c YES if should add a white line at the bottom
 Will insert it after the last view
 */

@property (assign, nonatomic) BOOL needsBottomLine;

/**
 @brief Invalidates bottom view which overlaps the bottom of safe area
 */

- (void)invalidateViewBelowSafeArea:(BOOL)isDisabled;
    
- (void)registerAccessibilityIdentifiers;

@end

@interface EHIViewController (SubclassingHooks)

/**
 Provides a hook to register reactions 
 
 @param model The view controller's view model
*/

- (void)registerReactions:(id)model;

/**
 @brief Provides a hook for subclasses to access the view after its been laid out correctly
 
 This method is called once, before @c -viewDidAppear: after the controller's view has been
 sized properly.
*/

- (void)willBecomeReady;

/**
 @brief Allows the the view controller to update its navigation item
 
 The navigation item can be updated elsewhere, but this hook provides a commond interface for
 subclasses to do so.
 
 @param item This view controller's navigation item
*/

- (void)updateNavigationItem:(UINavigationItem *)item;

/**
 @brief Allows the view controller subclass to override back button functionality
 
 This class provides a back button as the left navigation item by default. Overriding 
 this function allows you to change the behavior when the back button is tapped. 
 */

- (void)didTapBackButton:(UIButton *)button;

/** 
 @c YES if this view controller wants to display the fixed phone button; defaults to @c NO 
 */

- (BOOL)showsPhoneButton;

- (void)overlayTransitionDidTapOverlayContainer:(EHIModalOverlayTransitioningDelegate *)delegate;

@end

@interface EHIViewController (Analytics) <EHIAnalyticsUpdater>

/**
 @brief Called just before the view controller is asked to update the analytics context
 
 Subclasses should override this method if they need to switch contexts beforehand by
 calling @c +changeScreen:.
*/

- (void)prepareToUpdateAnalyticsContext;

/** 
 @brief Called just after the view controller updates the analytics context
 
 Subclasses should override this to make any necessary "on load" type state tracking calls.
*/

- (void)didUpdateAnalyticsContext;

/**
 @brief Re-runs the analytics flow
 
 In most cases, the analytics flow should be called at the appropriate points during the view
 controller lifecycle. This should only be called to update the analytics context in non-standard
 circumstances.
*/

- (void)invalidateAnalyticsContext;

/**
 @brief @c YES if the context should be invalidated automatically
 
 Defaults to @c YES, and normally happens in @c -viewWillAppear:. If a view controller needs to
 sidestep this behavior in non-standard circumstances, it can override this method.
*/

- (BOOL)automaticallyInvalidatesAnalyticsContext;

@end

@interface EHIViewController (Transitioning)

/**
 @brief Hook for subclasses to determine whether a custom transition should run
 
 If this method returns @c YES, @c -animationsForTransitionToViewController will be called
 on the view controller to get the custom animations.
 
 By default, this method returns @c NO and the standard wipe transition is used.
 
 @param controller The other view controller involved in this transition
 @param isEntering Whether this controller is entering into view via this transition
 
 @return A boolean indicated if custom animations should be used
*/

- (BOOL)executesCustomAnimationsForTransitionToViewController:(EHIViewController *)controller isEntering:(BOOL)isEntering;

/**
 @brief Generates the custom animations to use for the transition
 
 Implementers should return an array of @c EHINavigationAnimationBuilders, which can be created
 by calling @c -build on @c EHINavigationAnimation.
 
 @param controller The other view controller involved in this transition
 @param isEntering Whether this controller is entering into view via this transition
 
 @return An array of @c EHINavigationAnimationBuilders
*/

- (NSArray *)animationsForTransitionToViewController:(EHIViewController *)controller isEntering:(BOOL)isEntering;

@end

@interface EHIViewController (Keyboard)

/**
 @brief Dismisses the keyboard when a tap is recieved anywhere on the view, outside the keyboard bounds
 
 Subclasses should override and return @c YES to opt in to this functionality
*/

- (BOOL)requiresKeyboardSupport;

/** 
 @brief Hook that allows subclasses to expose a scroll view to react to keyboard notifications
 
 The content inset of the scroll view will be adjusted to accommodate the keyboard as it comes on 
 and off screen. Subclasses should override and return a scroll view if they would like to opt 
 into keyboard support.
*/

- (UIScrollView *)keyboardSupportedScrollView;

/** 
 @brief Hook that allows subclasses to expose a button to react to keyboard notifications
 
 The button will have its bottom constraint adjusted to accomodate the keyboard as it comes on 
 and off screen. Subclasses should override and return a button if they would like to opt into
 keyboard support.
 */

- (UIButton *)keyboardSupportedActionButton;

/**
 @brief Hook allowing subclasses control over which touches dismiss the keyboard
 
 By default, this method returns @c YES for all touches. Subclasses should inspect the touch and/or
 its view to determine if it should allow dismissal or not.
*/

- (BOOL)shouldDismissKeyboardForTouch:(UITouch *)touch;

/** 
 @brief Hook to apply custom insets to your content to accomodate the keyboard as it shows and hides
 
 Opting in to requiresKeyboardSupport in a subclass will enroll you in the keyboard will show and hide notifications.
 This hook will be called when the keyboard shows/hides with the appropriate @c shouldInset value.
*/

- (void)applyKeyboardInsets:(BOOL)shouldInset forNotification:(NSNotification *)notification;

@end

@interface EHIViewController (Security)

/**
 @brief Shows a security image whenever this view is on screen and the app is backgrounded
 
 Subclasses should override and return @c YES to opt in to this functionality.
*/

- (BOOL)hasSecureContent;

@end
