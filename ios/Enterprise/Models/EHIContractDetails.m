//
//  EHIContractDetails.m
//  Enterprise
//
//  Created by mplace on 2/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIContractDetails.h"
#import "EHIModel_Subclass.h"
#import "NSLocale+Country.h"

@implementation EHIContractDetails

- (NSString *)maskedId
{
    return [self.uid ehi_maskLast:4];
}

- (NSString *)contractNumber
{
    return self.uid;
}

- (NSString *)formattedTitle
{
    return [NSString stringWithFormat:@"%@ (%@)", self.name, self.billingAccount ?: self.maskedId];
}

- (BOOL)billingAccountExists
{
    return self.billingAccount.length > 0;
}

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHIContractDetails *)model
{
    return @{
        @"contract_number"                  : @key(model.uid),
        @"contract_name"                    : @key(model.name),
        @"contract_description"             : @key(model.details),
        @"contract_short_description"       : @key(model.shortDescription),
        @"contract_accepts_billing"         : @key(model.customerAcceptsBilling),
        @"third_party_email_notify"         : @key(model.thirdPartyEmailRequired),
        @"marketing_message_indicator"      : @key(model.marketingEmailOptIn),
        @"additional_information"           : @key(model.additionalInformation),
        @"contract_billing_account"         : @key(model.billingAccount),
        @"contract_type"                    : @key(model.contractType),
        @"terms_and_conditions"             : @key(model.termsAndConditions),
        @"contract_has_additional_benefits" : @key(model.contractHasAdditionalBenefits)
    };
}

+ (void)registerTransformers:(EHIContractDetails *)model
{
    [self key:@key(model.contractType) registerMap:@{
        @"CORPORATE" : @(EHIContractTypeCorporate),
        @"PROMOTION" : @(EHIContractTypePromotion),
    } defaultValue:@(EHIContractTypeUnknown)];
}

@end
