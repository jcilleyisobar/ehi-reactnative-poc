//
//  EHILocationPolicy.h
//  Enterprise
//
//  Created by Ty Cobb on 2/19/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"

typedef NS_ENUM(NSInteger, EHILocationPolicyCode) {
    EHILocationPolicyCodeRenterRequirements,
    EHILocationPolicyCodeAgeRequirements,
    EHILocationPolicyCodePayment,
    EHILocationPolicyCodeAdditionalDriver,
    EHILocationPolicyCodeAfterHours,
    EHILocationPolicyCodeDamageWaiver,
    EHILocationPolicyCodeExclusive,
    EHILocationPolicyCodeInsurance,
    EHILocationPolicyCodePersonalCoverage,
    EHILocationPolicyCodePersonalInsurance,
    EHILocationPolicyCodeRoadsideProtection,
    EHILocationPolicyCodeShuttle,
    EHILocationPolicyCodeSupplementalLiability,
    EHILocationPolicyCodeTollConvenience,
    EHILocationPolicyCodeDispute,
    EHILocationPolicyCodeMiscellaneous,
    EHILocationPolicyCodeUnknown,
};

typedef NS_ENUM(NSInteger, EHILocationPolicyKeyFactsSection) {
    EHILocationPolicyKeyFactsSectionProtections,
    EHILocationPolicyKeyFactsSectionEquipment,
    EHILocationPolicyKeyFactsSectionMinimumRequirements,
    EHILocationPolicyKeyFactsSectionAdditional,
    EHILocationPolicyKeyFactsSectionVehicleReturnAndDamages
};

EHIAnnotatable(EHILocationPolicy)

@class EHICarClassExtra;

@interface EHILocationPolicy : EHIModel
@property (assign, nonatomic, readonly) EHILocationPolicyCode code;
@property (assign, nonatomic, readonly) EHILocationPolicyKeyFactsSection keyFactsSection;
@property (copy  , nonatomic, readonly) NSArray<EHILocationPolicy> *exclusionPolicies;
@property (copy  , nonatomic, readonly) EHILocationPolicy *exclusionPolicy;
@property (copy  , nonatomic, readonly) NSString *name;
@property (copy  , nonatomic, readonly) NSString *text;
@property (copy  , nonatomic, readonly) NSString *codeDetails;
@property (copy  , nonatomic, readonly) NSString *codeText;
@property (assign, nonatomic, readonly) BOOL isMandatory;
@property (assign, nonatomic, readonly) BOOL keyFactsIncluded;

//grafted on properties
@property (strong, nonatomic) EHICarClassExtra *extra;



@end
