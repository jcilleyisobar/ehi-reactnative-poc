//
//  EHILocationInteractorViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/17/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHILocationInteractorViewModel.h"

@interface EHILocationInteractorViewModel ()
@property (strong, nonatomic) EHICalendarDateTimeInteractor *interactor;
@end

@implementation EHILocationInteractorViewModel

- (EHICalendarDateTimeInteractor *)interactor
{
	if(!_interactor) {
        _interactor = [[EHICalendarDateTimeInteractor alloc] initWithComponent:self.updatable inFlow:self.flow];
	}

	return _interactor;
}

- (id<EHIDateTimeUpdatableProtocol>)updatable
{
    return nil;
}

- (void)assembleWithProvider:(id<EHIDateTimeProviderProtocol>)provider
{
    [self.updatable setDate:provider.pickupDate inSection:EHIDateTimeComponentSectionPickupDate];
    [self.updatable setDate:provider.pickupTime inSection:EHIDateTimeComponentSectionPickupTime];
    [self.updatable setDate:provider.returnDate inSection:EHIDateTimeComponentSectionReturnDate];
    [self.updatable setDate:provider.returnTime inSection:EHIDateTimeComponentSectionReturnTime];
}

- (EHILocationFilterQueryLocationType)queryType
{
    if(self.isSelectingPickupLocation) {
        return self.hasDropoffLocation
            ? EHILocationFilterQueryLocationTypePickupOneWay
            : EHILocationFilterQueryLocationTypeRoundTrip;
    }
    
    return EHILocationFilterQueryLocationTypeDropoffOneWay;
}

- (BOOL)shouldSendPickupDate
{
	if(self.queryType == EHILocationFilterQueryLocationTypeDropoffOneWay) {
		return NO;
	}
    
	if(self.location.hasConflicts) {
		return !self.location.isAllDayClosedForPickup;
	}

	return YES;
}

- (BOOL)shouldSendPickupTime
{
    return self.shouldSendPickupDate;
}

- (BOOL)shouldSendDropoffDate
{
	switch(self.queryType) {
        case EHILocationFilterQueryLocationTypeDropoffOneWay:
		case EHILocationFilterQueryLocationTypeRoundTrip:
			return (self.filterQuery.datesFilter.pickupDate && !self.location.isAllDayClosedForDropoff);
		case EHILocationFilterQueryLocationTypePickupOneWay:
			return NO;
	}
}

- (BOOL)shouldSendDropoffTime
{
    return self.shouldSendDropoffDate;
}

- (BOOL)shouldWipeDropoffData
{
    return self.queryType == EHILocationFilterQueryLocationTypeRoundTrip && self.filterQuery.pickupDate == nil;
}

- (BOOL)canSendPickupTime
{
    return !self.location.hasPickupConflicts;
}

- (BOOL)canSendDropoffDate
{
    return self.filterQuery.pickupDate != nil;
}

- (BOOL)canSendDropoffTime
{
    return !self.location.hasDropoffConflicts && self.filterQuery.pickupDate;
}

@end
