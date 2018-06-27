//
//  EHILocationDetailsViewModel.h
//  Enterprise
//
//  Created by Pawel Bragoszewski on 12.02.2015.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHILocation.h"
#import "EHISectionHeaderModel.h"

typedef NS_ENUM(NSInteger, EHILocationDetailsSection) {
    EHILocationDetailsSectionMap,
    EHILocationDetailsSectionConflicts,
    EHILocationDetailsSectionInfo,
    EHILocationDetailsSectionHours,
    EHILocationDetailsSectionPickup,
    EHILocationDetailsSectionPolicies,
};

typedef void (^EHILocationDetailsReservationDatesBlock)();

@class EHILocationDetailsConflictViewModel;
@class EHILocationDetailsInfoViewModel;
@interface EHILocationDetailsViewModel : EHIViewModel <MTRReactive>

/** The location data model backing this view model */
@property (strong, nonatomic, readonly) EHILocation *location;
/** The location data model for the @c EHILocationDetailsSectionPickup; may be @c nil if not shown */
@property (strong, nonatomic, readonly) EHILocation *pickupLocation;
/** An array of @c EHILocationTimes models for this location */
@property (copy, nonatomic, readonly) NSArray *hours;
/** An array of @c EHILocationPolicy models for this location; may contain a placholder model for "More Policies" */
@property (copy, nonatomic, readonly) NSArray *policies;
/** All the policies available for the location; nonreactive */
@property (copy, nonatomic, readonly) NSArray *allPolicies;

@property (strong, nonatomic, readonly) EHILocationDetailsConflictViewModel *conflictsModel;
@property (strong, nonatomic, readonly) EHILocationDetailsInfoViewModel *infoModel;

/** The title for the location details screen */
@property (copy, nonatomic, readonly) NSString *title;
/** The caption for start reservation button */
@property (copy, nonatomic, readonly) NSString *actionTitle;

/** @c YES if the details for the location are being fetched */
@property (assign, nonatomic, readonly) BOOL isLoading;
/** @c YES if this location is from the primary brand */
@property (assign, nonatomic, readonly) BOOL isOnBrand;
/** @c NO if the user should be allowed to select the location */
@property (assign, nonatomic) BOOL disablesSelection;
@property (copy  , nonatomic) EHILocationDetailsReservationDatesBlock computeDatesBlock;

/**
 @c Drives the policy selection logic
 
 If an individual policy is selected, this method returns that policy. If the "More Policies"
 button is tapped, returns the list of @c allPolicies.
 
 @param indexPath The index path of the tapped cell
 @return The selected policy, or the @c allPolicies array
*/
 
- (void)showPolicyAtIndexPath:(NSIndexPath *)indexPath;

/** Handles the navigation and model passing required for selecting a location */
- (void)selectLocation;
/** Retuns the section header for the given section, if any */
- (EHISectionHeaderModel *)headerForSection:(EHILocationDetailsSection)section;

@end
