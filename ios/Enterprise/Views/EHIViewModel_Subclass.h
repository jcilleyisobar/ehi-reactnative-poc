//
//  EHIViewModel_Subclass.h
//  Enterprise
//
//  Created by Ty Cobb on 1/28/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIAnalytics.h"

@interface EHIViewModel (SubclassingHooks)

/**
 @brief Called once after @c -initWithModel: completes
 
 Subclasses can use this method to perform an one-time asynchronous operations that are
 dependent on the model being fully configured, such as making network requests.
 
 @note This method may be called during the next run-loop cycle after initialization.
*/

- (void)didInitialize;

/**
 @brief Called internally when @c isActive becomes @c YES
 
 Subclasses can use this method to perform any logic that needs to happen
 whenenver the view is coming on-screen.
*/

- (void)didBecomeActive;

/**
 @brief Called internally when @c isActive becomes @c NO
 
 Subclasses can use this method to perform any logic that needs to happen
 whenever the view is moving off-screen.
*/

- (void)didResignActive;

/**
 @brief The router corresponding to this view model
 
 Default implementation returns either the EHIMainRouter or the EHIReservationRouter
 depending on whether the EHIReservationBuilder is active or not
*/

- (EHIRouter *)router;

@end
