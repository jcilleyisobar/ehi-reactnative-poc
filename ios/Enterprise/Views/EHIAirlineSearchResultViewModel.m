//
//  EHIAirlineSearchResultViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 6/22/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIAirlineSearchResultViewModel.h"
#import "EHIAirline.h"

@interface EHIAirlineSearchResultViewModel ()
@property (strong, nonatomic) EHIAirline *airline;
@end

@implementation EHIAirlineSearchResultViewModel

+ (instancetype)initWithAirline:(EHIAirline *)airline
{
    EHIAirlineSearchResultViewModel *model = [EHIAirlineSearchResultViewModel new];
    model.airline     = airline;
    model.airlineName = !airline.isWalkIn ? airline.details : EHILocalizedString(@"flight_details_no_flight", @"I don't have a flight", @"");
    
    return model;
}

# pragma mark - Sorting

- (BOOL)isEqual:(id)object
{
    EHIAirlineSearchResultViewModel *anotherObject = (EHIAirlineSearchResultViewModel *)object;
    if(anotherObject) {
        return self.airline.code == anotherObject.airline.code;
    }
    
    return NO;
}

- (NSComparisonResult)compare:(EHIAirlineSearchResultViewModel *)otherObject {
    return [self.airline.details compare:otherObject.airline.details];
}

# pragma mark - Searching

- (BOOL)contains:(NSString *)name
{
    return [self.airline.details.lowercaseString containsString:name.lowercaseString];
}

@end
