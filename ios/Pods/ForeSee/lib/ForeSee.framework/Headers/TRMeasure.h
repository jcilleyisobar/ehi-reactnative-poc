//
//  TRMeasure.h
//
//  Created by Michael Han on 12-04-10.
//  Copyright (c) 2012 ForeSee. All rights reserved.
//

@class FSTriggerCriteria;

/**
 A measure defines the triggering thresholds for a given survey. When one of
 the thresholds is exceeded, the Trigger Library will present an invitation to the user.
 */
@interface TRMeasure : NSObject <NSCoding>

#pragma mark - Survey ID

/** The name of the survey associated with this measure. */
@property (nonatomic, retain) NSString *surveyID;

#pragma mark - Criteria

/** The measure's triggering criteria. If any value in the primary criteria 
 is exceeded, then the user becomes eligible to see a survey invitation. */
@property (nonatomic, readonly) FSTriggerCriteria *primaryCriteria;

/** The measure's combined triggering criteria (if any). In any single combined criteria, 
 all values must be exceeded for the user to be eligible to see a survey invitaton. */
@property (nonatomic, readonly) NSArray<FSTriggerCriteria *> *combinedCriteria;

@end
