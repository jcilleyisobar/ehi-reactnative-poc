//
//  EHIMacros.h
//  Enterprise
//
//  Created by Ty Cobb on 1/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

//
// Environment
//

#ifdef DEBUG
#define NOT_RELEASE 1
#else
#define NOT_RELEASE 0
#endif

//
// Keypath Macros -- taken from libextobjc: https://github.com/jspahrsummers/libextobjc
//

// @key -- stringifies the last component of a method chain
//      @key(model.submodel.title) -> @"title"
#define key(_path) (((void)(NO && ((void)_path, NO)), strrchr(# _path, '.') + 1))

// @keypath -- stringifies the path of components from the root of a method chain
//      @keypath(model.submodel.title) -> @"submodel.title"
#define keypath(_path) (((void)(NO && ((void)_path, NO)), strchr(# _path, '.') + 1))

//
// Device Macros
//

#define UIDeviceIsTablet (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad)

//
// Control State
//

#define UIControlStateSelectedHighlighted (UIControlStateSelected | UIControlStateHighlighted)

//
// Interface Orientations
//

#define UIInterfaceOrientationsAreOrthoganal(_orientation1, _orientation2) (UIInterfaceOrientationIsLandscape(_orientation1) != UIInterfaceOrientationIsLandscape(_orientation2))
#define UIInterfaceOrientationMaskBothPortrait (UIInterfaceOrientationMaskPortrait | UIInterfaceOrientationMaskPortraitUpsideDown)

//
// Autoresizing
//

#define UIViewAutoresizingFill (UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight)

//
// Convenience Macros/functions
//

#define ehi_call(_block) if(_block) _block

//
// Stringify
//

#define NSStringFromProperty(_property) NSStringFromSelector(@selector(_property))

//
// Value Macros
//
// alternative syntax for boxing/unboxing values
//      NSValueBox(CGPoint, <variable>)
//      NSValueUnbox(CGPoint, <variable>)
//

#define NSValueBox(_type, _value) [NSValue valueWith ## _type:_value]
#define NSValueUnbox(_type, _value) [_value _type ## Value]

//
// Version Macros
//

#define SYSTEM_VERSION_COMPARE(v)                   ([[[UIDevice currentDevice] systemVersion] compare:@""#v options:NSNumericSearch])
#define SYSTEM_VERSION_EQUAL_TO(v)                  (SYSTEM_VERSION_COMPARE(v) == NSOrderedSame)
#define SYSTEM_VERSION_GREATER_THAN(v)              (SYSTEM_VERSION_COMPARE(v) == NSOrderedDescending)
#define SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(v)  (SYSTEM_VERSION_COMPARE(v) != NSOrderedAscending)
#define SYSTEM_VERSION_LESS_THAN(v)                 (SYSTEM_VERSION_COMPARE(v) == NSOrderedAscending)
#define SYSTEM_VERSION_LESS_THAN_OR_EQUAL_TO(v)     (SYSTEM_VERSION_COMPARE(v) != NSOrderedDescending)

//
// Device Macros
//

#ifdef DEBUG
#define IS_DEVICE ([[[UIDevice currentDevice] model] rangeOfString:@"Simulator" options:NSCaseInsensitiveSearch].location == NSNotFound)
#else
#define IS_DEVICE YES
#endif

#define DEVICE_ID ([[[UIDevice currentDevice] identifierForVendor]UUIDString])

//
// Warnings
//

#define IGNORE_PERFORM_SELECTOR_WARNING(_code) \
do { \
_Pragma("clang diagnostic push") \
_Pragma("clang diagnostic ignored \"-Warc-performSelector-leaks\"") \
_code; \
_Pragma("clang diagnostic pop") \
} while(0)

//
// Structs
//

#define EHI_STRUCT(_name) struct _name _name; struct _name
