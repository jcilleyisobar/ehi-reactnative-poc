//
//  EHILocationEmptyQueryViewModel.m
//  Enterprise
//
//  Created by mplace on 2/10/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHILocationEmptyQueryViewModel.h"
#import "EHIUserLocation.h"
#import "EHIConfiguration.h"

@interface EHILocationEmptyQueryViewModel ()
@property (copy, nonatomic) NSString *title;
@end

@implementation EHILocationEmptyQueryViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _callButtonTitle   = EHILocalizedString(@"locations_empty_query_call_title", @"CALL US", @"Title for a button that calls the company");
        _nearbyButtonTitle = EHILocalizedString(@"locations_empty_query_nearby_title", @"FIND NEARBY LOCATIONS", @"Title for a button that triggers a nearby location search");
        _subtitle = EHILocalizedString(@"locations_empty_query_subtitle", @"Try searching with only the city name and nothing else (e.g. \"Miami\").", @"Subtitle appears when the search returns nothing");
    }
    
    return self;
}

- (void)updateWithModel:(NSString *)model
{
    [super updateWithModel:model];

    // update title and subtitle strings
    NSString *format = EHILocalizedString(@"locations_empty_query_title", @"We're sorry, we couldn't find an exact match for \"#{query}\"", @"Text appears when the search returns nothing.");
    self.title = [format ehi_applyReplacementMap:@{
        @"query" : model ?: @"",
    }];
}

# pragma mark - Actions

- (void)callHelpNumber
{
    [EHIAnalytics trackAction:self.analyticsAction(EHIAnalyticsLocActionCallUs) handler:nil];
    
    [UIApplication ehi_promptPhoneCall:[EHIConfiguration configuration].primarySupportPhone.number];
}

- (void)findNearbyLocations
{
    [EHIAnalytics trackAction:self.analyticsAction(EHIAnalyticsLocActionNearby) handler:nil];
    
    self.router.transition
        .push(EHIScreenLocationsMap).object(EHIUserLocation.location).start(nil);
}

//
// Helpers
//

- (NSString *(^)(NSString *))analyticsAction
{
    return ^(NSString *action) {
        return ehi_serializeActions(EHIAnalyticsLocActionNoLocations, action, nil);
    };
}

@end
