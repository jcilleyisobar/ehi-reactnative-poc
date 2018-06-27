//
//  EHIPaymentViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 1/13/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReservationStepViewModel.h"
#import "EHIPaymentInputViewModel.h"
#import "EHIDriverInfo.h"

typedef NS_ENUM(NSInteger, EHIPaymentViewStyle) {
    EHIPaymentViewStyleReservation,
    EHIPaymentViewStyleProfile,
    EHIPaymentViewStyleSelectPayment
};

typedef void (^EHIPaymentViewModelHandler)(NSString *, BOOL);

@class EHICreditCard;
@interface EHIPaymentViewModel : EHIReservationStepViewModel <MTRReactive>

@property (copy  , nonatomic) EHIPaymentViewModelHandler handler;
@property (assign, nonatomic) EHIPaymentViewStyle style;
@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) NSString *scanTitle;
@property (copy  , nonatomic, readonly) NSString *addTitle;

@property (strong, nonatomic, readonly) EHIPaymentInputViewModel *paymentInputViewModel;
@property (strong, nonatomic, readonly) EHIDriverInfo *driverInfo;

@property (assign, nonatomic, readonly) BOOL isLoading;
@property (assign, nonatomic, readonly) BOOL missingField;

- (void)scanCard;
- (void)addCard;

@end
