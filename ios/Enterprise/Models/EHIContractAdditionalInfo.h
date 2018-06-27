//
//  EHICorporateAdditionalInfo.h
//  Enterprise
//
//  Created by fhu on 6/8/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHIContractAdditionalInfoDropdownValue.h"

typedef NS_ENUM(NSInteger, EHIContractAdditionalInfoType) {
    EHIContractAdditionalInfoTypeUnknown,
    EHIContractAdditionalInfoTypeDate,
    EHIContractAdditionalInfoTypeDropdownList,
    EHIContractAdditionalInfoTypeExactValue,
    EHIContractAdditionalInfoTypePattern,
    EHIContractAdditionalInfoTypeText
};


@interface EHIContractAdditionalInfo : EHIModel

@property (copy  , nonatomic, readonly) NSString *name;
@property (copy  , nonatomic, readonly) NSString *placeholder;
@property (copy  , nonatomic, readonly) NSArray<EHIContractAdditionalInfoDropdownValue> *supportedValues;
@property (assign, nonatomic, readonly) EHIContractAdditionalInfoType type;
@property (assign, nonatomic, readonly) NSInteger sequence;
@property (assign, nonatomic, readonly) BOOL isRequired;
@property (assign, nonatomic, readonly) BOOL shouldDisplayOnSplash;
@property (assign, nonatomic, readonly) BOOL isModifiable;
@property (assign, nonatomic, readonly) BOOL isPreRate;

/** computed properties */
@property (strong, nonatomic, readonly) NSArray *options;

@end

EHIAnnotatable(EHIContractAdditionalInfo);