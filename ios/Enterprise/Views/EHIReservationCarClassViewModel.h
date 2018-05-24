//
//  EHIReservationCarClassViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 4/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIImage.h"

@interface EHIReservationCarClassViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic) NSString *title;
@property (copy  , nonatomic) NSString *subtitle;
@property (copy  , nonatomic) EHIImage *carImage;
@property (copy  , nonatomic) NSString *transmissionType;
@property (assign, nonatomic) BOOL isAutomaticTransmission;
@end
