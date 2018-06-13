//
//  FSTriggerCriteria.h
//  Foresee
//
//  Created by Wayne Burkett on 4/16/15.
//  Copyright (c) 2015 Foresee. All rights reserved.
//

/**
 A `FSTriggerCriteria` instance defines a measure's triggering criteria.
 */
@interface FSTriggerCriteria : NSObject

/** A key/value collection that defines custom trigger parameters
 for this measure.
 */
@property (nonatomic, readonly) NSDictionary *significantEvents;

/** Defines the number of app launches that need to be exceeded before the
 user is eligible to be invited to take the survey associated with this
 measure.
 */
@property (nonatomic, readonly) NSUInteger launchCount;

/** Defines the number of days since the app was first launched that must
 be exceeded for the user to be invited to take the survey associated
 with this measure.
 */
@property (nonatomic, readonly) NSUInteger daysSinceFirstLaunch;

/** Defines the number of days since the app was last launched that must
 be exceeded for the user to be invited to take the survey associated
 with this measure.
 */
@property (nonatomic, readonly) NSUInteger daysSinceLastLaunch;

/** Defines the number of view controllers that must be loaded ("page views")
 before the user is eligible to be invited to take the survey associated
 with this measure.
 */
@property (nonatomic, readonly) NSUInteger pageViews;

@end
