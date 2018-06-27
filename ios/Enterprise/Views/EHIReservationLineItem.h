//
//  EHIReservationLineItem.h
//  Enterprise
//
//  Created by fhu on 7/17/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

typedef NS_ENUM(NSUInteger, EHIReservationLineItemType) {
    EHIReservationLineItemTypeUnknown,
    EHIReservationLineItemTypeRedemption,
    EHIReservationLineItemTypeVehicleRate,
    EHIReservationLineItemTypeEquipment,
    EHIReservationLineItemTypeCoverage,
    EHIReservationLineItemTypeSavings,
    EHIReservationLineItemTypeFee,
    EHIReservationLineItemTypeFeeSummary,
    EHIReservationLineItemTypeFeeMileage,
    EHIReservationLineItemTypeTerms
};

@protocol EHIReservationLineItemRenderable <NSObject>

@required
- (EHIReservationLineItemType)type;
- (NSString *)formattedTitle;
- (NSString *)formattedRate;
- (NSString *)formattedTotal;
- (EHIPrice *)viewPrice;
- (BOOL)hasDetails;
- (NSInteger)quantity;

@optional
- (void(^)())action;
- (NSString *)longDetails;
- (NSString *)formattedSubtitle;
// invoice
- (NSString *)formattedType;
- (NSString *)formattedRateTotal;
- (BOOL)isLearnMore;

@end

@interface EHIReservationLineItem : NSObject <EHIReservationLineItemRenderable>
+ (instancetype)lineItemForLearnMoreButton;
@end
