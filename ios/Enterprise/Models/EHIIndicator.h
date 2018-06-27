//
//  EHIIndicator.h
//  Enterprise
//
//  Created by mplace on 7/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"

typedef NS_ENUM(NSUInteger, EHIIndicatorCode) {
    EHIIndicatorCodeUnknown,
    EHIIndicatorCodePortOfCall,
    EHIIndicatorCodeRail,
    EHIIndicatorCodeMotorcycle,
    EHIIndicatorCodeExotics,
};

@interface EHIIndicator : EHIModel
@property (assign, nonatomic, readonly) EHIIndicatorCode code;
@end

EHIAnnotatable(EHIIndicator)
