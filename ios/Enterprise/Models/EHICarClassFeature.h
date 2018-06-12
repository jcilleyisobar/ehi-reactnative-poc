//
//  EHICarClassFeature.h
//  Enterprise
//
//  Created by mplace on 6/5/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"

typedef NS_ENUM(NSUInteger, EHICarClassFeatureCode) {
    EHICarClassFeatureCodeAutomaticTransmission = 25,
    EHICarClassFeatureCodeManualTransmission    = 26,
};

@interface EHICarClassFeature : EHIModel
@property (assign, nonatomic) EHICarClassFeatureCode code;
@property (copy  , nonatomic) NSString *details;
@end

EHIAnnotatable(EHICarClassFeature)