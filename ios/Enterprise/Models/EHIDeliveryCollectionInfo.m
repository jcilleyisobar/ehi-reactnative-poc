//
//  EHIDeliveryLocation.m
//  Enterprise
//
//  Created by Alex Koller on 6/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDeliveryCollectionInfo.h"

@implementation EHIDeliveryCollectionInfo

# pragma mark - Equality

- (BOOL)isEqual:(id)object
{
    BOOL isEqual = [super isEqual:object];
    
    // if super failed, see if our dictionary representations are the same
    if(!isEqual && [object isKindOfClass:self.class]) {
        EHIDeliveryCollectionInfo *info = (EHIDeliveryCollectionInfo *)object;

        NSMutableDictionary *selfDictionary = [[NSDictionary dictionaryWithEncodableObject:self] mutableCopy];
        NSMutableDictionary *infoDictionary = [[NSDictionary dictionaryWithEncodableObject:info] mutableCopy];
        
        // ignore comments for equality
        [selfDictionary removeObjectForKey:@key(self.comments)];
        [infoDictionary removeObjectForKey:@key(info.comments)];
        
        isEqual = [selfDictionary isEqualToDictionary:infoDictionary];
    }
    
    return isEqual;
}

# pragma mark - EHINetworkEncodable

- (void)encodeWithRequest:(EHINetworkRequest *)request
{
    request[@"address"] = self.address;
    request[@"phone"] = self.phone;
    request[@"comments"] = self.comments;
}

@end
