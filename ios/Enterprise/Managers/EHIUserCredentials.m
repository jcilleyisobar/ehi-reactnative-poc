//
//  EHIUserCredentials.m
//  Enterprise
//
//  Created by Ty Cobb on 4/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIUserCredentials.h"
#import "EHIModel_Subclass.h"

@implementation EHIUserCredentials

- (void)parseDictionary:(NSMutableDictionary *)dictionary
{
    [super parseDictionary:dictionary];
   
    // create a dummy uid if necessary
    if(!self.uid) {
        dictionary[@key(self.uid)] = [NSUUID UUID].UUIDString;
    }
}

# pragma mark - Accessors

- (BOOL)isValid
{
    return self.encrypedCredentials.length > 0;
}

# pragma mark - EHINetworkEncodable

- (void)encodeWithRequest:(EHINetworkRequest *)request
{
    // use the encrypted credentials if we have that and if we are not doing a pasword update request
    if(self.encrypedCredentials && !self.updatedPassword) {
        request[@"ec"] = self.encrypedCredentials;
        request[@"remember_credentials"] = EHIStringifyFlag(YES);
    }
    // otherwise, perform username/password auth
    else {
        request[@"username"] = self.identification;
        request[@"password"] = self.password;
        request[@"remember_credentials"] = EHIStringifyFlag(self.remembersCredentials);
        
        request[@"new_password"] = self.updatedPassword.length > 0 ? self.updatedPassword : nil;
    }
    
    request[@"accept_decline_version"] = self.acceptedTermsVersion;
}

# pragma mark - Collection

+ (void)prepareCollection:(EHICollection *)collection
{
    collection.historyLimit = 1;
    collection.isSecure = YES;
}

# pragma mark - EHIAnalyticsEncodable

+ (void)encodeWithContext:(EHIAnalyticsContext *)context instance:(nullable EHIUserCredentials *)instance
{
    context[EHIAnalyticsUserKeepSigninKey] = @((BOOL)instance.remembersCredentials);
}

# pragma mark - EHIEncodableObject

+ (NSArray *)unencodableKeys:(EHIUserCredentials *)object
{
    return @[
        @key(object.acceptedTermsVersion),
    ];
}

@end
