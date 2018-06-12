//
//  EHICarClassExtra.h
//  Enterprise
//
//  Created by mplace on 2/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIPrice.h"
#import "EHIPriceRateType.h"
#import "EHIAnalyticsEncodable.h"

typedef NS_ENUM(NSInteger, EHICarClassExtraStatus) {
    EHICarClassExtraStatusUnknown,
    EHICarClassExtraStatusOptional,
    EHICarClassExtraStatusMandatory,
    EHICarClassExtraStatusWaived,
    EHICarClassExtraStatusIncluded,
};

typedef NS_ENUM(NSInteger, EHICarClassExtraAllocation) {
    EHICarClassExtraAllocationUnknown,
    EHICarClassExtraAllocationOnRequest,
    EHICarClassExtraAllocationFreeSell,
    EHICarClassExtraAllocationStopSell,
};

@interface EHICarClassExtra : EHIModel <EHIAnalyticsEncodable>

@property (copy  , nonatomic, readonly) NSString *code;
@property (copy  , nonatomic, readonly) NSString *keyFactsCode;
@property (assign, nonatomic, readonly) EHIPriceRateType rateType;
@property (assign, nonatomic, readonly) EHICarClassExtraStatus status;
@property (assign, nonatomic, readonly) EHICarClassExtraAllocation allocation;
@property (strong, nonatomic, readonly) EHIPrice *rate;
@property (strong, nonatomic, readonly) EHIPrice *maxPrice;
@property (assign, nonatomic, readonly) NSInteger maxQuantity;
@property (assign, nonatomic) NSInteger selectedQuantity;

// computed properties
@property (nonatomic, readonly) EHIPrice *total;
@property (nonatomic, readonly) NSString *name;
@property (nonatomic, readonly) NSString *shortDetails;
@property (nonatomic, readonly) NSString *longDetails;
@property (nonatomic, readonly) NSString *statusText;
@property (nonatomic, readonly) NSString *frequencyText;
@property (nonatomic, readonly) NSString *rateDescriptionWithMax;
@property (nonatomic, readonly) BOOL isSelected;
@property (nonatomic, readonly) BOOL isOptional;
@property (nonatomic, readonly) BOOL isIncluded;
@property (nonatomic, readonly) BOOL isMandatory;
@property (nonatomic, readonly) BOOL isWaived;

@property (assign, nonatomic) BOOL shouldShowSectionTitle;
@property (assign, nonatomic) BOOL lastInSection;

@end


EHIAnnotatable(EHICarClassExtra);
