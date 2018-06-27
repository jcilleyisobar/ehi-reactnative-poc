//
//  EHIPaymentOptionCellViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 1/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIReservationBuilderFlow.h"
#import "EHIReservationStepViewModel.h"

typedef NS_ENUM(NSInteger, EHIPaymentOptionLayout) {
    EHIPaymentOptionLayoutEnabled,
    EHIPaymentOptionLayoutDisabled,
};

@interface EHIPaymentOptionCellViewModel : EHIReservationStepViewModel <MTRReactive>
@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) NSAttributedString *subtitle;
@property (copy  , nonatomic, readonly) NSAttributedString *price;
@property (copy  , nonatomic, readonly) NSString *discount;
@property (assign, nonatomic, readonly) EHIReservationPaymentOption paymentOption;
@property (assign, nonatomic, readonly) EHIPaymentOptionLayout layoutType;

- (void)configureWithPaymentOption:(EHIReservationPaymentOption)paymentOption carClass:(EHICarClass *)carClass;

@end
