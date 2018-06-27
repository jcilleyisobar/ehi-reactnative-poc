//
//  EHIGeofenceNote.h
//  Enterprise
//
//  Created by Alex Koller on 12/10/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"

#define EHIGeofencingPickupRadius (5000)
#define EHIGeofencingReturnRadius (100)

typedef NS_ENUM(NSUInteger, EHIGeonotificationType) {
    EHIGeonotificationTypeUnknown,
    EHIGeonotificationTypeAfterHours,
    EHIGeonotificationTypeWayfinding,
};

@class EHIUserRental;
@interface EHIGeonotification : EHIModel

@property (assign, nonatomic, readonly) EHIGeonotificationType type;
@property (strong, nonatomic, readonly) CLRegion *region;
@property (copy  , nonatomic, readonly) NSString *message;
@property (strong, nonatomic, readonly) NSDate *latestFireDate;
@property (copy  , nonatomic, readonly) NSDictionary *userInfo;

// computed
@property (assign, nonatomic, readonly) BOOL shouldFire;

+ (instancetype)geonotificationForAfterHoursRental:(EHIUserRental *)rental;
+ (instancetype)geonotificationForWayfindingRental:(EHIUserRental *)rental;

@end
