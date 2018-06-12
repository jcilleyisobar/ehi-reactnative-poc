//
//  EHIPlacardViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 8/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

typedef NS_ENUM(NSUInteger, EHIPlacardType) {
    EHIPlacardTypeExtras,
    EHIPlacardTypePayment,
    EHIPlacardTypePriceDetails
};

@interface EHIPlacardViewModel : EHIViewModel

@property (copy  , nonatomic) NSAttributedString *title;
@property (assign, nonatomic, readonly) BOOL hidesInfoIcon;
@property (assign, nonatomic, readonly) BOOL isPriceDetails;

- (instancetype)initWithType:(EHIPlacardType)type carClass:(EHICarClass *)carClass;

@end
