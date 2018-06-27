//
//  EHIReservationCarClassUpgradeViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 11/19/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIReservationCarClassUpgradeViewModel : EHIViewModel <MTRReactive>

/** Vehicle image model for the image view */
@property (copy, nonatomic, readonly) EHIImage *vehicleImage;
/** Title describing details of the upgrade */
@property (copy, nonatomic, readonly) NSAttributedString *detailsTitle;
/** Title for button to initiate an upgrade */
@property (copy, nonatomic, readonly) NSString *buttonTitle;

@end
