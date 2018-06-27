//
//  EHIRequiredInfoViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 02/05/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIRequiredInfoViewModel.h"

@interface EHIRequiredInfoViewModel ()
@property (copy  , nonatomic) NSString *title;
@end

@implementation EHIRequiredInfoViewModel

+ (instancetype)modelForInfoType:(EHIRequiredInfoType)type
{
    EHIRequiredInfoViewModel *model = EHIRequiredInfoViewModel.new;
    model.title = [@"* " stringByAppendingString:[self titleForInfoType:type]];
    
    return model;
}

# pragma mark - Factory

+ (NSString *)titleForInfoType:(EHIRequiredInfoType)type
{
    switch(type) {
        case EHIRequiredInfoTypeForgotPassword:
            return EHILocalizedString(@"gdpr_forgot_password", @"Required to change your password", @"");
        case EHIRequiredInfoTypeProfile:
            return EHILocalizedString(@"gdpr_my_profile", @"Required to maintain your membership", @"");
        case EHIRequiredInfoTypeLookupRental:
            return EHILocalizedString(@"gdpr_look_up_rental", @"Required to look up a reservation", @"");
        case EHIRequiredInfoTypeEnroll:
            return EHILocalizedString(@"gdpr_enroll", @"Required to complete your enrollment", @"");
        case EHIRequiredInfoTypeReservation:
            return EHILocalizedString(@"gdpr_res_flow", @"Required to complete your reservation", @"");
    }
}

@end
