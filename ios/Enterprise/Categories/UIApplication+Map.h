//
//  UIApplication+Map.h
//  Enterprise
//
//  Created by mplace on 6/17/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "EHIThirdPartyMap.h"

@interface UIApplication (Map)

/**
 @brief Prompts the user to either open maps or copy address to pasteboard
 
 The @c location is wrapped in a MKMapItem for opening in maps
 and the @c location's formattedAddress property is copied to the pasteboard.
 If @c location is @c nil, nothing happens.
 */

+ (void)ehi_promptDirectionsForLocation:(nullable EHILocation *)location;

/**
 @brief Opens up the map application for @c location without prompting
 
 The @c location is wrapped in a MKMapItem for opening in maps. @c location
 displayName and coordinate are used to view the location in maps.
 */

+ (void)ehi_openMapsWithLocation:(nullable EHILocation *)location;


/**
 @brief Opens up the map application on a third party application for @c location without prompting
 
 From the @c location all of the necessary properties are pulled off, in order to assemble a NSURL that the target application, @c thirdPartMap, supports.
 */
+ (void)ehi_openThirdPartyMap:(nullable id<EHIThirdPartyMapProtocol>)thirdPartMap withLocation:(nullable EHILocation *)location;

@end
