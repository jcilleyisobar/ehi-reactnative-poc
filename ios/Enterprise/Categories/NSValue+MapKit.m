//
//  NSValue+MapKit.m
//  Enterprise
//
//  Created by Ty Cobb on 1/30/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "NSValue+MapKit.h"

#define EHICreateValueMethodName(_type) + (NSValue *)valueWith ## _type :(_type)parameter
#define EHICreateValueMethod(_type) EHICreateValueMethodName(_type) { \
    return [NSValue valueWithBytes:&parameter objCType:@encode(_type)]; \
}

#define EHIGetValueMethodName(_type) - (_type) _type ## Value
#define EHIGetValueMethod(_type) EHIGetValueMethodName(_type) { \
    _type value;            \
    [self getValue:&value]; \
    return value;           \
}

#define EHIValueMethodsForType(_type) EHICreateValueMethod(_type) EHIGetValueMethod(_type)

@implementation NSValue (Structs)

EHIValueMethodsForType(MKCoordinateRegion)

@end
