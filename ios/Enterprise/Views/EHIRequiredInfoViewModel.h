//
//  EHIRequiredInfoViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 02/05/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

typedef NS_ENUM(NSInteger, EHIRequiredInfoType) {
    EHIRequiredInfoTypeForgotPassword,
    EHIRequiredInfoTypeProfile,
    EHIRequiredInfoTypeLookupRental,
    EHIRequiredInfoTypeEnroll,
    EHIRequiredInfoTypeReservation,
};

@interface EHIRequiredInfoViewModel : EHIViewModel <MTRReactive>

+ (instancetype)modelForInfoType:(EHIRequiredInfoType)type;

@property (copy  , nonatomic, readonly) NSString *title;
@property (assign, nonatomic, readonly) EHIRequiredInfoType type;
@end
