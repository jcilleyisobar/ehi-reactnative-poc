//
//  EHIPinAuthenticationViewModel.h
//  Enterprise
//
//  Created by cgross on 4/27/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReservationStepViewModel.h"
#import "EHIRequiredInfoViewModel.h"

NS_ASSUME_NONNULL_BEGIN

@class EHIFormFieldViewModel;
@interface EHIPinAuthenticationViewModel : EHIReservationStepViewModel <MTRReactive>

@property (copy, nonatomic, readonly) NSString *title;
@property (copy, nonatomic, readonly) NSString *instructionsTitle;
@property (copy, nonatomic, readonly) NSString *submitTitle;
@property (strong, nonatomic) EHIFormFieldViewModel *formModel;
@property (assign, nonatomic) BOOL isLoading;
@property (assign, nonatomic) BOOL isReadyToSubmit;
@property (strong, nonatomic) EHIRequiredInfoViewModel *requiredInfoViewModel;

@property (copy  , nonatomic, nullable) void (^handler)(BOOL submitted, EHIServicesError * __nullable error);

- (void)submit;
- (void)close;

@end
NS_ASSUME_NONNULL_END
