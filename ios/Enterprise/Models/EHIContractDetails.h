//
//  EHIContractDetails.h
//  Enterprise
//
//  Created by mplace on 2/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHIContractAdditionalInfo.h"

typedef NS_ENUM(NSInteger, EHIContractType) {
    EHIContractTypeUnknown,
    EHIContractTypeCorporate,
    EHIContractTypePromotion
};

@interface EHIContractDetails : EHIModel

@property (copy  , nonatomic, readonly) NSString *name;
@property (copy  , nonatomic, readonly) NSString *details;
@property (copy  , nonatomic, readonly) NSString *shortDescription;
@property (copy  , nonatomic, readonly) NSString *billingAccount;
@property (copy  , nonatomic, readonly) NSArray<EHIContractAdditionalInfo> *additionalInformation;
@property (assign, nonatomic, readonly) BOOL customerAcceptsBilling;
@property (assign, nonatomic, readonly) BOOL thirdPartyEmailRequired;
@property (assign, nonatomic, readonly) BOOL marketingEmailOptIn;
@property (assign, nonatomic, readonly) EHIContractType contractType;
@property (copy  , nonatomic, readonly) NSString *termsAndConditions;
@property (assign, nonatomic, readonly) BOOL contractHasAdditionalBenefits;

// computed propertities
@property (copy  , nonatomic, readonly) NSString *maskedId;
@property (copy  , nonatomic, readonly) NSString *contractNumber;
@property (copy  , nonatomic, readonly) NSString *formattedTitle;
@property (assign, nonatomic, readonly) BOOL billingAccountExists;

@end
