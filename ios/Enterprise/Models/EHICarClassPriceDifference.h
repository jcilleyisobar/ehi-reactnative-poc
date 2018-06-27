//
//  EHICarClassPriceDifference.h
//  Enterprise
//
//  Created by Alex Koller on 11/20/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHIPrice.h"

typedef NS_ENUM(NSUInteger, EHICarClassPriceDifferenceType) {
    EHICarClassPriceDifferenceTypeUnknown,
    EHICarClassPriceDifferenceTypeUpgrade,
    EHICarClassPriceDifferenceTypeUpgradePrepay,
    EHICarClassPriceDifferenceTypeContract,
    EHICarClassPriceDifferenceTypePrepay,
    EHICarClassPriceDifferenceTypeUnpaidRefundAmount
};

@interface EHICarClassPriceDifference : EHIModel <EHIPriceContext>
@property (assign, nonatomic, readonly) EHICarClassPriceDifferenceType type;
@property (copy  , nonatomic, readonly) NSString *selectedCarClass;
@property (strong, nonatomic, readonly) EHIPrice *paymentDifference;
@property (strong, nonatomic, readonly) EHIPrice *viewDifference;
@end

EHIAnnotatable(EHICarClassPriceDifference)
