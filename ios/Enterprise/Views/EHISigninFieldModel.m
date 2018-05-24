//
//  EHISigninFieldModel.m
//  Enterprise
//
//  Created by Ty Cobb on 4/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHISigninFieldModel.h"

@implementation EHISigninFieldModel

+ (NSArray *)enterprisePlusFields
{
    EHISigninFieldModel *field;
    
    return [EHISigninFieldModel modelsWithDictionaries:@[@{
        @key(field.title)       : EHILocalizedString(@"login_identification_label", @"EMAIL OR MEMBER ID", @"Label for login identification field"),
        @key(field.placeholder) : EHILocalizedString(@"login_identification_placeholder", @"Email/Member ID", @"Placeholder for login identification field"),
        @key(field.returnType)  : @(UIReturnKeyNext)
    },@{
        @key(field.title)       : EHILocalizedString(@"login_password_label", @"PASSWORD", @"Label for login password field"),
        @key(field.placeholder) : EHILocalizedString(@"login_password_placeholder", @"Password", @"Placeholder for login password field"),
        @key(field.isSecure)    : @YES,
        @key(field.returnType)  : @(UIReturnKeyDone)
    }]];
}

+ (NSArray *)emeraldClubFields
{
    EHISigninFieldModel *field;
    
    return [EHISigninFieldModel modelsWithDictionaries:@[@{
        @key(field.title)       : EHILocalizedString(@"emerald_login_identification_username_title", @"USERNAME OR EMERALD CLUB #", @""),
        @key(field.placeholder) : EHILocalizedString(@"emerald_club_login_username_placeholder", @"Emerald Club Member ID", @""),
        @key(field.returnType)  : @(UIReturnKeyNext)
    },@{
        @key(field.title)       : EHILocalizedString(@"emerald_login_identification_password_title", @"PASSWORD", @""),
        @key(field.placeholder) : EHILocalizedString(@"emerald_club_login_password_placeholder", @"Password", @""),
        @key(field.isSecure)    : @YES,
        @key(field.returnType)  : @(UIReturnKeyDone)
    }]];
}

@end
