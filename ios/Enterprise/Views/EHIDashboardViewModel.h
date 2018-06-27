//
//  EHIDashboardViewModel.h
//  Enterprise
//
//  Created by Ty Cobb on 1/9/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIDashboardSections.h"
#import "EHICity.h"
#import "EHIDashboardPromotionCellViewModel.h"

typedef NS_ENUM(NSInteger, EHIContentSectionType) {
    EHIContentSectionTypeNone,
    EHIContentSectionTypeLoading,
    EHIContentSectionTypeCurrent,
    EHIContentSectionTypeUpcoming,
};

@interface EHIDashboardViewModel : EHIViewModel <MTRReactive>

/** The title for the search section */
@property (copy  , nonatomic, readonly) NSString *searchTitle;
/** The title for the quickstart section */
@property (copy  , nonatomic, readonly) NSString *quickstartTitle;
/** The title for the quickstart footer */
@property (copy  , nonatomic, readonly) NSString *clearActivityTitle;

/** The name of the parallaxing background image, if any */
@property (copy  , nonatomic, readonly) NSString *backgroundImageName;

/** The current type of content section */
@property (assign, nonatomic, readonly) EHIContentSectionType contentType;
/** The model to display in the content section, if any */
@property (strong, nonatomic, readonly) EHIModel *contentModel;

/** The models to display in the quickstart section */
@property (copy  , nonatomic, readonly) NSArray *quickstartModels;

/** @c YES if the quickstart section should display the fallback cell */
@property (assign, nonatomic, readonly) BOOL showsHistoryFallback;
/** @c YES if the dashboard is currently loading data */
@property (assign, nonatomic, readonly) BOOL isLoading; 
/** A placehodler to enable/disable the hero imager */
@property (strong, nonatomic, readonly) EHIModel *heroImageModel;
/** A placeholder to enable/disable the loyalty section */
@property (strong, nonatomic, readonly) EHIModel *loyaltyPromptModel;

@property (strong, nonatomic, readonly) EHIModel *notificationModel;
@property (strong, nonatomic, readonly) EHIModel *locationPromptModel;

/** A placeholder to enable/disable the promotion section */
@property (strong, nonatomic) EHIDashboardPromotionCellViewModel *promotionModel;

/** @c YES if the item at the path is selectable */
- (BOOL)shouldSelectItemAtIndexPath:(NSIndexPath *)indexPath;
/** Runs the selection logic for the specified item */
- (void)selectItemAtIndexPath:(NSIndexPath *)indexPath;
/** Shows system alerts for notifications */
- (void)acceptNotifications;
/** Hides notification prompt */
- (void)denyNotifications;
/** Clears all quickstart activity */
- (void)clearQuickstart;
/** Refresh the user rentals */
- (void)refreshRentals;

- (void)pushPromotionDetails;
/**Return current statefor analytics for screen based on rentals and auth statuses */
- (NSString *)currentStatus;

@end
