//
//  EHIReservationBuilderFlow.h
//  Enterprise
//
//  Created by mplace on 10/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

typedef NS_ENUM(NSInteger, EHIReservationBuilderFlow) {
    EHIReservationBuilderFlowDefault,
    EHIReservationBuilderFlowRestart,
    EHIReservationBuilderFlowModify,
    EHIReservationBuilderFlowLocationSearch
};

typedef NS_ENUM(NSUInteger, EHIReservationPaymentOption) {
    EHIReservationPaymentOptionUnknown,
    EHIReservationPaymentOptionPayNow,
    EHIReservationPaymentOptionPayLater,
    EHIReservationPaymentOptionRedeemPoints,
};
