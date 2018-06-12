//
//  NSString+Matching.m
//  Enterprise
//
//  Created by Ty Cobb on 1/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "NSString+Matching.h"

#define RGXStringRange ((NSRange){ .length = self.length })

NSString * const EHIMaskString = @"\u2022";
NSString * const EHISectionSignString = @"\u00A7";
@implementation NSString (Matching)

# pragma mark - Matching

- (NSString *)firstStringMatchingRegex:(NSString *)regex
{
    NSTextCheckingResult *result = [self firstTextCheckingResultForRegex:regex];
    return [self substringWithRange:result.range];
}

- (NSArray *)stringsMatchingRegex:(NSString *)regex
{
    NSArray *results = [self textCheckingResultsForRegex:regex];
  
    // gross manual mapping
    NSMutableArray *matches = [[NSMutableArray alloc] initWithCapacity:results.count];
    for(NSTextCheckingResult *result in results) {
        [matches addObject:[self substringWithRange:result.range]];
    }
    
    return matches;
}

- (NSArray *)textCheckingResultsForRegex:(NSString *)regex
{
    return [[self.class regularExpressionForRegex:regex] matchesInString:self options:0 range:RGXStringRange];
}

- (NSTextCheckingResult *)firstTextCheckingResultForRegex:(NSString *)regex
{
    return [[self.class regularExpressionForRegex:regex] firstMatchInString:self options:0 range:RGXStringRange];
}

- (BOOL)matchesRegex:(NSString *)regex
{
    return [self firstTextCheckingResultForRegex:regex] != nil;
}

- (BOOL)matchesCharacterSet:(NSCharacterSet *)characterSet
{
    return [self rangeOfCharacterFromSet:characterSet.invertedSet].location == NSNotFound;
}

- (BOOL)ehi_validEmail
{
    return [self matchesRegex:@".+@.+\\..+"];
}

- (BOOL)ehi_isPhoneNumber
{
    return [self matchesRegex:@"^\\D*(\\d\\D*){10}$"];
}

- (BOOL)ehi_isPossiblePhoneNumber
{
    return [self matchesRegex:@"^[\\s+\\d\\-]+$"];
}

- (BOOL)ehi_isMasked
{
    return [self containsString:EHIMaskString];
}

# pragma mark - Replacement

- (NSString *)stringByReplacingMatchesForRegex:(NSString *)regex withTemplate:(NSString *)replacement
{
    NSRegularExpression *regularExpression = [self.class regularExpressionForRegex:regex];
    return [regularExpression stringByReplacingMatchesInString:self options:0 range:RGXStringRange withTemplate:replacement];
}

# pragma mark - Equality

- (BOOL)ehi_isEqualToStringIgnoringCase:(NSString *)string
{
    return [self caseInsensitiveCompare:string] == NSOrderedSame;
}

# pragma mark - Caching

+ (NSRegularExpression *)regularExpressionForRegex:(NSString *)regex
{
    static NSCache *regularExpressions;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        regularExpressions = [NSCache new];
    });
    
    NSRegularExpression *regularExpression = [regularExpressions objectForKey:regex];
    if(regularExpression) {
        return regularExpression;
    }
    
    NSError *expressionError;
    regularExpression = [[NSRegularExpression alloc] initWithPattern:regex options:0 error:&expressionError];
    
    NSAssert(!expressionError, @"regex %@ formatted incorrectly.", regex);
    [regularExpressions setObject:regularExpression forKey:regex];
    
    return regularExpression;
}

@end
