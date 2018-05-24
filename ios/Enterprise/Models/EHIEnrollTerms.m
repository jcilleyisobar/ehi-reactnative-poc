//
//  EHIEnrollTerms.m
//  Enterprise
//
//  Created by Rafael Ramos on 8/19/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIEnrollTerms.h"
#import "EHIModel_Subclass.h"

@implementation EHIEnrollTerms

- (void)parseDictionary:(NSMutableDictionary *)dictionary
{
    [super parseDictionary:dictionary];
    
    [dictionary ehi_transform:@key(self.acceptDeclineDate) selector:@selector(ehi_date)];
}

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHIEnrollTerms *)model
{
    return @{
        @"accept_decline"         : @key(model.acceptDecline),
        @"accept_decline_version" : @key(model.acceptDeclineVersion),
        @"accept_decline_date"    : @key(model.acceptDeclineDate),
    };
}

- (void)encodeWithRequest:(EHINetworkRequest *)request
{
    request[@"accept_decline"] = EHIStringifyFlag(self.acceptDecline);
}

@end
