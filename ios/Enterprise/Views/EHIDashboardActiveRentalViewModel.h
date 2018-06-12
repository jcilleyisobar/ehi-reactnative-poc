//
//  EHIDashboardActiveRentalViewModel.h
//  Enterprise
//
//  Created by mplace on 5/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIDashboardActiveRentalViewModel : EHIViewModel <MTRReactive>
/** Title for the vehicle statistic grid */
@property (copy, nonatomic, readonly) NSAttributedString *vehicleStatTitle;
/** Title for the vehicle type cell in the statistic grid */
@property (copy, nonatomic, readonly) NSString *vehicleTypeTitle;
/** Title for the vehicle color cell in the statistic grid */
@property (copy, nonatomic, readonly) NSString *vehicleColorTitle;
/** Title for the vehicle plate cell in the statistic grid */
@property (copy, nonatomic, readonly) NSString *vehiclePlateTitle;
/** Title for rate my ride in the statistic grid */
@property (copy, nonatomic, readonly) NSString *rateMyRideTitle;

/** Subtitle for the vehicle type cell in the statistic grid */
@property (copy, nonatomic) NSString *vehicleTypeSubtitle;
/** Subtitle for the vehicle color cell in the statistic grid */
@property (copy, nonatomic) NSString *vehicleColorSubtitle;
/** Subtitle for the vehicle plate cell in the statistic grid */
@property (copy, nonatomic) NSString *vehiclePlateSubtitle;

/** Title for the rental info */
@property (copy, nonatomic, readonly) NSString *rentalInfoTitle;
/** Return location for teh rental info */
@property (copy, nonatomic) NSAttributedString *returnLocationTitle;
/** Return date and time for the rental info */
@property (copy, nonatomic) NSString *returnDateTimeTitle;

/** Title for the return instructions button */
@property (copy, nonatomic, readonly) NSString *returnInstructionsButtonTitle;
/** Title for the get directions button */
@property (copy, nonatomic, readonly) NSString *getDirectionsButtonTitle;
/** Title for the extend rental button */
@property (copy, nonatomic, readonly) NSString *extendRentalButtonTitle;
/** Title for the "Find Gas Stations" button */
@property (copy, nonatomic, readonly) NSString *findGasTitle;

/** @YES if the location is not of type @c EHILocationTypeAirport */
@property (assign, nonatomic) BOOL shouldHideAirport;
/** @YES if vehicle does not have name, make and model */
@property (assign, nonatomic) BOOL shouldHideVehicleName;
/** @YES if vehicle does not have color */
@property (assign, nonatomic) BOOL shouldHideVehicleColor;
/** @YES if vehicle does not have plate number */
@property (assign, nonatomic) BOOL shouldHideVehiclePlate;
/** @YES if no vehicle data is available */
@property (assign, nonatomic) BOOL shouldHideCurrentRentalSection;
/** @YES if rate my ride url is available */
@property (assign, nonatomic) BOOL shouldHideRateMyRide;

/** Hides return location button if @YES */
@property (assign, nonatomic) BOOL shouldHideReturnLocation;
/** Hides get directions button if @YES */
@property (assign, nonatomic) BOOL shouldHideGetDirections;

/** Shows the rental return policy */
- (void)showReturnInstructions;
/** Opens up the maps application centered around the return location */
- (void)showDirections;
/** Opens a dialogue with a phone number to call to extend the rental */
- (void)showExtendRentalDialogue;
/** Show gas stations near the return location */
- (void)showGasStations;
/** Show details for return location */
- (void)showLocationDetails;
/** Show rate my ride webview */
- (void)showRateMyRide;
@end
