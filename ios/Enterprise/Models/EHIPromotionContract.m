//
//  EHIPromotionContract.m
//  Enterprise
//
//  Created by Rafael Ramos on 3/30/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPromotionContract.h"
#import "EHIModel_Subclass.h"

#ifdef TESTS
#define EHIPromotionMock 1
#elif DEBUG
#define EHIPromotionMock (EHIMockEnabled && 1)
#else
#define EHIPromotionMock 1
#endif

@implementation EHIPromotionContract

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHIPromotionContract *)model
{
    return @{
        @"contract_number"   : @key(model.code),
        @"contract_type"     : @key(model.contractType),
        @"contract_sub_type" : @key(model.type),
        @"mob_descriptions"  : @key(model.descriptions),
        @"mob_description"   : @key(model.descriptions),
    };
}

+ (void)registerTransformers:(EHIPromotionContract *)model
{
    [super registerTransformers:model];
    
    [self key:@key(model.type) registerMap:@{
        @"WEEKEND_SPECIAL"     : @(EHIPromotionContractTypeWeekendSpecial),
        @"LMS"                 : @(EHIPromotionContractTypeLastMinuteSpecial),
        @"MON_THURS_START_END" : @(EHIPromotionContractTypeMondayThrusdayPromotions),
        @"SATURDAY_NIGHT_STAY" : @(EHIPromotionContractTypeSaturdayNightStayPromotions),
    } defaultValue:@(EHIPromotionContractTypeUnkown)];
}

# pragma mark - EHIPromotionRenderable

- (NSString *)title
{
    return self.name;
}

- (NSString *)shortTitle
{
    return self.shortDescription.ehi_stripHtml;
}

- (NSString *)subtitle
{
    return self.shortDescription.ehi_stripHtml;
}

- (NSString *)longDescription
{
    return self.details;
}

- (NSString *)cid
{
    return self.code;
}

- (NSString *)terms
{
    return self.termsAndConditions;
}

- (NSString *)imageName
{
    return @"weekend_special";
}

@end
