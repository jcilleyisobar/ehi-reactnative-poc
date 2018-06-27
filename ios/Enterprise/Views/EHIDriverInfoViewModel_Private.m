//
//  EHIDriverInfoViewModel_Private.m
//  Enterprise
//
//  Created by Bruno Fernandes Campos on 1/17/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIDriverInfoViewModel_Private.h"

@implementation EHIDriverInfoViewModel (Private)

- (EHIDriverInfo *)buildDriverInfoForRequestWithDriverInfo:(EHIDriverInfo *)driverInformation
{
    EHIDriverInfo *driverInfo = driverInformation.copy;

    [driverInfo updateWithDictionary:@{
        @key(driverInfo.firstName)               : self.firstName,
        @key(driverInfo.lastName)                : self.lastName,
        @key(driverInfo.shouldSerialize)         : @(self.shouldSaveDriverInfo),
    }];
    
    EHIPhone *phone    = driverInfo.phone.copy ?: EHIPhone.new;
    phone.number       = self.phone.ehi_isMasked ? driverInformation.phone.number : self.phone;
    phone.maskedNumber = self.phone.ehi_isMasked ? self.phone : nil;
    driverInfo.phone   = phone;
    
    driverInfo.email       = self.email.ehi_isMasked ? driverInformation.email : self.email;
    driverInfo.maskedEmail = self.email.ehi_isMasked ? self.email : nil;
    
    driverInfo.wantsEmailNotifications = self.specialOffersOptIn;

    return driverInfo;
}

@end
