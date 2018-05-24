//
//  EHIReservationCarClassViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 4/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationCarClassViewModel.h"
#import "EHICarClass.h"

@interface EHIReservationCarClassViewModel ()
@property (strong, nonatomic) EHICarClass *carClass;
@end

@implementation EHIReservationCarClassViewModel

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHICarClass class]]) {
        [self updateCarClass:model];
    }
}

- (void)updateCarClass:(EHICarClass *)carClass
{
    self.carClass = carClass;
    
    self.title = carClass.name;
    self.subtitle = carClass.makeModelOrSimilar;
    
    self.carImage = [carClass.images firstObject];
    
    self.transmissionType = carClass.transmission;
    self.isAutomaticTransmission = carClass.isAutomaticTransmission;
}

@end
