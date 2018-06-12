//
//  EHILocation.m
//  Enterprise
//
//  Created by Ty Cobb on 1/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel_Subclass.h"
#import "EHIUserLocation.h"
#import "EHIFavoritesManager.h"
#import "EHIConfiguration.h"
#import "EHIDataStore.h"
#import "EHIPhoneNumberFormatter.h"
#import "EHIIndicator.h"

#define EHILocationOneWaysFlag @"ONE_WAYS"
#define EHILocationMotorcyclesFlag @"MOTORCYCLES"
#define EHILocationExoticsFlag @"EXOTICS"
#define EHILocationOffersPickupFlag @"PYU"

@interface EHILocation ()
@property (copy  , nonatomic) NSArray<EHILocationPolicy> *policies;
@property (copy  , nonatomic, readonly) NSString *defaultName;
@property (copy  , nonatomic, readonly) NSArray *attributes;
@property (copy  , nonatomic, readonly) NSArray<EHIIndicator> *indicators;
@property (assign, nonatomic) EHILocationType type;
@property (assign, nonatomic) EHILocationType indicatorType;
@end

@implementation EHILocation

- (void)updateWithDictionary:(NSDictionary *)dictionary
{
    [super updateWithDictionary:dictionary];
    
    // preserve sorting for policies at the model level
    self.policies = (NSArray<EHILocationPolicy> *)[self.policies sort];
    // determine the location type from the GBO response via the indicators field
    self.indicatorType = [self locationTypeForIndicators:self.indicators];
}

- (void)parseDictionary:(NSMutableDictionary *)dictionary
{
    [super parseDictionary:dictionary];
    
    // downcase the type string before transforming it into an enumeration value
    [dictionary ehi_transform:@key(self.type) selector:@selector(uppercaseString)];
}

//
// Helper
//

- (EHILocationType)locationTypeForIndicators:(NSArray *)indicators
{
    EHIIndicator *indicator = (indicators ?: @[]).find(^(EHIIndicator *indicator) {
        return indicator.code == EHIIndicatorCodeMotorcycle
            || indicator.code == EHIIndicatorCodePortOfCall
            || indicator.code == EHIIndicatorCodeRail
            || indicator.code == EHIIndicatorCodeExotics;
    });
    
    switch (indicator.code) {
        case EHIIndicatorCodeMotorcycle:
            return EHILocationTypeMotorcycle;
        case EHIIndicatorCodePortOfCall:
            return EHILocationTypePort;
        case EHIIndicatorCodeRail:
            return EHILocationTypeTrain;
        case EHIIndicatorCodeExotics:
            return EHILocationTypeExotics;
        default:
            return EHILocationTypeUnknown;
    }
}

# pragma mark - Accessors

- (BOOL)isExotics
{
    return self.type == EHILocationTypeExotics || (self.attributes ?: @[]).any(^(NSString *line) {
        return [line isEqualToString:EHILocationExoticsFlag];
    });
}

- (BOOL)shouldMoveVansToEndOfList
{
    return [self.address.country shouldMoveVansToEndOfList];
}

- (BOOL)shouldShowIdentityCheckWithExternalVendorMessage
{
    return [self.address.country shouldShowIdentityCheckWithExternalVendorMessage];
}

- (BOOL)hasConflicts
{
    return self.hasPickupConflicts || self.hasDropoffConflicts;
}

- (BOOL)hasPickupConflicts
{
    return self.pickupValidity.status == EHILocationValidityStatusInvalidAllDay
        || self.pickupValidity.status == EHILocationValidityStatusInvalidAtThatTime;
}

- (BOOL)hasDropoffConflicts
{
    return self.dropoffValidity.status == EHILocationValidityStatusInvalidAllDay
        || self.dropoffValidity.status == EHILocationValidityStatusInvalidAtThatTime;
}

- (BOOL)hasAfterHours
{
    return self.dropoffValidity.status == EHILocationValidityStatusValidAfterHours;
}

- (BOOL)isAllDayClosedForPickup
{
	return self.pickupValidity.status == EHILocationValidityStatusInvalidAllDay;
}

- (BOOL)isAllDayClosedForDropoff
{
	return self.dropoffValidity.status == EHILocationValidityStatusInvalidAllDay;
}

- (BOOL)allowsOneWay
{
    return (self.attributes ?: @[]).any(^(NSString *line) {
        return [line isEqualToString:EHILocationOneWaysFlag];
    });
}

- (BOOL)hasMotorcycles
{
    return [self.attributes containsObject:EHILocationMotorcyclesFlag];
}

- (BOOL)isFavorited
{
    return [[EHIFavoritesManager sharedInstance] locationIsFavorited:self];
}

- (BOOL)isRecentActivity
{
    __block BOOL isRecentActivity = NO;
    
    [EHIDataStore find:self.class handler:^(NSArray *models) {
        isRecentActivity = models.has(self);
    }];
    
    return isRecentActivity;
}

- (NSString *)displayName
{
    return self.localizedName.length ? self.localizedName : self.address.addressLines.firstObject;
}

- (NSString *)phoneNumber
{
    NSArray *officePhones = (self.phones ?: @[]).select(^(EHIPhone *phone) {
        return phone.type == EHIPhoneTypeOffice;
    });
    
    // default to first if we have no office phones
    if(officePhones.count == 0) {
        EHIPhone *phone = self.phones.firstObject;
        return phone.number;
    }
    
    // return only phone or use flag when multiple office phones
    EHIPhone *phone = officePhones.count == 1 ? officePhones.firstObject : officePhones.find(^(EHIPhone *phone) {
        return phone.isDefault;
    });
    
    return phone.number;
}

- (NSString *)formattedPhoneNumber
{
    return [EHIPhoneNumberFormatter format:self.phoneNumber countryCode:self.address.countryCode];
}

# pragma mark - Computed

- (BOOL)isEmptyLocation
{
    return !self.position.latitude || !self.position.longitude;
}

- (EHILocationPolicy *)afterHoursPolicy
{
    return self.policies.find(^(EHILocationPolicy *policy) {
        return policy.code == EHILocationPolicyCodeAfterHours;
    });
}

- (BOOL)offersPickup
{
    return self.businessTypes.any(^(NSDictionary *dict){
        return [dict[@"code"] isEqualToString:EHILocationOffersPickupFlag];
    });
}

- (EHILocationType)type
{
    return self.indicatorType ?: _type;
}

- (BOOL)promptsForFlightInfo
{
    return self.isMultiTerminal
        && self.type == EHILocationTypeAirport;
}

- (BOOL)opensUnusually
{
    return self.isAlwaysOpen || self.isOpenSundays;
}

- (NSString *)countryCode
{
    return self.address.countryCode;
}

# pragma mark - Branding

- (BOOL)isOnBrand
{
    return self.brand == EHILocationBrandEnterprise;
}

- (NSString *)brandTitle
{
    switch(self.brand) {
        case EHILocationBrandEnterprise:
            return EHILocalizedString(@"location_brand_enterprise", @"Enterprise", @"Brand title for Enterprise locations");
        case EHILocationBrandNational:
            return EHILocalizedString(@"location_brand_national", @"National", @"Brand title for National locations");
        case EHILocationBrandAlamo:
            return EHILocalizedString(@"location_brand_alamo", @"Alamo", @"Brand title for Alamo locations");
        default: return nil;
    }
}

- (NSString *)brandUrl
{
    switch(self.brand) {
        case EHILocationBrandAlamo:
            return [EHIConfiguration configuration].alamoReservationUrl;
        case EHILocationBrandNational:
            return [EHIConfiguration configuration].nationalReservationUrl;
        default: return nil;
    }
}

# pragma mark - Times

- (NSArray *)days
{
    return (self.weeks ?: @[]).flatMap(^(EHILocationWeek *weeks) {
        return weeks.days;
    });
}

- (EHILocationTimes *)today
{
    // flatten out all the days, and return the first day that claims to be today
    return (self.weeks ?: @[]).flatMap(^(EHILocationWeek *weeks) {
        return weeks.days;
    }).find(^(EHILocationTimes *day) {
        return day.isToday;
    });
}

# pragma mark - Tags

- (NSString *)distanceTag
{
    return self.position ? [[EHIUserLocation location] localizedDistanceToCoordinate:self.position.coordinate] : nil;
}

- (NSString *)openSundaysTag
{
    return self.isOpenSundays ? EHILocalizedString(@"locations_open_sundays_tag", @"OPEN SUNDAYS", @"Text for 'Open Sundays' location tag") : nil;
}

- (NSString *)alwaysOpenTag
{
    return self.isAlwaysOpen ? EHILocalizedString(@"locations_always_open_tag", @"Open 24/7", @"") : nil;
}

# pragma mark - Utility Methods

+ (id<EHIMappable>)processSolrDictionaries:(id<EHIMappable>)dictionaries
{
    return [dictionaries map:^(NSDictionary *dictionary) {
        return [self processSolrDictionary:dictionary];
    }];
}

+ (NSDictionary *)processSolrDictionary:(NSDictionary *)dictionary
{
    EHILocation *location;
    
    NSMutableDictionary *result = [dictionary mutableCopy];
    
    // nest the address
    [result ehi_nest:@key(location.address) fields:@[
        @"addressLines", @"city", @"state",
        @"countryCode", @"postalCode",
    ]];

    // nest the phone number, and wrap it in an array
    [result ehi_nest:@key(location.phones) fields:@[ @"phoneNumber" ]];
    [result ehi_wrap:@key(location.phones)];
    
    // nest the days into the hours structure, and wrap them in an array
    [result ehi_nest:@key(location.weeks) fields:@[ @"location-hours" ]];
    [result ehi_wrap:@key(location.weeks)];
    
    // nest the position
    [result ehi_nest:@key(location.position) fields:@[
        @"latitude", @"longitude"
    ]];
    
    return [result copy];
}

- (BOOL)hasCloseTime:(NSDate *)date
{
    EHILocationWeek *standardWeek = (self.weeks ?: @[]).find(^(EHILocationWeek *week) {
        return week.type == EHILocationHoursTypeStandard;
    });
    
    EHILocationTimes *day = (standardWeek.days ?: @[]).find(^(EHILocationTimes *day) {
        return [day.date ehi_isEqual:date granularity:NSCalendarDayGranularity];
    });
    
    return [day doesCloseAtDate:date];
}

# pragma mark - Colleciton

+ (void)prepareCollection:(EHICollection *)collection
{
    // store an unlimited number of locations
    collection.historyLimit = 5;
}

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHILocation *)model
{
    return @{
        @"peopleSoftId"                 : @key(model.uid),
		@"id"                           : @key(model.uid),
        @"defaultLocationName"          : @key(model.defaultName),
        @"name"                         : @key(model.localizedName),
        @"locationNameTranslation"      : @key(model.localizedName),
        @"location_type"                : @key(model.type),
        @"locationType"                 : @key(model.type),
        @"gps"                          : @key(model.position),
        @"time_zone_id"                 : @key(model.timeZoneId),
        @"drive_distance"               : @key(model.driveDistance),
        @"filter_tags"                  : @key(model.filterTags),
        @"bookingURL"                   : @key(model.bookingUrl),
        @"business_types"               : @key(model.businessTypes),
        @"after_hours_pickup"           : @key(model.allowsAfterHoursPickup),
        @"afterHoursPickup"             : @key(model.allowsAfterHoursPickup),
        @"after_hours_return"           : @key(model.allowsAfterHoursReturn),
        @"afterHoursDropoff"            : @key(model.allowsAfterHoursReturn),
        @"is24HourLocation"             : @key(model.isAlwaysOpen),
        @"hours"                        : @key(model.weeks),
        @"openSundays"                  : @key(model.isOpenSundays),
        @"airport_code"                 : @key(model.airportCode),
        @"airline_details"              : @key(model.airlines),
        @"reservation_booking_system"   : @key(model.reservationBookingSystem),
        @"multi_terminal"               : @key(model.isMultiTerminal),
        @"pickupValidity"               : @key(model.pickupValidity),
        @"dropoffValidity"              : @key(model.dropoffValidity),
    };
}

+ (void)registerTransformers:(EHILocation *)model
{
    [self key:@key(model.type) registerMap:@{
        @"CITY"    : @(EHILocationTypeCity),
        @"AIRPORT" : @(EHILocationTypeAirport),
        @"RAIL"    : @(EHILocationTypeTrain),
        @"PORT_OF_CALL" : @(EHILocationTypePort),
    } defaultValue:@(EHILocationTypeUnknown)];
   
    [self key:@key(model.brand) registerTransformer:EHILocationBrandTransformer()];
    [self key:@key(model.reservationBookingSystem) registerTransformer:EHILocationReservationBookingSystemTransformer()];
}

NSValueTransformer * EHILocationBrandTransformer()
{
    EHIMapTransformer *transformer = [[EHIMapTransformer alloc] initWithMap:@{
        @"ENTERPRISE" : @(EHILocationBrandEnterprise),
        @"NATIONAL"   : @(EHILocationBrandNational),
        @"ALAMO"      : @(EHILocationBrandAlamo),
    }];

    transformer.defaultValue = @(EHILocationBrandUnknown);
    
    return transformer;
}

NSValueTransformer * EHILocationReservationBookingSystemTransformer()
{
    EHIMapTransformer *transformer = [[EHIMapTransformer alloc] initWithMap:@{
        @"ECARS" : @(EHILocationReservationBookingSystemEcars),
        @"ODYSSEY"   : @(EHILocationReservationBookingSystemOdyssey),
    }];
    
    transformer.defaultValue = @(EHILocationReservationBookingSystemUnknown);
    
    return transformer;
}

# pragma mark - EHIAnalyticsEncodable

+ (void)encodeWithContext:(EHIAnalyticsContext *)context instance:(nullable EHILocation *)instance
{
    context[EHIAnalyticsLocIdKey]               = instance.uid;
    context[EHIAnalyticsLocNameKey]             = instance.localizedName;
    context[EHIAnalyticsLocLatitudeKey]         = instance.position ? @(instance.position.latitude)  : nil;
    context[EHIAnalyticsLocLongitudeKey]        = instance.position ? @(instance.position.longitude) : nil;
    context[EHIAnalyticsLocTypeKey]             = instance.analyticsType;
    context[EHIAnalyticsLocCountryKey]          = instance.address.countryCode;
    context[EHIAnalyticsLocAfterHoursAvailable] = @(instance.hasAfterHours);
}

//
// Helpers
//

- (NSString *)analyticsType
{
    switch(self.type) {
        case EHILocationTypeCity:
            return EHIAnalyticsLocTypeBranch;
        case EHILocationTypeAirport:
            return EHIAnalyticsLocTypeAirport;
        case EHILocationTypeTrain:
            return EHIAnalyticsLocTypeRail;
        case EHILocationTypePort:
            return EHIAnalyticsLocTypePort;
        default: return nil;
    }
}

# pragma mark - EHIEncodableObject

+ (NSArray *)encodableKeys:(EHILocation *)location
{
    return @[
        @key(location.localizedName),
        @key(location.address),
        @key(location.driveDistance),
        @key(location.type),
        @key(location.airportCode),
        @key(location.brand),
        @key(location.attributes),
        @key(location.position),
        @key(location.isOpenSundays),
        @key(location.attributes)
    ];
}

# pragma mark - EHIWatchEncodable

- (NSArray *)encodedWayfindings {
    return self.wayfindings.map(^(EHILocationWayfinding *wayfinding) {
        return [wayfinding encodeForWatch];
    });
}

- (NSDictionary *)encodeForWatch
{
    return @{
        @"locationNameTranslation"     : self.localizedName ?: @"",
        @"addressLines"                : self.address.formattedAddress ?: @"",
        @"brand"                       : @(self.brand),
        @"type"                        : @(self.type),
        @"latitude"                    : @(self.position.latitude),
        @"longitude"                   : @(self.position.longitude),
        @"phoneNumber"                 : self.phoneNumber,
        @"wayfindings"                 : [self encodedWayfindings],
    };
}

@end
