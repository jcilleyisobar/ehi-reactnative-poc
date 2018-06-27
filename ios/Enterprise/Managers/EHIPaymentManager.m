//
//  EHIPaymentManager.m
//  Enterprise
//
//  Created by Rafael Ramos on 26/07/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIPaymentManager.h"
#import <PassKit/PassKit.h>

@implementation EHIPaymentManager

+ (BOOL)canPayWithApplePay
{
    return [PKPaymentAuthorizationViewController canMakePayments];
}

@end
