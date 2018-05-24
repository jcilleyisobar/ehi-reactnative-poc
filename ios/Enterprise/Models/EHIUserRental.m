//
//  EHIUserRental.m
//  Enterprise
//
//  Created by fhu on 4/17/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIUserRental.h"
#import "EHIModel_Subclass.h"
#import "EHICarClassVehicleRate.h"

@interface EHIUserRental()
@property (copy  , nonatomic) NSString *pickupTime;
@property (copy  , nonatomic) NSString *returnTime;
@end

@implementation EHIUserRental

- (void)updateWithDictionary:(NSDictionary *)dictionary forceDeletions:(BOOL)forceDeletions
{
    [super updateWithDictionary:dictionary forceDeletions:forceDeletions];
    
    EHICarClassVehicleRate *rate = [EHICarClassVehicleRate modelWithDictionary:dictionary];
    if(rate) {
        NSArray<EHICarClassVehicleRate *> * vehicleRates = (NSArray<EHICarClassVehicleRate> *)@[rate];
        self.carClassDetails.vehicleRates = (NSArray<EHICarClassVehicleRate> *)vehicleRates;
    }
}

+ (NSDictionary *)mappings:(EHIUserRental *)model
{
    return @{
         @"confirmation_number"     : @key(model.confirmationNumber),
         @"ticket_number"           : @key(model.ticketNumber),
         @"invoice_number"          : @key(model.invoiceNumber),
         @"pickup_location"         : @key(model.pickupLocation),
         @"return_location"         : @key(model.returnLocation),
         @"car_class_details"       : @key(model.carClassDetails),
         @"pickup_time"             : @key(model.pickupTime),
         @"return_time"             : @key(model.returnTime),
         @"vehicle_details"         : @key(model.carClassDetails),
         @"vehicle"                 : @key(model.carClassDetails),
         @"car_class_details"       : @key(model.carClassDetails),
         @"vehicle_details"         : @key(model.carClassDetails),
         @"price_summary"           : @key(model.priceSummary),
         @"customer_last_name"      : @key(model.lastName),
         @"customer_first_name"     : @key(model.firstName),
         @"rate_my_ride_url"        : @key(model.rateMyRideUrl),
         @"contract_name"           : @key(model.contractName),
         @"customer_address"        : @key(model.address),
         @"membership_number"       : @key(model.membershipNumber),
         @"rental_agreement_number" : @key(model.rentalAgreementNumber),
         @"vat_number"              : @key(model.vatNumber),
         @"payment_details"         : @key(model.paymentDetails),
         @"requires_watermark"      : @key(model.requiresWatermark)
    };
}

- (void)setReturnTime:(NSString *)returnTime
{
    self.returnDate = [returnTime ehi_dateTime] ?: [returnTime ehi_dateTimeTimeZone];
}

- (void)setPickupTime:(NSString *)pickupTime
{
    self.pickupDate = [pickupTime ehi_dateTime] ?: [pickupTime ehi_dateTimeTimeZone];
}

# pragma mark - Computed

- (NSString *)pickupTimeDisplay
{
    return [self displayTitleForDate:self.pickupDate];
}

- (NSString *)returnTimeDisplay
{
    return [self displayTitleForDate:self.returnDate];
}

- (BOOL)isOneWay
{
    return ![self.pickupLocation.uid isEqualToString:self.returnLocation.uid];
}

- (BOOL)isReturningAfterHours
{
    if(!self.returnLocation.allowsAfterHoursReturn) {
        return NO;
    }
    
    EHILocationDay *day = self.returnLocation.hours[self.returnDate];
    BOOL isClosed       = ![day.standardTimes isOpenForDate:self.returnDate];
    BOOL isAfterHours   = day.dropTimes.isOpenAllDay || [day.dropTimes isOpenForDate:self.returnDate];
    
    return isClosed && isAfterHours;
}

- (BOOL)hasAfterHoursInfo
{
    return self.returnLocation.afterHoursPolicy.name || self.returnLocation.afterHoursPolicy.text;
}

- (NSInteger)points
{
    return self.carClassDetails.redemptionPoints;
}

//
// Helper
//

- (NSString *)displayTitleForDate:(NSDate *)date
{
    if(!date) {
        return nil;
    }
    
    NSString *dateString = [date ehi_localizedMediumDateString];
    NSString *timeString = [date ehi_localizedTimeString];
    
    NSString *format = EHILocalizedString(@"user_rental_display_time", @"#{date} at #{time}", @"date/time display title for pickup & return dates");
    
    return [format ehi_applyReplacementMap:@{
        @"date" : dateString ?: @"",
        @"time" : timeString ?: @""
    }];
}

+ (NSArray *)encodableKeys:(EHIUserRental *)object
{
    return @[
        @key(object.confirmationNumber),
        @key(object.isCurrent),
        @key(object.returnTimeDisplay),
        @key(object.pickupTimeDisplay),
        @key(object.lastName)
    ];
}

# pragma mark - EHIWatchEncodable

- (NSDictionary *)encodeForWatch
{
    EHIUserRental *rental;
    
    return @{
        @key(rental.pickupDate) : [self.pickupDate ehi_dateTimeString] ?: @"",
        @key(rental.returnDate) : [self.returnDate ehi_dateTimeString] ?: @"",
        @key(rental.confirmationNumber) : self.confirmationNumber ?: @"",
        @key(rental.ticketNumber)       : self.ticketNumber ?: @"",
        @key(rental.invoiceNumber)      : self.invoiceNumber ?: @"",
        @key(rental.isCurrent)          : @(self.isCurrent),
        @key(rental.carClassDetails.makeModel)    : self.carClassDetails.makeModel ?: @"",
        @key(rental.carClassDetails.licensePlate) : self.carClassDetails.licensePlate ?: @"",
        @key(rental.pickupLocation)     : [self.pickupLocation encodeForWatch] ?: nil,
        @key(rental.returnLocation)     : [self.returnLocation encodeForWatch] ?: nil,
    };
}

@end
