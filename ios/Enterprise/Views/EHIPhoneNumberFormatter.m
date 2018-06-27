//
//  EHIPhoneNumberFormatter.m
//  Enterprise
//
//  Created by Ty Cobb on 4/15/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import <libPhoneNumber-iOS/NBPhoneNumberUtil.h>
#import <libPhoneNumber-iOS/NBPhoneNumber.h>
#import <libPhoneNumber-iOS/NBAsYouTypeFormatter.h>
#import "EHIPhoneNumberFormatter.h"

@implementation EHIPhoneNumberFormatter

+ (NSString *)format:(NSString *)source countryCode:(NSString *)countryCode
{
    if(!source) {
        return nil;
    }
    if(!countryCode) {
        return source;
    }
    
    NSError *error;
    
    // generate a phone number object in our current locale
    NBPhoneNumberUtil *formatter = [NBPhoneNumberUtil new];
    NBPhoneNumber *phoneNumber = [formatter parse:source defaultRegion:countryCode error:&error];
   
    NSString *result = source;
    if(!error) {
        NSString *formatted = [formatter formatNumberForMobileDialing:phoneNumber regionCallingFrom:[NSLocale ehi_region] withFormatting:YES error:&error];
        if(!error && formatted) {
            result = formatted;
        }
    }
    
    // in case the library fail to parse the phone number, return it as it is
    BOOL isValidResult = result && result.length > 0;
    result = isValidResult ? result : source;
    
    return result;
}

+ (NSString *)formatUserInput:(NSString *)input error:(NSError **)error
{
    NBAsYouTypeFormatter *formatter = self.formatter;

    __autoreleasing NSError *placeholder = nil;
    if(error == NULL) {
        error = &placeholder;
    }
    
    NSString *sanitizedInput = [input ehi_stripNonDecimalCharacters];
    NSString *result = [formatter inputString:sanitizedInput];
    
    if(!formatter.isSuccessfulFormatting && [sanitizedInput isEqualToString:result]) {
        *error = [NSError errorWithDomain:EHIErrorDomainGeneral code:-1 userInfo:nil];
    }
    
    return *error ? input : result;
}

+ (EHIFormattedPhone *)format:(NSString *)input
{
    EHIFormattedPhone *model = [EHIFormattedPhone modelWithPhone:input];
    
    NSError *error = nil;
    NSString *result = [self formatUserInput:input error:&error];
    
    model.formattedPhone = result;
    model.error = error;
    
    return model;
}

# pragma mark - Accessors

+ (NBAsYouTypeFormatter *)formatter
{
    static NBAsYouTypeFormatter *formatter;
    static NSString *formatterRegion;
    
    // re-initialize the formatter if the users region has changed or if we haven't done so yet
    if(!formatter || ![formatterRegion isEqualToString:[NSLocale ehi_region]]) {
        formatterRegion = [NSLocale ehi_region];
        formatter = [[NBAsYouTypeFormatter alloc] initWithRegionCode:formatterRegion];
    }

    return formatter;
}

@end
