//
//  UIView+Unarchiving.h
//  Enterprise
//
//  Created by Ty Cobb on 1/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@interface UIView (Unarchiving)
+ (instancetype)ehi_instanceFromNib;
+ (instancetype)ehi_instanceFromNibWithName:(NSString *)name;
+ (instancetype)ehi_deviceSpecificInstanceFromNib;
@end

@interface UIStoryboard (Unarchiving)
+ (instancetype)ehi_deviceSpecificStoryboardWithName:(NSString *)name;
@end

/**
 Returns a string with the approrpriate device-specific suffix.
 @param  name The base, unsuffixed string
 @return A new string with the correct suffix appended
*/

extern NSString * UIDeviceifyName(NSString *name);

/**
 Returns a string with the apIprorpriate device-specific suffix.
 
 @param name  The base, unsuffixed string
 @param idiom The device idiom for to check against
 
 @return A new string with the correct suffix appended
*/

extern NSString * UIDeviceifyNameWithIdiom(NSString *name, UIUserInterfaceIdiom idiom);
