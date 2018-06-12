//
//  EHISingleDateCalendarEnums.h
//  Enterprise
//
//  Created by Egor Vorotnikov on 5/31/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

typedef NS_ENUM(NSInteger, EHISingleDateCalendarType) {
    EHISingleDateCalendarTypePickup,
    EHISingleDateCalendarTypeReturn,
};

typedef NS_ENUM(NSInteger, EHISingleDateCalendarFlow) {
    EHISingleDateCalendarFlowUnknown,
    EHISingleDateCalendarFlowLocationsMap,
    EHISingleDateCalendarFlowLocationsFilter,
};