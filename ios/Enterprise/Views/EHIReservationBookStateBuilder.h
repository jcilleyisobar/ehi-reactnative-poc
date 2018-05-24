//
//  EHIReservationBookStateBuilder.h
//  Enterprise
//
//  Created by Rafael Ramos on 22/03/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIPriceContext.h"

typedef NS_ENUM(NSInteger, EHIReservationBookStateTitleFlow) {
    EHIReservationBookStateTitleFlowAddPayment,
    EHIReservationBookStateTitleFlowDefault,
    EHIReservationBookStateTitleFlowModify
};

@class EHIUserPaymentMethod;
@class EHICarClassPriceDifference;
@interface EHIReservationBookStateBuilder : NSObject

@property (assign, nonatomic, readonly) EHIReservationBookStateTitleFlow titleState;
@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) NSString *subtitle;

- (EHIReservationBookStateBuilder *(^)(BOOL))modify;
- (EHIReservationBookStateBuilder *(^)(BOOL))prepay;
- (EHIReservationBookStateBuilder *(^)(BOOL))collectsNewCreditCard;
- (EHIReservationBookStateBuilder *(^)(BOOL))businessTrip;
- (EHIReservationBookStateBuilder *(^)(BOOL))addedCreditCard;
- (EHIReservationBookStateBuilder *(^)(NSString *))discount;
- (EHIReservationBookStateBuilder *(^)(id<EHIPriceContext>))currencyConversion;
- (EHIReservationBookStateBuilder *(^)(EHICarClassPriceDifference *))priceDifference;
- (EHIReservationBookStateBuilder *(^)(EHIUserPaymentMethod *))paymentMethod;

@end
