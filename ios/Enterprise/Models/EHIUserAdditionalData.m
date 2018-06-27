//
//  EHIUserAdditionalData.m
//  Enterprise
//
//  Created by Rafael Ramos on 18/08/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIUserAdditionalData.h"

@interface EHIUserAdditionalData ()
@property (assign, nonatomic) BOOL isUpdatingProfile;
@property (assign, nonatomic) BOOL isCreatingProfile;
@end

@implementation EHIUserAdditionalData

+ (instancetype)modelForProfileUpdate
{
    EHIUserAdditionalData *model = EHIUserAdditionalData.new;
    model.isUpdatingProfile = YES;
    
    return model;
}

+ (instancetype)modelForProfileCreation
{
    EHIUserAdditionalData *model = EHIUserAdditionalData.new;
    model.isCreatingProfile = YES;
    
    return model;
}

+ (NSDictionary *)mappings:(EHIUserAdditionalData *)model
{
    return @{
        @"email_unique"                 : @key(model.isUniqueEmail),
        @"read_only_restrictions"       : @key(model.restrictions),
        @"editable"                     : @key(model.isEditable),
        @"branch_enrolled"              : @key(model.isBranchEnrolled),
        @"auth_token"                   : @key(model.authToken),
        @"encrypted_credential"         : @key(model.credentials),
        @"credit_card_near_expiration"  : @key(model.isCreditCardNearExpiration),
        @"credit_card_expired"          : @key(model.isCreditCardExpired),
        @"driver_license_expired"       : @key(model.isDriverLicenseExpired),
    };
}

# pragma mark - EHINetworkEncodable

- (void)encodeWithRequest:(EHINetworkRequest *)request
{
    NSString *mode = [NSString new];
    if(self.isUpdatingProfile) {
        mode = @"UPDATE";
    }

    if(self.isCreatingProfile) {
        mode = @"ENROLLMENT";
    }
    
    request[@"update_profile_mode"] = mode;
}

@end
