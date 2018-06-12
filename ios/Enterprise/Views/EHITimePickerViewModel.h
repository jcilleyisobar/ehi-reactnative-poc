//
//  EHITimePickerViewModel.h
//  Enterprise
//
//  Created by mplace on 3/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHITimePickerTime.h"
#import "EHIButtonType.h"

@interface EHITimePickerViewModel : EHIViewModel <MTRReactive>

/** title for the screen */
@property (copy  , nonatomic) NSString *title;
/** array of times to be rendered in the time selector */
@property (copy  , nonatomic) NSArray *times;
/** title for the time selection button */
@property (copy  , nonatomic) NSString *selectionButtonTitle;
/** title for the time info button (based on after hours/closed) */
@property (copy  , nonatomic) NSString *infoButtonTitle;
/** title for the show locations on map view) */
@property (copy  , nonatomic) NSString *needLocationOpenLabelTitle;
/** call to action title for the search show locations on map view button) */
@property (copy  , nonatomic) NSString *searchForLocationsButtonTitle;
@property (copy  , nonatomic, readonly) NSString *lastReturnTimeTitle;
@property (copy  , nonatomic, readonly) NSString *lastReturnTimeText;
/** image name for the time info button (based on after hours/closed) */
@property (assign, nonatomic) EHIButtonType infoButtonType;
/** @c YES if the currently selected time is not after hours*/
@property (assign, nonatomic) BOOL infoButtonIsHidden;
@property (assign, nonatomic) BOOL isLastReturnTime;
/** @c YES if the info button should be selectable */
@property (assign, nonatomic) BOOL infoButtonIsSelectable;
/** @c YES if the current time is selectable */
@property (assign, nonatomic) BOOL currentTimeIsSelectable;
/** @c YES if the current time is closed */
@property (assign, nonatomic) BOOL currentTimeIsClosed;
/** @c YES if the times are currently loading */
@property (assign, nonatomic) BOOL isLoading;
/** @c YES if the user is currently choosing the return time */
@property (assign, nonatomic) BOOL isPickingReturnTime;
/** Updates the UI according to which index the time selector is looking at */
@property (strong, nonatomic) NSIndexPath *indexPathForCurrentTime;

/** @c YES if the time is selectable */
- (BOOL)shouldSelectTimeAtIndexPath:(NSIndexPath *)indexPath;
/** Selects the time at the provided index */
- (void)selectTimeAtIndexPath:(NSIndexPath *)indexPath;
/** Call to trigger the action assosciated with the info button */
- (void)triggerInfoAction;
/** Present the location map */
- (void)showLocationsMap;

/** provides the initial index to scroll to when the view appears */
- (NSIndexPath *)initialIndexPath;

@end
