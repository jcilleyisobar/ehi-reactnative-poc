//
//  EHIMenuItem.h
//  Enterprise
//
//  Created by Ty Cobb on 3/30/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "NAVTransition.h"

typedef NS_ENUM(NSInteger, EHIMenuItemType) {
    EHIMenuItemTypePromotion,
    EHIMenuItemTypeScreen,
    EHIMenuItemTypeSecondary
};

typedef NS_ENUM(NSInteger, EHIMenuItemHeader) {
    EHIMenuItemHeaderNone,
    EHIMenuItemHeaderEnterprisePlus,
    EHIMenuItemHeaderReservation,
    EHIMenuItemHeaderSupport
};

typedef NS_ENUM(NSInteger, EHIMenuItemRow) {
    EHIMenuItemRowPromotion,
    EHIMenuItemRowRNTest,
    EHIMenuItemRowHome,
    EHIMenuItemRowRentals,
    EHIMenuItemRowRewards,
    EHIMenuItemRowProfile,
    EHIMenuItemRowSignIn,
    EHIMenuItemRowRentalLookUp,
    EHIMenuItemRowNewRental,
    EHIMenuItemRowLocations,
    EHIMenuItemRowFeedback,
    EHIMenuItemRowSupport,
    EHIMenuItemRowSettings,
    EHIMenuItemRowSignout,
    EHIMenuItemRowDebug,
};

typedef void(^EHIMenuAction)(void(^)(BOOL));
typedef void(^EHIMenuTransition)(NAVTransitionBuilder *);

@interface EHIMenuItem : EHIModel

/** The classification for this menu item; drives visual style */
@property (assign, nonatomic, readonly) EHIMenuItemType type;
/** The header for this menu item; drives visual style */
@property (assign, nonatomic, readonly) EHIMenuItemHeader header;
/** Enum providing mapped one-to-one to individual menu items */
@property (assign, nonatomic, readonly) EHIMenuItemRow row;
/** The title for this menu item */
@property (copy  , nonatomic, readonly) NSString *title;
/** The attributed title for this menu item */
@property (copy  , nonatomic, readonly) NSAttributedString *attributedTitle;
/** The title for the header */
@property (copy  , nonatomic, readonly) NSString *headerTitle;
/** The icon name for this menu item */
@property (copy  , nonatomic, readonly) NSString *iconName;
/** The name for the analytics action to fire when this item is tapped */
@property (copy  , nonatomic, readonly) NSString *analyticsAction;
/** The root to transition to on selection; optional; mutually exclusive with @c transition */
@property (copy  , nonatomic, readonly) NSString *root;
/** A custom routing operation to run on selection; optional; mutually exclusive with @c root */
@property (copy  , nonatomic, readonly) EHIMenuTransition transition;
/** The action block to run when the menu item is tapped. Passed a completion block that @em must be called */
@property (copy  , nonatomic, readonly) EHIMenuAction action;

@property (assign, nonatomic) BOOL showHeader;
@property (assign, nonatomic) BOOL hideDivider;

/** Generates a list of @c EHIMenuItems to be rendered */
+ (NSArray *)items;

@end
