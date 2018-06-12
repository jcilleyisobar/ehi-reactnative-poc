//
//  EHINetworkEncodable.h
//  Enterprise
//
//  Created by Ty Cobb on 1/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHINetworkRequest.h"

@protocol EHINetworkEncodable <NSObject>

/**
 @brief Provides a mechanism for objects to encode their data into a network request.
 
 Implementers should serialize themselves into the parameterized request using key-value 
 subscripting. The subscripting is nil-safe, and passing nil as the value will clear
 any existing value.
 
 Encodable objects or collections of encodable objects will be automatically encoded
 as a child of the currently encoding object.
 
 @param request The request to encode data for
*/

- (void)encodeWithRequest:(EHINetworkRequest *)request;

@end

// encoding conformance for dictionary types
@interface NSDictionary (EHINetworkEncodable) <EHINetworkEncodable> @end
