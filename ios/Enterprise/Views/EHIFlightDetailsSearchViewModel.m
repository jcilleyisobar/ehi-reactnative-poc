//
//  EHIFlightDetailsSearchViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 6/23/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIFlightDetailsSearchViewModel.h"
#import "EHIReservationBuilder.h"

@implementation EHIFlightDetailsSearchViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        if([model isKindOfClass:[EHIAirline class]]) {
            [self fetchAirlineName:model];
        }
    	_searchPlaceholder = EHILocalizedString(@"flight_details_airline_placeholder", @"Your Airline", @"");
        _airlineTitle      = EHILocalizedString(@"flight_details_airline_title", @"AIRLINE", @"").uppercaseString;
    }
    
    return self;
}

- (void)updateWithModel:(id)model
{
    if([model isKindOfClass:[EHIAirline class]]) {
        [self fetchAirlineName:model];
    }
}

//
// Helpers
//

- (void)fetchAirlineName:(EHIAirline *)airline
{
    _airlineName = !airline.isWalkIn ? airline.details : @"";
}

@end
