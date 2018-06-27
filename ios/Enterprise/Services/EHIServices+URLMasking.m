//
//  EHIServices+URLMasking.m
//  Enterprise
//
//  Created by Rafael Ramos on 24/07/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIServices+URLMasking.h"
#import "EHIUser.h"

@implementation EHIServices (URLMasking)

- (NSString *)maskURL:(NSURL *)url
{
    NSString *absoluteString = url.absoluteString;
    absoluteString = [self skipIfSolr:absoluteString];
    if(absoluteString != nil) {
        absoluteString = [self maskLoyalty:absoluteString];
        absoluteString = [self maskReservation:absoluteString];
        absoluteString = [self maskLocation:absoluteString];
        absoluteString = [self maskContract:absoluteString];
        absoluteString = [self maskPayment:absoluteString];
    }
    
    return absoluteString;
}

- (NSString *)maskLoyalty:(NSString *)input
{
    NSString *loyalty = [EHIUser currentUser].loyaltyNumber;
    if(loyalty){
        return [input stringByReplacingOccurrencesOfString:loyalty withString:@"<loyaltyId>"];
    } else {
        return input;
    }
}

- (NSString *)maskReservation:(NSString *)input
{
    BOOL matches = [input matchesRegex:@"reservations?"];
    if(matches) {
        return [[input
                 stringByReplacingMatchesForRegex:@"\\/[0-9]+\\/?" withTemplate:@"/<resId>/"]
                 stringByReplacingMatchesForRegex:@"\\/$" withTemplate:@""];
    }
    
    return input;
}

- (NSString *)maskLocation:(NSString *)input
{
    BOOL matches = [input matchesRegex:@"\\/locations?"];
    if(matches) {
        return [[input
                 stringByReplacingMatchesForRegex:@"\\/[0-9]+\\/?" withTemplate:@"/<locationId>/"]
                stringByReplacingMatchesForRegex:@"\\/$" withTemplate:@""];
    }
    
    return input;
}

- (NSString *)maskContract:(NSString *)input
{
    return [input stringByReplacingMatchesForRegex:@"\\/contracts\\/(\\w|\\d)+" withTemplate:@"/contracts/<contractId>"];
}

- (NSString *)maskPayment:(NSString *)input
{
    return [input stringByReplacingMatchesForRegex:@"\\/payment\\/(\\w|\\d)+" withTemplate:@"/payment/<paymentId>"];
}

- (NSString *)skipIfSolr:(NSString *)input
{
    return [input containsString:@".location."] ? nil : input;
}

@end
