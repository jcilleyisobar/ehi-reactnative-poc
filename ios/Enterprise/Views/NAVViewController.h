//
//  NAVTransitionController.h
//  Enterprise
//
//  Created by Alex Koller on 11/16/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "NAVTransitionPerformer.h"
#import "NAVTransitionDestination.h"
#import "NAVPreview.h"

@protocol NAVViewController

/**
 @brief Factory method to construct view controller instances
 
 The default implementation attempts to load the view controller from the
 storyboard specified by @c +storyboardName with the identifier matching the name
 of this view controller's class.
 
 Subclasses may override this method to perform custom instantiation.
 
 @return A new view controller instance
 */

+ (instancetype)instance;

/**
 @brief Specifies the storyboard identifier for this view controller.
 
 The default implementation returns a stringified version of the class name. Subclasses
 may override this method to specify a custom identifier.
 
 @return The storyboard identifier for this view controller.
 */

+ (NSString *)storyboardIdentifier;

/**
 @brief Specifies the name of the storyboard assoscaited with this view controller
 
 The default implemenation throws an exception. Subclasses should implement this method
 so that view contorller's can be constructed successfully.
 
 @return The name of the assosciated storyboard
 */

+ (NSString *)storyboardName;

/**
 @brief The name associated with a specific @c NAVViewController subclass
 
 The default implementation returns @c nil. Subclasses should implement this method and
 return a unique name for the screen. This name is used when setting destinations for
 @c NAVTransitions.
 
 @return The unique name of the view controller
 */

+ (NSString *)screenName;

@end

@interface NAVViewController : UIViewController <NAVViewController, NAVTransitionPerformer, NAVTransitionDestination>

@end

@interface NAVViewController (Previewing)

/**
 @brief A list of views that detect 3D Touch
 
 The default implementation returns @c nil. Subclasses can implement this method to
 automatically register for previewing (aka Peek Pop). If implemented, @c -previewingContext:transitionForLocation:
 will be called whenever a 3D touch is detected in one of the source views.

 @return Views that initiate previewing
 */

- (NSArray *)previewingSourceViews;

/**
 @brief Creates NAVPreview that describes a peek-pop transition
 
 The default implementation returns @c nil which performs no previewing. Called when
 a 3D touch is detected within a previewing source view. Provides @c previewingContext
 to make customizations to the preview UI. 
 
 @note @c UICollectionView source views automatically focus the selected cell.
 
 @param previewingContext Context of the preview. Has reference to source view that initiated preview.
 @param location          The point touched relative to the source view
 
 @return The NAVPreview to use to coordinate a peek-pop transition
 */

- (NAVPreview *)previewingContext:(id<UIViewControllerPreviewing>)previewingContext previewForLocation:(CGPoint)location;


@end