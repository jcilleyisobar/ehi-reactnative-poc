//
//  EHICarClassPaymentLineItem.h
//  Enterprise
//
//  Created by mplace on 2/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIPrice.h"
#import "EHIPriceRateType.h"
#import "EHIReservationLineItem.h"

@class EHICarClass;
@class EHICarClassExtra;
@class EHICarClassMileage;

typedef NS_ENUM(NSUInteger, EHICarClassPriceLineItemStatus) {
    EHICarClassPriceLineItemStatusNone,
    EHICarClassPriceLineItemStatusIncluded,
    EHICarClassPriceLineItemStatusCharged,
    EHICarClassPriceLineItemStatusWaived,
};

@interface EHICarClassPriceLineItem : EHIModel <EHIReservationLineItemRenderable>

@property (assign, nonatomic, readonly) EHIReservationLineItemType type;
@property (assign, nonatomic, readonly) EHICarClassPriceLineItemStatus status;
@property (assign, nonatomic, readonly) EHIPriceRateType rateType;
@property (copy  , nonatomic, readonly) NSString *code;

@property (copy  , nonatomic, readonly) NSString *title;
@property (strong, nonatomic, readonly) EHIPrice *total;
@property (strong, nonatomic, readonly) EHIPrice *rate;
@property (assign, nonatomic, readonly) NSInteger duration;

// computed properties
@property (copy  , nonatomic, readonly) NSString *formattedTitle;
@property (copy  , nonatomic, readonly) NSString *formattedSubtitle;
@property (copy  , nonatomic, readonly) NSString *formattedTotal;
@property (assign, nonatomic, readonly) BOOL hasDetails;
@property (assign, nonatomic, readonly) BOOL isIncluded;
@property (assign, nonatomic, readonly) BOOL isWaived;
@property (assign, nonatomic, readonly) BOOL isCharged;
@property (assign, nonatomic, readonly) NSInteger quantity;

// if this is set, we show the set string. otherwise, this is computed
@property (copy, nonatomic) NSString *formattedRate;

// grafted-on properties; don't access the members of these objects, use computed properties!
@property (weak, nonatomic, readonly) EHICarClassExtra *extra;

// linking 
- (void)linkExtra:(EHICarClassExtra *)extra;
- (void)linkCarClass:(EHICarClass *)carClass;
@end

@interface EHICarClassPriceLineItem (Generators)
+ (instancetype)lineItemForExtra:(EHICarClassExtra *)extra type:(EHIReservationLineItemType)type;
+ (instancetype)lineItemForMileage:(EHICarClassMileage *)mileage;
@end

EHIAnnotatable(EHICarClassPriceLineItem);
