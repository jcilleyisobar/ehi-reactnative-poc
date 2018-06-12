//
//  EHILocationPolicy.m
//  Enterprise
//
//  Created by Ty Cobb on 2/19/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationPolicy.h"
#import "EHIModel_Subclass.h"

@implementation EHILocationPolicy

- (NSString *)codeText
{
    return [[self.class transformerForKey:@key(self.code)] reverseTransformedValue:@(self.code)];
}

- (EHILocationPolicy *)exclusionPolicy
{
    return self.exclusionPolicies.firstObject;
}

# pragma mark - EHIModel

+ (NSDictionary *)mappings:(EHILocationPolicy *)model
{
    return @{
        @"policy_description" : @key(model.name),
        @"policy_text"        : @key(model.text),
        @"key_facts_section"  : @key(model.keyFactsSection),
        @"key_facts_included" : @key(model.keyFactsIncluded),
        @"description" : @key(model.codeDetails),
        @"mandatory"   : @key(model.isMandatory),
        @"detailed_description" : @key(model.text),
        @"policy_exclusions"    : @key(model.exclusionPolicies)
    };
}

+ (void)registerTransformers:(EHILocationPolicy *)model
{
    [super registerTransformers:model];
    
    EHIMapTransformer *transformer = [[EHIMapTransformer alloc] initWithMap:@{
        @"ADDR" : @(EHILocationPolicyCodeAdditionalDriver),
        @"AGE"  : @(EHILocationPolicyCodeAgeRequirements),
        @"PYMT" : @(EHILocationPolicyCodePayment),
        @"AFHR" : @(EHILocationPolicyCodeAfterHours),
        @"CDW"  : @(EHILocationPolicyCodeDamageWaiver),
        @"EXCL" : @(EHILocationPolicyCodeExclusive),
        @"INS"  : @(EHILocationPolicyCodeInsurance),
        @"PAC"  : @(EHILocationPolicyCodePersonalCoverage),
        @"PAI"  : @(EHILocationPolicyCodePersonalInsurance),
        @"RAP"  : @(EHILocationPolicyCodeRoadsideProtection),
        @"RQMT" : @(EHILocationPolicyCodeRenterRequirements),
        @"SHTL" : @(EHILocationPolicyCodeShuttle),
        @"SLP"  : @(EHILocationPolicyCodeSupplementalLiability),
        @"TCC"  : @(EHILocationPolicyCodeTollConvenience),
        @"MISC" : @(EHILocationPolicyCodeMiscellaneous),
        @"DISP" : @(EHILocationPolicyCodeDispute),
    }];
    transformer.defaultValue = @(EHILocationPolicyCodeUnknown);
    [self key:@key(model.code) registerTransformer:transformer];
    
    EHIMapTransformer *keyFactsTransformer = [[EHIMapTransformer alloc] initWithMap:@{
        @"PROTECTIONS" : @(EHILocationPolicyKeyFactsSectionProtections),
        @"EQUIPMENT"   : @(EHILocationPolicyKeyFactsSectionEquipment),
        @"MINIMUM_REQUIREMENTS" : @(EHILocationPolicyKeyFactsSectionMinimumRequirements),
        @"ADDITIONAL"  : @(EHILocationPolicyKeyFactsSectionAdditional),
        @"QUESTIONS"   : @(EHILocationPolicyKeyFactsSectionVehicleReturnAndDamages)
    }];
    keyFactsTransformer.defaultValue = @(EHILocationPolicyKeyFactsSectionProtections);
    [self key:@key(model.keyFactsSection) registerTransformer:keyFactsTransformer];
}

# pragma mark - Comparison

- (NSComparisonResult)compare:(EHILocationPolicy *)policy
{
    // if this is not a policy push it to the bottom
    if(![policy isKindOfClass:[EHILocationPolicy class]]) {
        return NSOrderedAscending;
    }

    // sort by policy code
    return policy.code > self.code ? NSOrderedAscending : NSOrderedDescending;
}

@end
