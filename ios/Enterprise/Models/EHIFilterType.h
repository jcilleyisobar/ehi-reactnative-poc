//
//  EHIFilterType.h
//  Enterprise
//
//  Created by mplace on 6/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

typedef NS_ENUM(NSUInteger, EHIFilterType) {
    EHIFilterTypeUnknown,
    EHIFilterTypeWildcard,
    EHIFilterTypeLocationHours,
    EHIFilterTypeLocationType,
    EHIFilterTypeLocationMiscellaneous,
    EHIFilterTypeTransmission,
    EHIFilterTypePassengerCapacity,
    EHIFilterTypeFuel,
    EHIFilterTypeClass
};
