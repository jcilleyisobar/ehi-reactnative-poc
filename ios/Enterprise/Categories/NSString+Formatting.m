//
//  NSString+Formatting.m
//  Enterprise
//
//  Created by Ty Cobb on 2/18/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "NSString+Formatting.h"

@implementation NSString (Formatting)

- (NSString *)ehi_repeat:(NSInteger)count
{
    return [@"" stringByPaddingToLength:count withString:self startingAtIndex:0];
}

- (NSString *)ehi_securedText:(BOOL)showLast
{
    NSInteger maskLast = showLast ? 1 : 0;
    return [self ehi_maskLast:maskLast mask:@"•"];
}

- (NSString *)ehi_maskLast:(NSInteger)n
{
    return [self ehi_maskLast:n mask:@"*"];
}

- (NSString *)ehi_maskLast:(NSInteger)n mask:(NSString *)mask
{
    NSInteger maskLength = MAX(0, self.length - n);
    return [self stringByReplacingCharactersInRange:NSMakeRange(0, maskLength) withString:[mask ehi_repeat:maskLength]];
}

- (NSString *)ehi_split:(NSUInteger)size separator:(NSString *)separator
{
    if(!separator) {
        separator = @" ";
    }
    
    NSMutableArray *chunks = [NSMutableArray new];
    
    for(int i=0 ; i < self.length ; i+=size) {
        NSUInteger length = MIN(self.length - i, size);
        NSString *chunk = [self substringWithRange:NSMakeRange(i, length)];
        
        [chunks addObject:chunk];
    }
    
    return [chunks componentsJoinedByString:separator];
}

- (NSString *)ehi_stripNonDecimalCharacters
{
    NSCharacterSet *nonDecimalSet = [[NSCharacterSet decimalDigitCharacterSet] invertedSet];
    return [[self componentsSeparatedByCharactersInSet:nonDecimalSet] componentsJoinedByString:@""];
}

- (NSString *)ehi_last:(NSInteger)count
{
    NSInteger index = MIN(self.length, self.length - count);
    return [self substringFromIndex:index];
}

- (NSString *)ehi_first:(NSInteger)count
{
    NSInteger index = MIN(self.length, MAX(0, count));
    return [self substringToIndex:index];
}

- (NSString *)ehi_trim
{
    return [self stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
}

- (NSString *)ehi_applyReplacementMap:(NSDictionary *)map
{
    NSString *result = self;
   
    for(NSString *key in map) {
        // wrap the key in the delimiter
        NSString *token = [NSString stringWithFormat:@"#{%@}", key];
        // stringify the corresponding value
        NSString *value = [map[key] description];
        // and update the string
        result = [result stringByReplacingOccurrencesOfString:token withString:value];
    }
    
    return result;
}

- (NSString *)ehi_appendComponent:(NSString *)component
{
    return [self ehi_appendComponent:component joinedBy:nil];
}

- (NSString *)ehi_appendComponent:(NSString *)component joinedBy:(NSString *)joiner
{
    // if we don't have a component, just return this string
    if(!component.length) {
        return self;
    }
    // otherwise, if we have a joiner and a component append both
    else if(joiner.length) {
        return [self stringByAppendingFormat:@"%@%@", component, joiner];
    }
    // finally, if joiner is zero-lengh, just append the component
    else {
        return [self stringByAppendingString:component];
    }
}

@end
