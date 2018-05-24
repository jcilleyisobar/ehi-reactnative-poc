//
//  EHILocationDetailsInfoViewModel.h
//  Enterprise
//
//  Created by Ty Cobb on 2/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHILocation.h"

@interface EHILocationDetailsInfoViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic, readonly) NSAttributedString *title;
@property (copy  , nonatomic, readonly) NSString *favoritesTitle;
@property (copy  , nonatomic, readonly) NSString *directionsTitle;
@property (copy  , nonatomic, readonly) NSString *wayfindingTitle;
@property (copy  , nonatomic, readonly) NSAttributedString *afterHoursTitle;

@property (assign, nonatomic, readonly) BOOL isFavorited;
@property (assign, nonatomic, readonly) BOOL isOnBrand;
@property (assign, nonatomic, readonly) BOOL hasWayfindingDirections;
@property (assign, nonatomic, readonly) BOOL hideExotics;
@property (assign, nonatomic, readonly) BOOL hideAfterHours;
@property (assign, nonatomic) BOOL isOneWay;

@property (copy  , nonatomic, readonly) NSString *address;
@property (copy  , nonatomic, readonly) NSString *phoneNumber;
@property (copy  , nonatomic, readonly) NSArray  *wayfindings;

- (void)toggleIsFavorited;
- (void)showDirections;
- (void)showDirectionsFromTerminal;
- (void)callLocation;

@end
