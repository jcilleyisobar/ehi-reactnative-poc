//
//  EHIUserBasicProfile.m
//  Enterprise
//
//  Created by mplace on 2/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIUserBasicProfile.h"
#import "EHIModel_Subclass.h"

@implementation EHIUserBasicProfile

+ (NSDictionary *)mappings:(EHIUserBasicProfile *)model
{
    return @{
        @"last_name"         : @key(model.lastName),
        @"first_name"        : @key(model.firstName),
        @"loyalty_data"      : @key(model.loyalty),
        @"user_name"         : @key(model.username),
        @"date_of_birth"     : @key(model.maskedBirthDate),
    };
}

# pragma mark - Computed

- (NSString *)fullName
{
    NSString *name = @"";
    name = [name ehi_appendComponent:self.firstName joinedBy:@" "];
    name = [name ehi_appendComponent:self.lastName];
    name = [name stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
    
    return name;
}

@end
