//
//  EHILocation.h
//  Enterprise
//
//  Created by Ty Cobb on 1/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationHours.h"
#import "EHILocationWeek.h"
#import "EHILocationDistance.h"
#import "EHILocationCoordinate.h"
#import "EHILocationWayfinding.h"
#import "EHILocationPolicy.h"
#import "EHIAddress.h"
#import "EHIAirline.h"
#import "EHIPhone.h"
#import "EHIAnalyticsEncodable.h"
#import "EHILocationRenterAge.h"
#import "EHIWatchEncodable.h"
#import "EHILocationValidity.h"

typedef NS_ENUM(NSInteger, EHILocationType) {
    EHILocationTypeUnknown,
    EHILocationTypeAirport,
    EHILocationTypeCity,
    EHILocationTypePort,
    EHILocationTypeTrain,
    EHILocationTypeMotorcycle,
    EHILocationTypeExotics,
};

typedef NS_ENUM(NSInteger, EHILocationBrand) {
    EHILocationBrandUnknown,
    EHILocationBrandEnterprise,
    EHILocationBrandNational,
    EHILocationBrandAlamo,
};

typedef NS_ENUM(NSInteger, EHILocationReservationBookingSystem) {
    EHILocationReservationBookingSystemUnknown,
    EHILocationReservationBookingSystemEcars,
    EHILocationReservationBookingSystemOdyssey
};

@interface EHILocation : EHIModel <EHIAnalyticsEncodable, EHIWatchEncodable>

@property (copy  , nonatomic, readonly) NSString *localizedName;
@property (copy  , nonatomic, readonly) NSString *details;
@property (copy  , nonatomic, readonly) NSString *airportCode;
@property (copy  , nonatomic, readonly) NSString *timeZoneId;
@property (copy  , nonatomic, readonly) NSString *bookingUrl;
@property (copy  , nonatomic, readonly) NSArray *businessTypes;
@property (copy  , nonatomic, readonly) NSArray *filterTags;
@property (copy  , nonatomic, readonly) NSArray<EHILocationWayfinding> *wayfindings;
@property (copy  , nonatomic, readonly) NSArray<EHILocationWeek> *weeks;
@property (copy  , nonatomic, readonly) NSArray<EHIPhone> *phones;
@property (copy  , nonatomic, readonly) NSArray<EHIAirline> *airlines;
@property (copy  , nonatomic, readonly) NSArray<EHILocationPolicy> *policies;
@property (strong, nonatomic, readonly) EHIAddress *address;
@property (strong, nonatomic, readonly) EHILocationDistance *driveDistance;
@property (strong, nonatomic, readonly) EHILocationCoordinate *position;
@property (strong, nonatomic, readonly) EHILocationValidity *pickupValidity;
@property (strong, nonatomic, readonly) EHILocationValidity *dropoffValidity;
@property (assign, nonatomic, readonly) EHILocationType type;
@property (assign, nonatomic, readonly) EHILocationBrand brand;
@property (assign, nonatomic, readonly) EHILocationReservationBookingSystem reservationBookingSystem;
@property (assign, nonatomic, readonly) BOOL allowsAfterHoursPickup;
@property (assign, nonatomic, readonly) BOOL allowsAfterHoursReturn;
@property (assign, nonatomic, readonly) BOOL isAlwaysOpen;
@property (assign, nonatomic, readonly) BOOL isOpenSundays;
@property (assign, nonatomic, readonly) BOOL allowsOneWay;
@property (assign, nonatomic, readonly) BOOL hasMotorcycles;
@property (assign, nonatomic, readonly) BOOL isMultiTerminal;
@property (assign, nonatomic, readonly) BOOL isExotics;

// grafted-on from solr
@property (strong, nonatomic) EHILocationHours *hours;
@property (copy  , nonatomic) NSArray<EHILocationRenterAge> *ageOptions;

// computed properties
@property (nonatomic, readonly) BOOL isFavorited; // reactive
@property (nonatomic, readonly) BOOL isRecentActivity;
@property (nonatomic, readonly) BOOL isOnBrand;
@property (nonatomic, readonly) BOOL offersPickup;
@property (nonatomic, readonly) BOOL isEmptyLocation;
@property (nonatomic, readonly) BOOL promptsForFlightInfo;
@property (nonatomic, readonly) BOOL opensUnusually;
@property (nonatomic, readonly) BOOL shouldMoveVansToEndOfList;
@property (nonatomic, readonly) BOOL shouldShowIdentityCheckWithExternalVendorMessage;
@property (nonatomic, readonly) BOOL hasConflicts;
@property (nonatomic, readonly) BOOL hasPickupConflicts;
@property (nonatomic, readonly) BOOL hasDropoffConflicts;
@property (nonatomic, readonly) BOOL hasAfterHours;
@property (nonatomic, readonly) BOOL isAllDayClosedForPickup;
@property (nonatomic, readonly) BOOL isAllDayClosedForDropoff;

@property (nonatomic, readonly) NSString *displayName;
@property (nonatomic, readonly) NSString *phoneNumber;
@property (nonatomic, readonly) NSString *formattedPhoneNumber;
@property (nonatomic, readonly) NSString *distanceTag;
@property (nonatomic, readonly) NSString *openSundaysTag;
@property (nonatomic, readonly) NSString *alwaysOpenTag;
@property (nonatomic, readonly) NSString *brandTitle;
@property (nonatomic, readonly) NSString *brandUrl;
@property (nonatomic, readonly) NSArray *days;
@property (nonatomic, readonly) NSString *countryCode;
@property (nonatomic, readonly) EHILocationTimes *today;
@property (nonatomic, readonly) EHILocationPolicy *afterHoursPolicy;

// grafted on properties
@property (assign, nonatomic) BOOL isNearbyLocation;

// hack: this renders the compressed view for the location cell corresponding to this location
@property (assign, nonatomic) BOOL hidesDetails;

@property (strong, nonatomic) NSDate *pickupDate;
@property (strong, nonatomic) NSDate *dropOffDate;

// utility methods
+ (NSDictionary *)processSolrDictionary:(NSDictionary *)dictionary;
+ (id<EHIMappable>)processSolrDictionaries:(id<EHIMappable>)dictionaries;

- (BOOL)hasCloseTime:(NSDate *)date;

// transformer for mapping brand values
extern NSValueTransformer * EHILocationBrandTransformer();

@end

EHIAnnotatable(EHILocation)
