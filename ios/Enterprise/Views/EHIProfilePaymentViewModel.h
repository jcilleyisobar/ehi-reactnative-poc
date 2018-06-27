//
//  EHIProfilePaymentViewModel.h
//  Enterprise
//
//  Created by fhu on 5/19/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIProfilePaymentStatusViewModel.h"

typedef NS_ENUM(NSInteger, EHIProfilePaymentSection) {
    EHIProfilePaymentSectionMethods,
    EHIProfilePaymentSectionStatus
};

@interface EHIProfilePaymentViewModel : EHIViewModel <MTRReactive>
@property (strong, nonatomic) NSArray *paymentMethodsModel;
@property (strong, nonatomic) EHIProfilePaymentStatusViewModel *statusModel;
@end
