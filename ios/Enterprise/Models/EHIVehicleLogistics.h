//
//  EHIVehicleLogistics.h
//  Enterprise
//
//  Created by Alex Koller on 6/11/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHIDeliveryCollectionInfo.h"

@interface EHIVehicleLogistics : EHIModel

@property (strong, nonatomic) EHIDeliveryCollectionInfo *deliveryInfo;
@property (strong, nonatomic) EHIDeliveryCollectionInfo *collectionInfo;

// computed
@property (assign, nonatomic, readonly) BOOL hasSameDeliveryAndCollection;

@end
