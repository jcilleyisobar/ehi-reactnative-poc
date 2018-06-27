//
//  EHIFormFieldBasicProfileViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 5/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldBasicProfileViewModel.h"

@implementation EHIFormFieldBasicProfileViewModel

# pragma mark - Accessors

- (EHIFormFieldType)type
{
    return EHIFormFieldTypeBasicProfile;
}

- (id)inputValue
{
    return nil;
}

- (BOOL)isUneditable
{
    return YES;
}

# pragma mark - Validation

- (BOOL)validate:(BOOL)showErrors
{
    return YES;
}

@end

@implementation EHIFormFieldBasicProfileViewModel (Generators)

+ (instancetype)nameFieldForProfile:(EHIUserBasicProfile *)profile
{
    EHIFormFieldBasicProfileViewModel *name = [EHIFormFieldBasicProfileViewModel new];
    name.title = EHILocalizedString(@"profile_edit_name_title", @"NAME", @"");
    name.subtitle = profile.fullName;
    name.isLastInGroup = YES;
    
    return name;
}

+ (instancetype)memberIdFieldForProfile:(EHIUserBasicProfile *)profile
{
    EHIFormFieldBasicProfileViewModel *memberID = [EHIFormFieldBasicProfileViewModel new];
    memberID.title = EHILocalizedString(@"profile_edit_member_id_title", @"MEMBER ID", @"");
    memberID.subtitle = profile.loyalty.number;
    memberID.isLastInGroup = YES;
    
    return memberID;
}

+ (instancetype)accountFieldForCorporateAccount:(EHIContractDetails *)corporateAccount
{
    EHIFormFieldBasicProfileViewModel *accountFormField = [EHIFormFieldBasicProfileViewModel new];
    accountFormField.title = EHILocalizedString(@"profile_edit_account_title", @"ACCOUNT", @"");
    accountFormField.subtitle = corporateAccount.name;
    accountFormField.isLastInGroup = YES;
    
    return accountFormField;
}


+ (instancetype)accountFieldForMissingAccount
{
    EHIFormFieldBasicProfileViewModel *account = [EHIFormFieldBasicProfileViewModel new];
    account.title = EHILocalizedString(@"profile_edit_account_title", @"ACCOUNT", @"");
    account.subtitle = EHILocalizedString(@"profile_edit_account_no_account_title", @"Contact your travel representative in order to setup a corporate account with your profile.", @"");
    account.isLastInGroup    = YES;
    account.hideAcessoryIcon = YES;
    
    return account;
}

@end
