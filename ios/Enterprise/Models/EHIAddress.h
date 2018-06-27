//
//  EHIAddress.h
//  Enterprise
//
//  Created by fhu on 5/19/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHICountry.h"

@interface EHIAddress : EHIModel

@property (copy, nonatomic, readonly) NSArray *addressLines;
@property (copy, nonatomic, readonly) NSString *city;
@property (copy, nonatomic, readonly) NSString *subdivisionCode;
@property (copy, nonatomic, readonly) NSString *subdivisionName;
@property (copy, nonatomic, readonly) NSString *countryCode;
@property (copy, nonatomic, readonly) NSString *countryName;
@property (copy, nonatomic, readonly) NSString *postalCode;
@property (copy, nonatomic, readonly) NSString *addressType;

// computed properties
@property (nonatomic, readonly) NSString *formattedAddress;
@property (nonatomic, readonly) EHICountry *country;

/** generates the formatted address; if @c forceLinebreak, then a newline is placed between the address and city */
- (NSString *)formattedAddress:(BOOL)forceLinebreak;

@end