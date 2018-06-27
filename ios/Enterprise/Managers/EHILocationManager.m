//
//  EHILocationManager.m
//  Enterprise
//
//  Created by George Stuart on 2/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationManager.h"
#import "EHILocationRequest.h"

#define EHIErrorDestinationKey @"EHIErrorDestinationKey"

@interface EHILocationManager () <CLLocationManagerDelegate>
@property (assign, nonatomic) BOOL isReady;
@property (strong, nonatomic) CLLocationManager *locationFinder;
@property (strong, nonatomic) NSMutableArray *pendingRequests;
@property (nonatomic, readonly) BOOL shouldRequestWhenIsUseAuthorization;
@end

@implementation EHILocationManager

+ (instancetype)sharedInstance
{
	static EHILocationManager *_sharedInstance;
	static dispatch_once_t onceToken;
	dispatch_once_on_main_thread(&onceToken, ^{
		_sharedInstance = [self new];
	});
	
	return _sharedInstance;
}

- (instancetype)init
{
    if(self = [super init]) {
        _userLocation = [EHIUserLocation new];
        _pendingRequests = [NSMutableArray new];
        
        _locationFinder = [CLLocationManager new];
        _locationFinder.delegate = self;
       
        // if locations aren't available, then we're ready to fetch
        _isReady = !self.locationsAvailable;
       
        // if locations are available, then try and fetch the current location
        if(!_isReady) {
            [self currentLocationWithHandler:^(CLLocation *location, NSError *error) {
                self.isReady = YES;
            }];
        }
    }

    return self;
}

# pragma mark - Requests

- (void)locationsAvailableWithHandler:(void(^)(BOOL locationsAvailable, NSError *error))handler;
{
    // add a new availability requuest
    EHILocationRequest *request = [[EHILocationRequest alloc] initWithAvailabilityFlag:YES handler:handler];
    [self.pendingRequests addObject:request];
   
    // start location finding if necessary
    [self initiateNextLocationStep];
}

- (void)currentLocationWithHandler:(void(^)(CLLocation *location, NSError *error))handler;
{
    // add a new location request
    EHILocationRequest *request = [[EHILocationRequest alloc] initWithAvailabilityFlag:NO handler:handler];
    [self.pendingRequests addObject:request];
    
    // start location finding if necessary
    [self initiateNextLocationStep];
}

//
// Helpers
//

- (void)initiateNextLocationStep
{
    if(self.shouldRequestWhenIsUseAuthorization) {
        [self.locationFinder requestWhenInUseAuthorization];
    } else {
        [self.locationFinder startUpdatingLocation];
    }
}

- (void)flushAvailabilityHandlersWithError:(NSError *)error
{
    // render the error on-screen if necessary
    [self renderError:error];
    
    // pull out any availability requests
    NSArray *availabilityRequests = self.pendingRequests.ehi_remove(^(EHILocationRequest *request) {
        return request.isAvailabilityRequest;
    });
    
    // call all the availability handlers
    for(EHILocationRequest *request in availabilityRequests) {
        EHILocationRequestAvailabilityHandler handler = request.handler;
        ehi_call(handler)(self.locationsAvailable && !error, error);
    }
}

- (void)flushHandlersWithLocation:(CLLocation *)location error:(NSError *)error
{
    // this coordinate shouldn't be value if we error
    if(error) {
        location = nil;
    }
   
    // update our internal state
    self.userLocation.currentLocation = location;
    
    // flush any availability handlers first
    [self flushAvailabilityHandlersWithError:error];
    
    // then run through any leftover location handlers
    if(![self shouldContinueSearchingForError:error]) {
        for(EHILocationRequest *request in self.pendingRequests) {
            EHILocationRequestLocationHandler handler = request.handler;
            ehi_call(handler)(location, error);
        }
        
        // clean up after ourselves
        [self.pendingRequests removeAllObjects];
        [self.locationFinder stopUpdatingLocation];
    }
}

# pragma mark - CLLocationManagerDelegate

- (void)locationManager:(CLLocationManager *)manager didChangeAuthorizationStatus:(CLAuthorizationStatus)status
{
    // flush any availability request handlers
    [self flushAvailabilityHandlersWithError:nil];
  
    // if we still have remaining requests, get the location
    if(self.pendingRequests.count) {
        [self initiateNextLocationStep];
    }
    
    // wipe cache if not allowed anymore
    if(status == kCLAuthorizationStatusRestricted || status == kCLAuthorizationStatusDenied) {
        self.userLocation.currentLocation = nil;
    }
}

- (void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray *)locations
{
    [self flushHandlersWithLocation:locations.firstObject error:nil];
}

- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error
{
    [self flushHandlersWithLocation:nil error:error];
}

# pragma mark - Authorization States

- (BOOL)locationsAvailable
{
    CLAuthorizationStatus status = [CLLocationManager authorizationStatus];
    
    return status == kCLAuthorizationStatusAuthorizedWhenInUse
        || status == kCLAuthorizationStatusAuthorizedAlways;
}

- (CLAuthorizationStatus)locationStatus
{
    return [CLLocationManager authorizationStatus];
}

- (BOOL)shouldRequestWhenIsUseAuthorization
{
    return [CLLocationManager authorizationStatus] == kCLAuthorizationStatusNotDetermined;
}

# pragma mark - Errors

- (BOOL)shouldContinueSearchingForError:(NSError *)error
{
    return error && error.code == kCLErrorLocationUnknown;
}

- (void)renderError:(NSError *)error
{
    // we're going to ignore errors before we're ready
    if(!self.isReady) {
        return;
    }
    // ensure the error is one we can display
    error = [self localizedErrorFromLocationError:error];
    if(!error) {
        return;
    }
   
    // show an alert for this error
    EHIAlertViewBuilder *alert = EHIAlertViewBuilder.new
        .title(EHILocalizedString(@"locations_unvailable_error_title", @"Locations Services Unavailable", @"Title for location services error"))
        .message(error.localizedDescription)
        .cancelButton(EHILocalizedString(@"alert_okay_title", @"Okay", @"Title for alert 'okay' button"));
 
    // if we have a destination, add a button for it
    NSURL *destination = [self errorDestinationFromLocationError:error];
    if(destination) {
        alert.button(EHILocalizedString(@"alert_settings_button", @"Settings", @"Title for location alert settings button"));
    }
    
    alert.show(^(NSInteger index, BOOL canceled) {
        // open the destination URL if we have one and weren't canceled
        if(!canceled && destination) {
            [UIApplication ehi_openURL:destination];
        }
    });
}

- (NSError *)localizedErrorFromLocationError:(NSError *)error
{
    // parse the error message
    NSString *message = [self errorMessageFromLocationError:error];
    if(!message) {
        return nil;
    }
  
    // return an error if we generated a custom message, otherwise return nil
    return [NSError errorWithDomain:kCLErrorDomain code:error.code userInfo:@{
        NSLocalizedDescriptionKey : message
    }];
}

- (NSString *)errorMessageFromLocationError:(NSError *)error
{
    // don't error for non-core location errors or unknown location
    if(![error.domain isEqualToString:kCLErrorDomain] || error.code == kCLErrorLocationUnknown) {
        return nil;
    }
    
    switch((CLError)error.code) {
        case kCLErrorDenied:
            return EHILocalizedString(@"location_services_denied_error", @"Location services are disabled, please enable them in settings to continue.", @"Message for kCLErrorDenied code");
        default:
            return EHILocalizedString(@"location_services_generic_error", @"Can't access location services at this time, try again later.", @"Message for unknown CLError");
    }
}

- (NSURL *)errorDestinationFromLocationError:(NSError *)error
{
    NSURL *result = nil;
   
    // add the destination for the settings bundle for denied errors, if possible
    if(error.code == kCLErrorDenied) {
        result = [NSURL URLWithString:UIApplicationOpenSettingsURLString];
    }
    
    return result;
}

@end
