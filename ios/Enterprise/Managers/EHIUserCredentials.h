//
//  EHIUserCredentials.h
//  Enterprise
//
//  Created by Ty Cobb on 4/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHIUserCredentials.h"
#import "EHIAnalyticsEncodable.h"

@interface EHIUserCredentials : EHIModel <EHIAnalyticsEncodable>

@property (copy  , nonatomic) NSString *identification;
@property (copy  , nonatomic) NSString *password;
@property (copy  , nonatomic) NSString *updatedPassword;
@property (copy  , nonatomic) NSString *encrypedCredentials;
@property (copy  , nonatomic) NSString *acceptedTermsVersion;

@property (assign, nonatomic) BOOL isValid;
@property (assign, nonatomic) BOOL isEmeraldCredentials;
@property (assign, nonatomic) BOOL remembersCredentials;
@property (copy  , nonatomic) NSDate *expiryDate;
@property (copy  , nonatomic) NSDate *authenticationDate;

@end
