//
//  EHIReservationPriceSubtitleType.h
//  Enterprise
//
//  Created by mplace on 8/18/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

typedef NS_ENUM(NSUInteger, EHIReservationPriceButtonType) {
    EHIReservationPriceButtonTypePrice,
    EHIReservationPriceButtonTypeCallForAvailability,
    EHIReservationPriceButtonTypeWebBook,
    EHIReservationPriceButtonTypeSecretRate
};

typedef NS_ENUM(NSUInteger, EHIReservationPriceButtonSubtitleType) {
    EHIReservationPriceButtonSubtitleTypeNone,
    EHIReservationPriceButtonSubtitleTypeVehicle,
    EHIReservationPriceButtonSubtitleTypeTotalCost,
    EHIReservationPriceButtonSubtitleTypeTotalCostOptionalNote,
    EHIReservationPriceButtonSubtitleTypeAfterPoints,
    EHIReservationPriceButtonSubtitleTypeModify,
    EHIReservationPriceButtonSubtitleTypeUpdatedTotal
};
