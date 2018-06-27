//
//  EHIDeliveryLocation.h
//  Enterprise
//
//  Created by Alex Koller on 6/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHIAddress.h"
#import "EHIPhone.h"

@interface EHIDeliveryCollectionInfo : EHIModel

@property (strong, nonatomic) EHIAddress *address;
@property (strong, nonatomic) EHIPhone *phone;
@property (copy  , nonatomic) NSString *comments;

@end
