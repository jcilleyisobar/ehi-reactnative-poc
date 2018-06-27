//
//  EHIReservationAdditionalInfo.h
//  Enterprise
//
//  Created by Alex Koller on 7/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHIContractAdditionalInfo.h"

@interface EHIContractAdditionalInfoValue : EHIModel

@property (copy, nonatomic) NSString *value;

// linking
@property (copy  , nonatomic, readonly) NSString *name;
@property (assign, nonatomic, readonly) NSInteger sequence;
@property (assign, nonatomic, readonly) EHIContractAdditionalInfoType type;
@property (assign, nonatomic, readonly) BOOL isRequired;

@property (assign, nonatomic) BOOL shouldShowSectionTitle;
@property (assign, nonatomic) BOOL isLastInSection;

- (void)linkContractAdditionalInfo:(EHIContractAdditionalInfo *)info;

@end

EHIAnnotatable(EHIContractAdditionalInfoValue);