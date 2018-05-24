//
//  EHIReservationBuilder_Private.h
//  Enterprise
//
//  Created by Ty Cobb on 6/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIReservationBuilder.h"
#import "EHIUserManager.h"
#import "EHIHistoryManager.h"
#import "EHIToastManager.h"
#import "EHIConfiguration.h"
#import "EHISettings.h"
#import "EHIDataStore.h"
#import "EHIServices+Location.h"
#import "EHIServices+Reservation.h"
#import "EHIReservationBuilder+Analytics.h"

@interface EHIReservationBuilder () <EHIUserListener>
@property (assign, nonatomic) BOOL isReady;
@property (assign, nonatomic) EHIReservationBuilderFlow currentFlow;
@property (strong, nonatomic) NSHashTable *listeners;
@property (strong, nonatomic) EHILocation *pickupLocation;
@property (strong, nonatomic) EHILocation *returnLocation;
@property (strong, nonatomic) EHIReservation *modifiedReservation;
@property (strong, nonatomic) NSMutableArray<EHIContractAdditionalInfoValue> *additionalInfo;
@property (strong, nonatomic) id<EHINetworkCancelable> activeRequest;
@property (nonatomic, readonly) EHIAlertViewBuilder *cancelationAlert;
@end

@interface EHIReservationBuilder (Helpers)
/** Loads driver info, either building it from the current user or unarchiving it from disk */
- (void)loadDriverInfo;
@end
