//
//  EHIReservationAdditionalInfo.m
//  Enterprise
//
//  Created by Alex Koller on 7/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIContractAdditionalInfoValue.h"

@interface EHIContractAdditionalInfoValue ()
@property (weak, nonatomic) EHIContractAdditionalInfo *contractAdditionalInfo;
@end

@implementation EHIContractAdditionalInfoValue

# pragma mark - Accessors

- (NSString *)name
{
    return self.contractAdditionalInfo.name;
}

- (NSInteger)sequence
{
    return self.contractAdditionalInfo.sequence;
}

- (EHIContractAdditionalInfoType)type
{
    return self.contractAdditionalInfo.type;
}

- (BOOL)isRequired
{
    return self.contractAdditionalInfo.isRequired;
}

# pragma mark - Linking

- (void)linkContractAdditionalInfo:(EHIContractAdditionalInfo *)info
{
    // one-way link the info
    self.contractAdditionalInfo = info;
}

# pragma mark - EHINetworkEncodable

- (void)encodeWithRequest:(EHINetworkRequest *)request
{
    [super encodeWithRequest:request];
    
    request[@"id"] = self.uid;
    request[@"value"] = self.value;
}

@end
