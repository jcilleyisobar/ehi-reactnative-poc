//
//  EHIConfirmationLocationViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 4/21/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIConfirmationLocationViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIReservationBuilder.h"
#import "EHILocation.h"

@interface EHIConfirmationLocationViewModel ()
@property (strong, nonatomic) EHILocation *location;
@end

@implementation EHIConfirmationLocationViewModel

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHILocation class]]) {
        self.location = model;
    }
}

- (void)setLocation:(EHILocation *)location
{
    _location = location;
    
    BOOL     oneWayReservation = [EHIReservationBuilder sharedInstance].isOneWayReservation;
    NSString *pickupLoaction   = [EHIReservationBuilder sharedInstance].pickupLocation.uid;
    NSString *returnLocation   = [EHIReservationBuilder sharedInstance].returnLocation.uid;
    NSString *titleString;
    
    if(oneWayReservation) {
        if(pickupLoaction == location.uid) {
            titleString = EHILocalizedString(@"reservation_confirmation_location_section_pickup_title", @"PICK-UP LOCATION", @"");
        }
        else if(returnLocation == location.uid) {
            titleString = EHILocalizedString(@"reservation_confirmation_location_section_return_title", @"RETURN LOCATION", @"");
        }
    } else {
        titleString = EHILocalizedString(@"reservation_location_selection_pickup_header_fallback_title", @"LOCATION", @"fallback header title for a section that allows user to select pickup location");
    }
    
    self.title       = titleString;
    self.name        = location.displayName;
    self.address     = location.address.formattedAddress;
    self.phone       = location.formattedPhoneNumber;
    self.iconImage   = [self iconImageForLocation:location];
}

- (NSString *)iconImageForLocation:(EHILocation *)location
{
    if(location.isFavorited) {
        return @"icon_favorites_03";
    } switch (location.type) {
        case EHILocationTypeAirport:
            return @"icon_airport_green";
        case EHILocationTypePort:
            return @"icon_portofcall_01";
        case EHILocationTypeTrain:
            return @"icon_train_01";
        default:
            return nil;
    }
}

# pragma mark - Actions

- (void)callLocation
{
    [UIApplication ehi_promptPhoneCall:self.location.formattedPhoneNumber];
}

@end
