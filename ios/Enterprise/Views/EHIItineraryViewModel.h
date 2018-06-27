//
//  EHIItineraryViewModel.h
//  Enterprise
//
//  Created by Ty Cobb on 2/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationStepViewModel.h"
#import "EHILocation.h"
#import "EHISectionHeaderModel.h"

@interface EHIItineraryViewModel : EHIReservationStepViewModel <MTRReactive>

@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) NSString *pickupHeaderTitle;
@property (copy  , nonatomic, readonly) NSString *returnHeaderTitle;
@property (copy  , nonatomic, readonly) NSString *reservationDurationTitle;
@property (copy  , nonatomic, readonly) NSString *actionButtonTitle;
@property (assign, nonatomic, readonly) BOOL isInitiating;
@property (assign, nonatomic, readonly) BOOL isLoading;
@property (assign, nonatomic, readonly) BOOL isOneWay;
@property (assign, nonatomic, readonly) BOOL isReadyToContinue;

// call when ready to initiate reservation
- (void)commitItinerary;

@end
