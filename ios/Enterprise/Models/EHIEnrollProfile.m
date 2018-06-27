//
//  EHIEnrollProfile.m
//  Enterprise
//
//  Created by Rafael Ramos on 8/19/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIEnrollProfile.h"
#import "EHIUser.h"

@interface EHIEnrollProfile ()
@property (strong, nonatomic) EHIUser *user;
@property (copy  , nonatomic) NSString *password;
@property (assign, nonatomic) BOOL acceptedTerms;
@end

@implementation EHIEnrollProfile

+ (instancetype)modelForUser:(EHIUser *)user password:(NSString *)password acceptedTerms:(BOOL)terms
{
    EHIEnrollProfile *model = EHIEnrollProfile.new;
    model.user = user;
    model.password      = password;
    model.acceptedTerms = terms;

    return model;
}

- (NSString *)individualId
{
    return self.user.individualId;
}

- (NSString *)firstName
{
    return self.user.firstName;
}

- (NSString *)lastName
{
    return self.user.lastName;
}

- (NSString *)email
{
    return self.user.contact.maskedEmail ?: self.user.contact.email;
}

- (EHIAddress *)address
{
    return self.user.address;
}

- (EHIUserLicenseProfile *)license
{
    return self.user.license;
}

- (NSDate *)birthDate
{
    return self.user.license.birthdate;
}

- (EHIEnrollTerms *)terms
{
    EHIEnrollTerms *terms;
    return [EHIEnrollTerms modelWithDictionary:@{
        @key(terms.acceptDecline) : @(self.acceptedTerms)
    }];
}

- (EHIUserPreferencesProfile *)preferences
{
    return self.user.preference;
}

- (NSArray<EHIPhone> *)phones
{
    return self.user.contact.phones.copy;
}

- (void)encodeWithRequest:(EHINetworkRequest *)request
{
    request[@"individual_id"]        = self.individualId;
    request[@"first_name"]           = self.firstName;
    request[@"last_name"]            = self.lastName;
    request[@"date_of_birth"]        = self.birthDate.ehi_string;
    request[@"address"]              = self.address;
    request[@"email"]                = self.email;
    request[@"phones"]               = self.phones;
    request[@"drivers_license"]      = self.license;
    request[@"password"]             = self.password;
    request[@"preference"]           = self.preferences;
    request[@"terms_and_conditions"] = self.terms;
}

@end
