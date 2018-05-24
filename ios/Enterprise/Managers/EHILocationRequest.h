//
//  EHILocationRequest.h
//  Enterprise
//
//  Created by Ty Cobb on 4/15/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import <CoreLocation/CoreLocation.h>

typedef void(^EHILocationRequestAvailabilityHandler)(BOOL, NSError *);
typedef void(^EHILocationRequestLocationHandler)(CLLocation *, NSError *);

@interface EHILocationRequest : NSObject

@property (copy  , nonatomic, readonly) id identifier;
@property (copy  , nonatomic, readonly) id handler;
@property (assign, nonatomic, readonly) BOOL isAvailabilityRequest;

- (instancetype)initWithAvailabilityFlag:(BOOL)isAvailabilityRequest handler:(id)handler;

@end
