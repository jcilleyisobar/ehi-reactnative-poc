//
//  EHIServices+Location.m
//  Enterprise
//
//  Created by Ty Cobb on 1/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIServices+Location.h"
#import "EHIServices_Private.h"

#define EHIAgeOptionsMock 0

@implementation EHIServices (Location)

- (id<EHINetworkCancelable>)fetchLocationsForQuery:(EHILocationSearchQuery *)query handler:(void (^)(EHILocations *, EHIServicesError *))handler
{
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeNone get:@"%@/text/%@", self.searchBase, query.query];
    [request parameters:^(EHINetworkRequest *request) {
        request[@"brand"]          = @"ENTERPRISE";
        request[@"oneway"]         = EHIStringifyFlag(query.isOneWay);
        request[@"locale"]         = NSLocale.ehi_identifier;
        request[@"countryCode"]    = NSLocale.ehi_region;
        request[@"includeExotics"] = EHIStringifyFlag(YES);
    }];

    return [self startRequest:request parseModel:[EHILocations class] asynchronously:YES handler:handler];
}

- (id<EHINetworkCancelable>)fetchLocationsForRegion:(EHISearchRegion)region filters:(EHILocationFilterQuery *)filterQuery handler:(void (^)(EHISpatialLocations *, EHIServicesError *))handler
{
    EHINetworkRequest *request =
        [EHINetworkRequest service:EHIServicesEnvironmentTypeNone get:@"%@/spatial/%.2f/%.2f", self.searchBase, region.center.latitude, region.center.longitude];
    
    [request parameters:^(EHINetworkRequest *request) {
        request[@"locale"]         = NSLocale.ehi_identifier;
        request[@"includeExotics"] = EHIStringifyFlag(YES);
       
        // encode the radius if anything other than default is specified (always round up)
        if(region.radius != 0.0) {
            request[@"radius"] = @((int)(region.radius / 1000.0) + 1);
        }

        // encode the filters into the parameters if they exist
        [filterQuery encodeWithRequest:request];
    }];
   
    return [self startRequest:request parseModel:[EHISpatialLocations class] asynchronously:YES handler:handler];
}

- (id<EHINetworkCancelable>)updateHoursForLocation:(EHILocation *)location handler:(void (^)(EHILocation *, EHIServicesError *))handler
{
    return [self updateHoursForLocation:location fromDate:[NSDate ehi_today] toDate:[[NSDate ehi_today] ehi_addDays:EHIDaysPerYear] handler:handler];
}

- (id<EHINetworkCancelable>)updateHoursForLocation:(EHILocation *)location fromDate:(NSDate *)fromDate toDate:(NSDate *)toDate handler:(void (^)(EHILocation *, EHIServicesError *))handler
{
    // if we don't have a location or we already fetched hours, then we're done here
    if(!location.uid || location.hours) {
        ehi_call(handler)(location, nil);
        return nil;
    }
    
    NSString *from = [fromDate ehi_components:NSCalendarDayGranularity].date.ehi_string ?: @"";
    NSString *to   = [toDate ehi_components:NSCalendarDayGranularity].date.ehi_string ?: @"";
    
    // otherwise, fetch the hours and store them on the location
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeNone get:@"%@/hours/%@", self.searchBase, location.uid];
    [request parameters:^(EHINetworkRequest *request) {
        request [@"from"] = from;
        request [@"to"]   = to;
    }];
    
    return [self startRequest:request parseAsynchronously:YES withBlock:^id(id responseData) {
        location.hours = [EHILocationHours modelWithDictionary:responseData];
        return location;
    } handler:handler];
}

- (id<EHINetworkCancelable>)fetchHoursForLocation:(EHILocation *)location date:(NSDate *)date handler:(void (^)(EHILocationDay *, EHIServicesError *))handler
{
    id<EHINetworkCancelable> cancelable = nil;
   
    // if we don't have a location, fail gracelessly
    if(!location.uid) {
        ehi_call(handler)(nil, nil);
    } else if(location.hours) {
        EHILocationDay *day = location.hours[date];
        ehi_call(handler)(day, nil);
    } else {
        // stringify the date we care about
        NSString *dateString = [date ehi_string];
       
        // requre hours for this specific day
        EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeNone get:@"%@/hours/%@", self.searchBase, location.uid];
        [request body:^(EHINetworkRequest *request) {
            request[@"from"] = dateString;
            request[@"to"]   = dateString;
        }];
       
        // and pull the day out of the response
        cancelable = [self startRequest:request parseModel:[EHILocationHours class]
                         asynchronously:YES handler:^(EHILocationHours *hours, EHIServicesError *error) {
             EHILocationDay *day = [hours isKindOfClass:EHILocationHours.class] ? hours[dateString] : nil;
             ehi_call(handler)(day, error);
        }];
    }
    
    return cancelable;
}

- (id<EHINetworkCancelable>)updateAgeOptionsForLocation:(EHILocation *)location handler:(void (^)(EHILocation *, EHIServicesError *))handler
{
    if(!location.uid || location.ageOptions) {
        ehi_call(handler)(location, nil);
        return nil;
    }
    
#if EHIAgeOptionsMock
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeNone get:@"mock://ageOptions.json"];
#else
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeNone get:@"%@/renterage/%@", self.searchBase, location.uid];
#endif
    return [self startRequest:request parseCollection:[EHILocationRenterAge class] asynchronously:YES handler:^(id response, EHIServicesError *error) {
        location.ageOptions = response;
        ehi_call(handler)(location, nil);
    }];
}

- (id<EHINetworkCancelable>)updateDetailsForLocation:(EHILocation *)location handler:(void (^)(EHILocation *, EHIServicesError *))handler
{
    if(!location.uid) {
        ehi_call(handler)(location, nil);
        return nil;
    }
    
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBOLocation get:@"locations/%@/%@/%@", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, location.uid];
    return [self startRequest:request parseAsynchronously:YES withBlock:^(NSDictionary *response) {
        [location updateWithDictionary:response[@"location"]];
        return location;
    } handler:handler];
}

//
// Helpers
//

- (NSString *)searchBase
{
    return [EHISettings environment].search;
}

@end
