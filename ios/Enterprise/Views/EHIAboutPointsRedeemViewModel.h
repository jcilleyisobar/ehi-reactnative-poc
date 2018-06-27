//
//  EHIAboutPointsRedeemViewModel.h
//  Enterprise
//
//  Created by frhoads on 1/12/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIAboutPointsRedeemViewModel : EHIViewModel
@property (strong, nonatomic, readonly) NSString *titleText;
@property (strong, nonatomic, readonly) NSString *subtitleText;
@property (strong, nonatomic, readonly) NSString *buttonText;

- (void)showStartReservation;

@end
