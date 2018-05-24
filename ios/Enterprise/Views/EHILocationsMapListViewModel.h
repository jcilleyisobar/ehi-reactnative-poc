//
//  EHILocationsMapListViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 5/29/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

typedef NS_ENUM(NSInteger, EHILocationsMapListLayout) {
    EHILocationsMapListLayoutCallout,
    EHILocationsMapListLayoutList
};

typedef NS_ENUM(NSInteger, EHILocationsMapListStyle) {
	EHILocationsMapListStyleValid,
	EHILocationsMapListStyleInvalid
};

@class EHILocation;
@interface EHILocationsMapListViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic, readonly) NSAttributedString *title;
@property (copy  , nonatomic, readonly) NSString *subtitle;
@property (copy  , nonatomic, readonly) NSAttributedString *afterHoursTitle;
@property (copy  , nonatomic, readonly) NSAttributedString *conflictTitle;
@property (copy  , nonatomic, readonly) NSString *openHoursTitle;
@property (copy  , nonatomic, readonly) NSString *openHours;
@property (copy  , nonatomic, readonly) NSString *flexibleTravelTitle;
@property (assign, nonatomic, readonly) EHILocationsMapListStyle style;
@property (assign, nonatomic, readonly) BOOL isExpanded;
@property (assign, nonatomic, readonly) BOOL shouldShowDetails;
@property (assign, nonatomic, readonly) BOOL hasConflicts;
@property (copy  , nonatomic) id<EHIAnalyticsEncodable> filterQuery;
@property (assign, nonatomic) BOOL isOneWay;
@property (assign, nonatomic) EHILocationsMapListLayout layout;

- (instancetype)initWithModel:(EHILocation *)model;
- (void)updateWithModel:(EHILocation *)model;

- (void)changeState;

@end
