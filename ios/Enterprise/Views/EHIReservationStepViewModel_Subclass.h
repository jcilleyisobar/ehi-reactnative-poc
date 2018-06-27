//
//  EHIReservationStepViewModel_Subclass.h
//  Enterprise
//
//  Created by Alex Koller on 10/6/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIReservationStepViewModel.h"
#import "EHIReservationBuilder.h"

@interface EHIReservationStepViewModel (Subclass)
/** Passthrough method for easy access to the reservation builder */
- (EHIReservationBuilder *)builder;
@end
