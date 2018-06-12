//
//  EHIPaymentOptionViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 1/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHISectionHeaderModel.h"
#import "EHIReservationStepViewModel.h"
#import "EHICarClassViewModel.h"

typedef NS_ENUM(NSUInteger, EHIPaymentOptionSection) {
    EHIPaymentOptionSectionRedemptionPoints,
    EHIPaymentOptionSectionCarClass,
    EHIPaymentOptionSectionPlacard,
    EHIPaymentOptionSectionPrepayBanner,
    EHIPaymentOptionSectionPaymentOptions,
    EHIPaymentOptionSectionFooter,
};

@class EHIPlacardViewModel;
@class EHICarClassViewModel;
@interface EHIPaymentOptionViewModel : EHIReservationStepViewModel <MTRReactive>
@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) EHIPlacardViewModel *placardModel;
@property (strong, nonatomic, readonly) EHICarClassViewModel *carClassModel;
@property (strong, nonatomic, readonly) NSArray *paymentOptionModels;
@property (strong, nonatomic, readonly) EHIModel *prepayBannerModel;
@property (strong, nonatomic, readonly) EHIModel *footerModel;
@property (assign, nonatomic, readonly) BOOL shouldAnimate;

- (void)selectItemAtIndex:(NSInteger)index;
- (void)presentModalPaymentInformation;

@end
