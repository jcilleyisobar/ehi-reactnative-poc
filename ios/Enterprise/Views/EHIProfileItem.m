//
//  EHIProfileBasicItem.m
//  Enterprise
//
//  Created by fhu on 5/18/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIProfileItem.h"
#import "EHIUser.h"

@implementation EHIProfileItem

+ (NSArray *)memberInfoItems
{
    EHIUser *user = [EHIUser currentUser];
    
    EHIProfileItem *name = [EHIProfileItem new];
    name.title = EHILocalizedString(@"profile_name_header", @"NAME", @"header for name cell in my profile");
    name.data = [NSString stringWithFormat:@"%@ %@", user.profiles.basic.firstName, user.profiles.basic.lastName].capitalizedString;
    
    EHIProfileItem *email = [EHIProfileItem new];
    email.title = EHILocalizedString(@"profile_email_header", @"EMAIL", @"header for email cell in my profile");
    email.data = user.contact.maskedEmail;
    
    EHIProfileItem *memberID = [EHIProfileItem new];
    memberID.title = EHILocalizedString(@"profile_member_id_header", @"MEMBER ID", @"header for member id cell in my profile");
    memberID.data = user.profiles.basic.loyalty.number;
    
    EHIProfileItem *address = [EHIProfileItem new];
    address.title = EHILocalizedString(@"profile_address_header", @"ADDRESS", @"header for address cell in my profile");
    address.data = user.address.addressLines.firstObject;
    
    EHIProfileItem *phoneNumbers = [EHIProfileItem new];
    phoneNumbers.title = EHILocalizedString(@"profile_phone_header", @"PHONE NUMBER", @"header for phone cell in my profile");
    phoneNumbers.data = user.contact.phones;
    phoneNumbers.type = EHIProfileCellTypePhone;
    
    EHIProfileItem *corporate = [EHIProfileItem new];
    corporate.title = EHILocalizedString(@"profile_corporate_header", @"ACCOUNT", @"header for corporate in my profile");
    corporate.data = user.corporateContract.formattedTitle;
    
    return @[name, memberID, corporate, email, address, phoneNumbers];
}

+ (NSArray *)driverLicenseItems
{
    EHIUserLicenseProfile *license = [EHIUser currentUser].license;
    
    EHIProfileItem *licenseNumber = [EHIProfileItem new];
    licenseNumber.title = EHILocalizedString(@"profile_edit_license_number_title", @"DRIVER'S LICENSE", @"");
    licenseNumber.data = license.licenseNumber;
    
    EHIProfileItem *issueDate = [EHIProfileItem new];
    issueDate.title = EHILocalizedString(@"profile_edit_license_issue_date", @"ISSUE DATE", @"");
    issueDate.data = [NSDate ehi_localizedMaskedDate:license.maskedLicenseIssue] ?: [license.licenseIssue ehi_localizedShortDateString];
    
    EHIProfileItem *expirationDate = [EHIProfileItem new];
    expirationDate.title = EHILocalizedString(@"profile_edit_license_expiration_date_title", @"EXPIRATION DATE", @"");
    expirationDate.data = [NSDate ehi_localizedMaskedDate:license.maskedLicenseExpiry];
    
    return @[licenseNumber, issueDate, expirationDate];
}

@end
