//
//  EHIConfirmationHeaderViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 4/21/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIImage.h"

@interface EHIConfirmationHeaderViewModel : EHIViewModel <MTRReactive>
@property (copy, nonatomic, readonly) NSAttributedString *headerTitle;
@property (copy, nonatomic, readonly) NSString *confirmationTitle;
@property (copy, nonatomic) NSString *confirmationNumber;
@property (copy, nonatomic) NSAttributedString *emailTitle;
@property (copy, nonatomic) EHIImage *vehicleImage;

@end
