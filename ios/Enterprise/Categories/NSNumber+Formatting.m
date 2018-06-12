//
//  NSNumber+Formatting.m
//  Enterprise
//
//  Created by mplace on 5/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "NSNumber+Formatting.h"

@implementation NSNumber (Formatting)

- (NSString *)ehi_localizedDecimalString
{
    return [NSNumberFormatter localizedStringFromNumber:self numberStyle:NSNumberFormatterDecimalStyle];
}

- (BOOL)ehi_isBooleanLike
{
    // boolean NSNumbers often use char as the underlying type
    return strcmp(self.objCType, @encode(BOOL)) == 0
        || strcmp(self.objCType, @encode(char)) == 0;
}

- (NSNumber *)ehi_negateBool
{
    return [NSNumber numberWithBool:![self boolValue]];
}

@end
