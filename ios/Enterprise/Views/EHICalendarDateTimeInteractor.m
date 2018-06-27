//
// Created by Rafael Ramos on 5/17/17.
// Copyright (c) 2017 Enterprise. All rights reserved.
//

#import "EHICalendarDateTimeInteractor.h"
#import "EHISingleDateCalendarViewModel.h"
#import "EHISingleTimeCalendarViewModel.h"
#import "EHIDateTimeUpdatableProtocol.h"

@interface EHICalendarDateTimeInteractor ()
@property (assign, nonatomic) EHISingleDateCalendarFlow flow;
@property (weak  , nonatomic) id<EHIDateTimeUpdatableProtocol> component;
@property (copy  , nonatomic) EHICalendarDateTimeInteractorHandler completion;
@property (strong, nonatomic) NSDate *pickupValue;
@property (strong, nonatomic) NSDate *returnValue;
@end

@implementation EHICalendarDateTimeInteractor

- (instancetype)initWithComponent:(id<EHIDateTimeUpdatableProtocol>)component inFlow:(EHISingleDateCalendarFlow)flow
{
	if(self = [super init]) {
		self.flow = flow;
		self.component = component;
	}

	return self;
}

- (void)handleChangesInSection:(EHIDateTimeComponentSection)section with:(EHICalendarData *)calendarData completion:(EHICalendarDateTimeInteractorHandler)completion
{
	self.completion = completion;
	switch(section) {
		case EHIDateTimeComponentSectionPickupDate:
		case EHIDateTimeComponentSectionReturnDate: {
			self.pickupValue = calendarData.pickupDate;
			self.returnValue = calendarData.returnDate;
			[self handleDateChange:section];
			break;
		}
		case EHIDateTimeComponentSectionPickupTime:
		case EHIDateTimeComponentSectionReturnTime: {
			self.pickupValue = calendarData.pickupTime;
			self.returnValue = calendarData.returnTime;
			[self handleTimeChange:section];
			break;
		}
	}
}

- (void)handleDateChange:(EHIDateTimeComponentSection)section
{
    enum EHISingleDateCalendarType type = section == EHIDateTimeComponentSectionPickupDate
        ? EHISingleDateCalendarTypePickup
        : EHISingleDateCalendarTypeReturn;

    EHISingleDateCalendarViewModel *model = [[EHISingleDateCalendarViewModel alloc] initWithModel:@(type)];
	model.flow = self.flow;
    model.handler    = self.dateHandler;
	model.pickupDate = self.pickupValue;
    model.returnDate = self.returnValue;

    self.router.transition
        .push(EHIScreenSingleDateSelect)
        .object(model)
        .start(nil);
}

- (void (^)(NSDate *pickupDate, NSDate *returnDate))dateHandler
{
    __weak typeof(self) welf = self;
    return ^(NSDate *pickupDate, NSDate *returnDate){
        welf.router.transition.pop(1).start(nil);
        [welf setDatePickup:pickupDate andReturn:returnDate];
    };
}

- (void)setDatePickup:(NSDate *)pickupDate andReturn:(NSDate *)returnDate
{
    [self.component setDate:pickupDate inSection:EHIDateTimeComponentSectionPickupDate];
    [self.component setDate:returnDate inSection:EHIDateTimeComponentSectionReturnDate];

	[self runCompletionWith:pickupDate and:returnDate];
}

- (void)handleTimeChange:(EHIDateTimeComponentSection)section
{
    EHISingleTimeCalendarType type = section == EHIDateTimeComponentSectionPickupTime
        ? EHISingleTimeCalendarTypePickupTime
        : EHISingleTimeCalendarTypeReturnTime;

    EHISingleTimeCalendarViewModel *model = [[EHISingleTimeCalendarViewModel alloc] initWithModel:@(type)];
	model.flow = self.flow;
	model.handler    = self.timeHandler;
    model.pickupTime = self.pickupValue;
    model.returnTime = self.returnValue;

    self.router.transition
        .push(EHIScreenSingleTimeSelect)
        .object(model)
        .start(nil);
}

- (void (^)(NSDate *pickupDate, NSDate *returnDate))timeHandler
{
    __weak typeof(self) welf = self;
    return ^(NSDate *pickupTime, NSDate *returnTime){
        welf.router.transition.pop(1).start(nil);
        [welf setTimePickup:pickupTime andReturn:returnTime];
    };
}

- (void)setTimePickup:(NSDate *)pickupTime andReturn:(NSDate *)returnTime
{
    [self.component setDate:pickupTime inSection:EHIDateTimeComponentSectionPickupTime];
    [self.component setDate:returnTime inSection:EHIDateTimeComponentSectionReturnTime];

	[self runCompletionWith:pickupTime and:returnTime];
}

- (void)runCompletionWith:(NSDate *)pickupValue and:(NSDate *)returnValue
{
	ehi_call(self.completion)(pickupValue, returnValue);
}

- (EHIRouter *)router
{
	return [EHIMainRouter currentRouter];
}

@end
