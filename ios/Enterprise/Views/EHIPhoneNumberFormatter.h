//
//  EHIPhoneNumberFormatter.h
//  Enterprise
//
//  Created by Ty Cobb on 4/15/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocation.h"
#import "EHIFormattedPhone.h"

@interface EHIPhoneNumberFormatter : NSObject

/**
 @brief Formats and localizes the source phone number
 
 The country code should correspond to the country of the source phone number, i.e. 
 if @c source is a UK number, the @c countryCode should be @c UK. The output will
 be formatted according to the user's regional setting.

 @param source      The phone number string to format
 @param countryCode The country code of the phone number
*/

+ (NSString *)format:(NSString *)source countryCode:(NSString *)countryCode;

/**
 @brief  Formats the source phone number using the user's locale
 
 If no formatting is applied, then this method will populate the @c error parameter.
 
 @param input The phone number string to format
 @param error An error, if any, formatting the phone number
 
 @return The formatted phone number
*/

+ (NSString *)formatUserInput:(NSString *)input error:(NSError **)error;

/**
 @brief Formats the source phone number using the user's locale
 
 @param input The phone number string to format
 
 @return A model to be used in conjunction with EHIPhoneTextField
*/

+ (EHIFormattedPhone *)format:(NSString *)input;

@end
