//
//  EHIPaymentOptionModalViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 2/18/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIInfoModalViewModel.h"

@interface EHIPaymentOptionModalViewModel : EHIInfoModalViewModel <MTRReactive>
@property (copy, nonatomic, readonly) NSString *prepayTitle;
@property (copy, nonatomic, readonly) NSString *prepayDetails;
@property (copy, nonatomic, readonly) NSString *payLaterTitle;
@property (copy, nonatomic, readonly) NSString *payLaterDetails;
@property (copy, nonatomic, readonly) NSAttributedString *prepayPolicy;
@end
