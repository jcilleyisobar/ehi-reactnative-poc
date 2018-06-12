//
//  EHIDashboardUpcomingRentalViewModel.h
//  Enterprise
//
//  Created by mplace on 5/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIImage.h"

@interface EHIDashboardUpcomingRentalViewModel : EHIViewModel <MTRReactive>
/** Title for the upcoming rental confirmation section (static) */
@property (copy, nonatomic, readonly) NSAttributedString *upcomingRentalTitle;
/** Title for the rental confirmation number (static) */
@property (copy, nonatomic, readonly) NSString *confirmationNumberTitle;
/** Relative pickup date title */
@property (copy, nonatomic) NSString *relativePickupDateTitle;
/** The confirmation number of the upcoming rental */
@property (copy, nonatomic) NSString *confirmationNumber;
/** The display pickup date and time */
@property (copy, nonatomic) NSString *pickupDateTime;
/** The display name of the pickup location */
@property (copy, nonatomic) NSAttributedString *pickupLocation;
/** Title for the get directions button */
@property (copy, nonatomic) NSString *directionsButtonTitle;
/** Title for directions from terminal button */
@property (copy, nonatomic) NSString *directionsFromTerminalText;
/** Title for the view details button */
@property (copy, nonatomic) NSString *detailsButtonTitle;
/** Image of the rental vehicle */
@property (strong, nonatomic) EHIImage *vehicleImage;
/** @YES if the location is not of type @c EHILocationTypeAirport */
@property (assign, nonatomic) BOOL shouldHideAirport;
/** @YES if location contains wayfinding information */
@property (assign, readonly) BOOL shouldHideDirectionsFromTerminal;

- (void)showDirections;
- (void)showDetails;
- (void)showLocationDetails;
- (void)showDirectionsFromTerminal;

@end
