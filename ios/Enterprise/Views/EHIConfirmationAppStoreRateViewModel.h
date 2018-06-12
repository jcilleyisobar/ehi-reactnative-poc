//
//  EHIConfirmationAppStoreRateViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 6/16/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIConfirmationAppStoreRateViewModel : EHIViewModel <MTRReactive>
@property (copy, nonatomic, readonly) NSString *title;
@property (copy, nonatomic, readonly) NSString *subtitle;
@property (copy, nonatomic, readonly) NSString *rateButtonTile;
@property (copy, nonatomic, readonly) NSString *dismissButtontitle;
@end
