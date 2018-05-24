//
//  EHIDashboardActiveRentalViewModel.m
//  Enterprise
//
//  Created by mplace on 5/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIDashboardActiveRentalViewModel.h"
#import "EHIConfirmationViewModel.h"
#import "EHIUserRental.h"
#import "EHIInfoModalViewModel.h"
#import "EHILocationDetailsViewModel.h"
#import "EHIWebBrowserViewModel.h"

@interface EHIDashboardActiveRentalViewModel ()
@property (strong, nonatomic) EHIUserRental *activeRental;
@end

@implementation EHIDashboardActiveRentalViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        NSString *rental  = EHILocalizedString(@"dashboard_your_vehicle", @"YOUR VEHICLE", @"second half of the vehicle stat title, used in context 'your CURRENT RENTAL'");
        _vehicleStatTitle = EHIAttributedStringBuilder.new
            .space.appendText(rental).fontStyle(EHIFontStyleHeavy, 26).color([UIColor blackColor]).string;
        
        _vehicleTypeTitle  = EHILocalizedString(@"dashboard_current_rental_vehicle_type_title", @"VEHICLE", @"vehicle type title for the dashboard current rental cell.");
        _vehicleColorTitle = EHILocalizedString(@"dashboard_current_rental_vehicle_color_title", @"VEHICLE COLOR", @"vehicle color title for the dashboard current rental cell.");
        _vehiclePlateTitle = EHILocalizedString(@"dashboard_current_rental_vehicle_plate_title", @"VEHICLE PLATE #", @"vehicle plate title for the dashboard current rental cell.");
        _rentalInfoTitle   = EHILocalizedString(@"dashboard_current_rental_title", @"CURRENT RENTAL", @"rental title for the dashboard current rental cell.");
        
        _extendRentalButtonTitle       = EHILocalizedString(@"dashboard_current_rental_extend_rental_button_title", @"EXTEND RENTAL", @"title for the extend rental button on the dashboard current rental cell.");
        _getDirectionsButtonTitle      = EHILocalizedString(@"dashboard_current_rental_get_directions_button_title", @"GET DIRECTIONS", @"title for the get directions button on the dashboard current rental cell.");
        _returnInstructionsButtonTitle = EHILocalizedString(@"dashboard_current_rental_return_intructions_button_title", @"RETURN INSTRUCTIONS", @"title for the return instructions button on the dashboard current rental cell.");
        _findGasTitle                  = EHILocalizedString(@"dashboard_current_rental_find_fuel_description", @"FIND GAS STATIONS", @"");
        _rateMyRideTitle               = EHILocalizedString(@"dashboard_current_rental_rate_my_ride_button", @"Rate Vehicle Features", @"");
    }
    
    return self;
}

- (void)updateWithModel:(EHIUserRental *)rental
{
    [super updateWithModel:rental];
    
    if([rental isKindOfClass:[EHIUserRental class]]) {
        self.activeRental = rental;
    }
}

- (void)setActiveRental:(EHIUserRental *)activeRental
{
    _activeRental = activeRental;
    
    EHICarClass *carClass = activeRental.carClassDetails;
    
    // vehicle info
    // if makeModel is not available show name instead
    NSString *vehicleSubtitle = carClass.makeModel.length > 0 ? carClass.makeModel : carClass.name;
    self.vehicleTypeSubtitle  = vehicleSubtitle;
    self.vehicleColorSubtitle = carClass.color;
    self.vehiclePlateSubtitle = carClass.licensePlate;
    
    // return location and time
    self.returnLocationTitle  = [self titleForLocation:activeRental.returnLocation];
    self.returnDateTimeTitle  = activeRental.returnTimeDisplay;

    // hide the airport icon if the return location is not of airport type
    self.shouldHideAirport = activeRental.returnLocation.type != EHILocationTypeAirport;
    
    // hide the 'vehicle' section if neither makeModel nor name is available
    self.shouldHideVehicleName = self.vehicleTypeSubtitle.length == 0;
    // hide the 'color' section if color is not available
    self.shouldHideVehicleColor = self.vehicleColorSubtitle.length == 0;
    // hide the 'plate' section if color is not available
    self.shouldHideVehiclePlate = self.vehiclePlateSubtitle.length == 0;
    
    // hide the whole top part if no vehicle data is available
    self.shouldHideCurrentRentalSection = self.shouldHideVehicleName && self.shouldHideVehicleColor && self.shouldHideVehiclePlate;

    BOOL hideAfterHours = YES;
    if(activeRental.returnLocation && activeRental.isOneWay) {
        hideAfterHours = ![self isRental:activeRental returningAfterHoursOnLocation:activeRental.returnLocation];
    } else if(activeRental.pickupLocation != nil) {
        hideAfterHours = ![self isRental:activeRental returningAfterHoursOnLocation:activeRental.pickupLocation];
    }

    self.shouldHideReturnLocation = hideAfterHours;

    self.shouldHideGetDirections = activeRental.returnLocation.isEmptyLocation;
    
    self.shouldHideRateMyRide = self.activeRental.rateMyRideUrl.length == 0;
}

- (NSAttributedString *)titleForLocation:(EHILocation *)location
{
    if (!location.displayName.length) {
        return nil;
    }
    
    EHIAttributedStringBuilder *builder = EHIAttributedStringBuilder.new
        .fontStyle(EHIFontStyleLight, 24.f).color([UIColor blackColor])
        .text(location.displayName).color([UIColor ehi_greenColor]);
    
    if(location.type == EHILocationTypeAirport) {
        builder.space.appendText(location.airportCode).fontStyle(EHIFontStyleLight, 18.f);
    }
    
    return builder.string;
}


# pragma mark - Actions

- (void)showReturnInstructions
{
    [EHIAnalytics trackAction:EHIAnalyticsDashActionInstructions handler:nil];

    EHILocationPolicy *afterHoursPolicy = self.activeRental.returnLocation.afterHoursPolicy ?: self.activeRental.pickupLocation.afterHoursPolicy;

    EHIInfoModalViewModel *modal = [EHIInfoModalViewModel new];
    modal.title   = afterHoursPolicy.codeDetails;
    modal.details = afterHoursPolicy.text;

    [modal present:nil];
}

- (void)showDirections
{
    [EHIAnalytics trackAction:EHIAnalyticsActionGetDirections handler:nil];
    
    // display an action sheet that allows the user to open maps, or copy the address to pasteboard
    [UIApplication ehi_promptDirectionsForLocation:self.activeRental.returnLocation];
}

- (void)showExtendRentalDialogue
{
    [EHIAnalytics trackAction:EHIAnalyticsDashActionExtend handler:nil];
    
    // present a modal that allows the user to call the return location to extend their rental
    self.router.transition
        .present(EHIScreenExtendRental).object(self.activeRental).start(nil);
}

- (void)showGasStations
{
    NSString *query = EHILocalizedString(@"rental_find_gas_search_query", @"gas", @"map app gas search query");
    
    [EHIAnalytics trackAction:EHIAnalyticsDashActionFindGasStations handler:nil];
    
    // open up the native maps application and search for gas near the return location
    [UIApplication ehi_openMapsWithSearchQuery:query atLocation:self.activeRental.returnLocation];
}

- (void)showLocationDetails
{
    EHILocationDetailsViewModel *viewModel = [[EHILocationDetailsViewModel alloc] initWithModel:self.activeRental.returnLocation];
    viewModel.disablesSelection = YES;
    
    // push the location details screen with the correct location model
    self.router.transition
        .push(EHIScreenLocationDetails).object(viewModel).start(nil);
}

- (void)showRateMyRide
{
    [EHIAnalytics trackAction:EHIAnalyticsDashActionRateVehicle handler:nil];

    NSString *rateMyRideURL = self.activeRental.rateMyRideUrl;
    NSURL *URL = [[NSURL alloc] initWithString:rateMyRideURL];
    NSString *title = EHILocalizedString(@"rate_my_ride_webview_title", @"Rate My Ride", @"");
    EHIWebBrowserViewModel *model = [[EHIWebBrowserViewModel alloc] initWithUrl:URL body:nil title:title];
    self.router.transition
        .present(EHIScreenWebBrowser).object(model).start(nil);
}

//
// Helpers
//

- (BOOL)isRental:(EHIUserRental *)rental returningAfterHoursOnLocation:(EHILocation *)location
{
    if(!location.allowsAfterHoursReturn) {
        return NO;
    }
    
    NSDate *returnDate      = rental.returnDate;
    NSString *returnDateKey = [returnDate ehi_string];
    EHILocationDay *dropDay = location.hours[returnDateKey];

    return [dropDay.dropTimes isOpenForDate:returnDate];
}

@end
