//
//  UIApplication+URL.h
//  Enterprise
//
//  Created by mplace on 5/8/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocation.h"

NS_ASSUME_NONNULL_BEGIN

@interface UIApplication (URL)

/**
 @brief Prompts the user to call the @c phoneNumber
 
 The @c phoneNumber is santized first so that it is properly formatted for calling. If
 @c phoneNumber is @c nil, nothing happens.
*/

+ (void)ehi_promptPhoneCall:(nullable NSString *)phoneNumber;

/**
 @brief Opens the native maps application centered at the locations coordinates and a search is performed using the query string
 
 The @c query string can be any text that would normally be typed into the search bar of the native maps app
 The @c location is used to center the map at its coordinates
*/

+ (void)ehi_openMapsWithSearchQuery:(nonnull NSString *)query atLocation:(nonnull EHILocation *)location;

/**
 @brief Prompt the user to open @url in their web browser
 
 @param url The url to open
*/
+ (void)ehi_promptUrl:(NSString *)url;

+ (void)ehi_openURL:(NSURL *)url;

@end

NS_ASSUME_NONNULL_END
