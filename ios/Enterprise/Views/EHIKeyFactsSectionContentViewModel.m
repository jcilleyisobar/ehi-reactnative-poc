//
//  EHIKeyFactsSectionContentViewModel.m
//  Enterprise
//
//  Created by fhu on 11/4/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIKeyFactsSectionContentViewModel.h"
#import "NSAttributedString+Construction.h"
#import "EHIReservationBuilder.h"
#import "EHICarClassExtra.h"

@interface EHIKeyFactsSectionContentViewModel()
@property (copy  , nonatomic) NSString *contentText;
@end

@implementation EHIKeyFactsSectionContentViewModel

- (instancetype)init
{
    if(self = [super init]) {
        _hidesBottomThickDivider = YES;
    }
    return self;
}

- (NSAttributedString *)contentAttributedText
{
    if(_contentAttributedText) {
        return _contentAttributedText;
    }
    return EHIAttributedStringBuilder.new.text(self.contentText ?: @"").string;
}

#pragma mark - Generators

+ (instancetype)modelForType:(EHIKeyFactsSectionContentType)type withReservation:(EHIReservation *)reservation
{
    switch (type) {
        case EHIKeyFactsSectionContentTypeDamageLiability:
            return [self modelForDamageLiability];
        case EHIKeyFactsSectionContentTypeProtection:
            return [self modelForProtection:reservation];
        case EHIKeyFactsSectionContentTypeEquipment:
            return [self modelForEquipment:reservation];
        case EHIKeyFactsSectionContentTypeMinimumRequirements:
            return [self modelForMinimum:reservation];
        case EHIKeyFactsSectionContentTypeAdditionalPolicies:
            return [self modelForAdditionalPolicies:reservation];
        case EHIKeyFactsSectionContentTypeAdditionalLiabilities:
            return [self modelForAdditionalLiabilities];
        case EHIKeyFactsSectionContentTypeVehicleReturnAndDamages:
            return [self modelForVehicleReturnAndDamages:reservation];
    }
}

+ (instancetype)modelForDamageLiability
{
    EHIKeyFactsSectionContentViewModel *model = [self new];
    model.headerText = EHILocalizedString(@"key_facts_damage_liability_title", @"Damage Liability", @"");
    model.contentText =  [NSString stringWithFormat:@"%@\n\n%@",
        EHILocalizedString(@"key_facts_damage_theft", @"Unless you purchase a waiver or protection product or one is included in your reservation as specified above, you are responsible to the Rental Company for theft or any damage to the vehicle during the full period of your vehicle hire. This is true even if the accident was not your fault. You are liable for loss of revenue if the vehicle cannot be rented because it is damaged or stolen, a reasonable claims administration fee, and diminishment of value and for any towing, storage or impound fees of the vehicle. Unless third party liability protection is included in your rental as provided below or is purchased by you, you are also responsible for injury or damages to third parties.", @""),
        EHILocalizedString(@"key_facts_liability_for_damage", @"Please note your liability for damage extends until the vehicle is checked in by the Rental Company. Unless otherwise provided in the rental agreement, if you return the vehicle after the Rental Companies business hours, you will remain responsible for any damage or loss in accordance with the rental agreement until the Rental Company reopens and conducts the post rental inspection.", @"")];
    
    return model;
}

+ (instancetype)modelForProtection:(EHIReservation*)reservation
{
    EHIKeyFactsSectionContentViewModel *model = [self new];
    
    model.subHeaderText = EHILocalizedString(@"key_facts_protection_products_title", @"Protection Products", @"");

    NSArray *protectionPolicies = (reservation.keyFactsPolicies ?: @[]).select(^(EHILocationPolicy *policy) {
        return policy.keyFactsSection == EHILocationPolicyKeyFactsSectionProtections;
    });

    //graft on extras to each protection policy
    NSArray *protectionExtras = [reservation.selectedCarClass vehicleRateForPrepay:reservation.prepaySelected].extras.insurance ?: @[];
    for (EHILocationPolicy *policy in protectionPolicies) {
        policy.extra = protectionExtras.find(^(EHICarClassExtra *extra) {
            return [extra.keyFactsCode isEqualToString:policy.codeText];
        });
    }

    NSArray *includedProtectionPolicies = protectionPolicies.map(^(EHILocationPolicy *policy) {
        return policy.keyFactsIncluded ? [[EHIKeyFactsContentViewModel alloc] initWithModel:policy] : nil;
    });
    NSArray *optionalProtectionPolicies = protectionPolicies.map(^(EHILocationPolicy *policy) {
        return !policy.keyFactsIncluded && policy.extra  && !policy.extra.isIncluded ? [[EHIKeyFactsContentViewModel alloc] initWithModel:policy] : nil;
    });
    NSArray *optionalUnavailableProtectionPolicies = protectionPolicies.map(^(EHILocationPolicy *policy) {
        return !policy.keyFactsIncluded && !policy.extra ? [[EHIKeyFactsContentViewModel alloc] initWithModel:policy] : nil;
    });

    model.subHeaderDetailsText = includedProtectionPolicies.count
        ? EHILocalizedString(@"key_facts_included_protection_subtitle", @"Your rental agreement will include the following equipment/products:", @"")
        : EHILocalizedString(@"key_facts_no_included_protections", @"This rental does not include protection products", @"");

    NSString *secondHeaderContent = EHILocalizedString(@"key_facts_no_protections", @"There are no purchasable protection products available for your rental.", @"");
    if(optionalProtectionPolicies.count) {
        secondHeaderContent = EHILocalizedString(@"key_facts_optional_bookable_protections_subtitle", @"You have the following optional protection products available for an additional charge:", @"");
    } else if(optionalUnavailableProtectionPolicies.count) {
        secondHeaderContent = EHILocalizedString(@"key_facts_optional_non_bookable_protections_subtitle", @"You will have the following optional protection products available for purchase at time of pick-up:", @"");
    }

    EHIKeyFactsContentViewModel *contentViewModel = [EHIKeyFactsContentViewModel modelWithContent:secondHeaderContent];
    contentViewModel.hasBlackDivider = YES;

    model.contentList = @[
        includedProtectionPolicies,
        contentViewModel,
        optionalProtectionPolicies,
        optionalUnavailableProtectionPolicies
    ].flatten;

    return model;
}

+ (instancetype)modelForEquipment:(EHIReservation*)reservation
{
    EHIKeyFactsSectionContentViewModel *model = [self new];

    model.subHeaderText = EHILocalizedString(@"key_facts_equipment_products_title", @"Equipment Products", @"");

    NSArray *extras = [reservation.selectedCarClass vehicleRateForPrepay:reservation.prepaySelected].extras.equipment ?: @[];
    NSArray *includedExtras = extras.map(^(EHICarClassExtra *extra) {
        return extra.isIncluded ? [[EHIKeyFactsContentViewModel alloc] initWithModel:extra] : nil;
    });
    NSArray *optionalExtras = extras.map(^(EHICarClassExtra *extra) {
        BOOL optional = extra.status == EHICarClassExtraStatusMandatory ||
                        extra.status == EHICarClassExtraStatusOptional;

        return optional ? [[EHIKeyFactsContentViewModel alloc] initWithModel:extra] : nil;
    });

    model.subHeaderDetailsText = includedExtras.count
        ? EHILocalizedString(@"key_facts_included_equipment_subtitle", @"Your rental agreement will include the following equipment/products:", @"")
        : EHILocalizedString(@"key_facts_no_included_equipment", @"This rental does not include equipment/products", @"");

    EHIKeyFactsContentViewModel *contentViewModel = [EHIKeyFactsContentViewModel modelWithContent:EHILocalizedString(@"key_facts_optional_equipment_subtitle", @"You have the following optional equipment/products available for an additional charge:", @"")];
    contentViewModel.hasBlackDivider = YES;

    model.contentList = @[
        includedExtras,
        contentViewModel,
        optionalExtras
    ].flatten;

    return model;
}

+ (instancetype)modelForMinimum:(EHIReservation*)reservation
{
    EHIKeyFactsSectionContentViewModel *model = [self new];
    model.hidesBottomThickDivider = NO;

    model.headerText = EHILocalizedString(@"key_facts_minimum_requirements_title", @"Minimum Requirements", @"");

    NSArray *minimumPolicies = (reservation.keyFactsPolicies ?: @[]).map(^(EHILocationPolicy *policy) {
        return policy.keyFactsSection == EHILocationPolicyKeyFactsSectionMinimumRequirements ? [[EHIKeyFactsContentViewModel alloc] initWithModel:policy] : nil;
    });

    model.subHeaderDetailsText = minimumPolicies.count
        ? EHILocalizedString(@"key_facts_minimum_requirements_subtitle", @"Your rental will be subject to the following minimum requirements:", @"")
        : EHILocalizedString(@"key_facts_no_minimum_requirements", @"There are no minimum requirements for your rental.", @"");

    if(!minimumPolicies.count) {
        model.contentText = EHILocalizedString(@"key_facts_no_minimum_requirements", @"There are no minimum requirements for your rental.", @"");
    } else {
        model.contentList = minimumPolicies.flatten;
    }

    return model;
}

+ (instancetype)modelForAdditionalPolicies:(EHIReservation*)reservation
{
    EHIKeyFactsSectionContentViewModel *model = [self new];
    model.hidesTopThickDivider = YES;
    model.hidesBottomThickDivider = NO;

    model.headerText = EHILocalizedString(@"key_facts_additional_rental_policies_title", @"Additional Rental Policies", @"");
    
    NSArray *additionalPolicies = (reservation.keyFactsPolicies ?: @[]).map(^(EHILocationPolicy *policy) {
        return policy.keyFactsSection == EHILocationPolicyKeyFactsSectionAdditional ? [[EHIKeyFactsContentViewModel alloc] initWithModel:policy] : nil;
    });
    
    model.subHeaderDetailsText = additionalPolicies.count
        ? EHILocalizedString(@"key_facts_additional_rental_policies_subtitle", @"Your rental will be subject to the following additional rental policies:", @"")
        : EHILocalizedString(@"key_facts_no_additional_rental_policies", @"There are no additional rental policies for your rental.", @"");
    
    if(!additionalPolicies.count) {
        model.contentText = EHILocalizedString(@"key_facts_no_additional_rental_policies", @"There are no additional rental policies for your rental.", @"");
    } else {
        model.contentList = additionalPolicies.flatten;
    }
    
    return model;
}

+ (instancetype)modelForAdditionalLiabilities
{
    EHIKeyFactsSectionContentViewModel *model = [self new];
    model.hidesTopThickDivider = YES;
    model.hidesBottomThickDivider = NO;
    
    model.headerText = EHILocalizedString(@"key_facts_additional_liabilites_title", @"Additional Liabilities", @"");
    model.contentAttributedText = EHIAttributedStringBuilder.new
        .text(EHILocalizedString(@"key_facts_additional_liabilities_subtitle", @"You will be responsible for the following additional charges or liabilities if incurred:", @""))
        .newline.newline.append([NSAttributedString attributedStringListWithItems:@[
            EHILocalizedString(@"key_facts_additional_liabilities_modification_charges", @"Additional rental charges for changes you make to the booked rental vehicle, rental period, or optional products.", @""),
            EHILocalizedString(@"key_facts_additional_liabilities_damage_theft", @"Damages, theft, or third party liabilities not covered by a protection product.", @""),
            EHILocalizedString(@"key_facts_additional_liabilities_fines_penalty_charges", @"Any fines or penalty charges relating to the operation of the vehicle during your rental period, such as parking or speeding fines, plus reasonable administration charges.", @""),
            EHILocalizedString(@"key_facts_additional_liabilities_legal_fees", @"Any legal fees incurred collecting any payments due under the terms of the rental agreement.", @""),
            EHILocalizedString(@"key_facts_additional_liabilities_collection_fee", @"A reasonable collection fee if a vehicle is not returned to the original rental office.", @""),
            EHILocalizedString(@"key_facts_additional_liabilities_cleaning_fee", @"The cost of cleaning the vehicle if you return the vehicle in a dirty condition.", @"")
        ] formatting:NO])
        .newline.newline.appendText(EHILocalizedString(@"key_facts_additional_liabilities_rental_agreement", @"When you complete the rental agreement you will be required to present a valid credit or debit card as security for any charges incurred during your rental. Your signature on the rental agreement will pre-authorise the Rental Company to charge the card for future payments that become due. The Rental Company may also hold a deposit against these future liabilities. In the country of your rental the deposit amount varies.", @""))
        .string;
    
    model.hidesBottomThickDivider = YES;
    
    return model;
}

+ (instancetype)modelForVehicleReturnAndDamages:(EHIReservation*)reservation
{
    EHIKeyFactsSectionContentViewModel *model = [self new];
    model.hidesBottomThickDivider = NO;
    model.hidesTopThickDivider    = YES;
    
    model.headerText      = EHILocalizedString(@"key_facts_contact_information_title", @"Vehicle Returns & Damages", @"");

    NSArray *returnPolicies = (reservation.keyFactsPolicies ?: @[]).map(^(EHILocationPolicy *policy) {
        return policy.keyFactsSection == EHILocationPolicyKeyFactsSectionVehicleReturnAndDamages ? [[EHIKeyFactsContentViewModel alloc] initWithModel:policy] : nil;
    });
    
    NSString *textContent = EHILocalizedString(@"key_facts_contact_information_branch", @"When you return the vehicle...", @"");
    EHIKeyFactsContentViewModel *contentViewModel = [EHIKeyFactsContentViewModel modelWithContent:textContent];
    
    model.contentList = @[
        contentViewModel,
        returnPolicies
    ].flatten;
    
    return model;
}

@end
