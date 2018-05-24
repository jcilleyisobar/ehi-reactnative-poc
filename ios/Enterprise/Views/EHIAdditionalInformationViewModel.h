//
//  EHIAdditionalInformationViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 4/22/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReservationStepViewModel.h"
#import "EHIRequiredInfoViewModel.h"

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSInteger, EHIAdditionalInformationFlow) {
    EHIAdditionalInformationFlowDefault,
    EHIAdditionalInformationFlowReview,
};

@class EHIFormFieldViewModel;
@interface EHIAdditionalInformationViewModel : EHIReservationStepViewModel <MTRReactive>

- (instancetype)initWithFlow:(EHIAdditionalInformationFlow)flow;

@property (copy, nonatomic, readonly) NSString *title;
@property (copy, nonatomic, readonly) NSString *instructionsTitle;
@property (copy, nonatomic, readonly) NSString *submitTitle;
@property (strong, nonatomic) NSArray<EHIFormFieldViewModel *> *formModels;
@property (assign, nonatomic) BOOL isLoading;
@property (assign, nonatomic) BOOL isInvalid;
@property (assign, nonatomic) BOOL hideNavigation;
@property (assign, nonatomic) EHIAdditionalInformationFlow flow;
@property (strong, nonatomic) EHIRequiredInfoViewModel *requiredInfoModel;

@property (copy  , nonatomic, nullable) void (^handler)(BOOL submitted, EHIServicesError * __nullable error);

- (void)submit;
- (void)close;

@end

NS_ASSUME_NONNULL_END
