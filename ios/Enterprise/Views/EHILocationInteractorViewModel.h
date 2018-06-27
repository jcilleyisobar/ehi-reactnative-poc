//
//  EHILocationInteractorViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 5/17/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHICalendarDateTimeInteractor.h"
#import "EHIDateTimeUpdatableProtocol.h"
#import "EHIDateTimeProviderProtocol.h"
#import "EHILocationFilterQuery.h"
#import "EHISingleDateCalendarEnums.h"

@interface EHILocationInteractorViewModel : EHIViewModel <MTRReactive>

@property (strong, nonatomic, readonly) EHICalendarDateTimeInteractor *interactor;
@property (assign, nonatomic, readonly) EHILocationFilterQueryLocationType queryType;
@property (assign, nonatomic, readonly) EHISingleDateCalendarFlow flow;

/* Used to reason about query type */
@property (assign, nonatomic) BOOL hasDropoffLocation;
@property (assign, nonatomic) BOOL isSelectingPickupLocation;

/* Used to reason about date and time */
@property (strong, nonatomic) EHILocation *location;
@property (strong, nonatomic) EHILocationFilterQuery *filterQuery;

@property (assign, nonatomic, readonly) BOOL shouldSendPickupDate;
@property (assign, nonatomic, readonly) BOOL shouldSendPickupTime;
@property (assign, nonatomic, readonly) BOOL canSendPickupTime;
@property (assign, nonatomic, readonly) BOOL shouldSendDropoffDate;
@property (assign, nonatomic, readonly) BOOL shouldSendDropoffTime;
@property (assign, nonatomic, readonly) BOOL canSendDropoffDate;
@property (assign, nonatomic, readonly) BOOL canSendDropoffTime;
@property (assign, nonatomic, readonly) BOOL shouldWipeDropoffData;

/* Subclasses must provide a concrete class */
- (id<EHIDateTimeUpdatableProtocol>)updatable;

- (void)assembleWithProvider:(id<EHIDateTimeProviderProtocol>)provider;

@end
