//
//  EHIEnrollmentConfirmationViewModel.h
//  Enterprise
//
//  Created by Rafael Machado on 8/11/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIEnrollmentStepViewModel.h"
#import "EHIBenefitsViewModel.h"

@interface EHIEnrollmentConfirmationViewModel : EHIEnrollmentStepViewModel <MTRReactive>

+ (instancetype)initWithUsername:(NSString *)username password:(NSString *)password;

@property (strong, nonatomic) EHIBenefitsViewModel *benefitsViewModel;
@property (copy  , nonatomic, readonly) NSString *intro;
@property (copy  , nonatomic, readonly) NSString *bulletOne;
@property (copy  , nonatomic, readonly) NSString *bulletTwo;
@property (copy  , nonatomic, readonly) NSString *bulletThree;
@property (copy  , nonatomic, readonly) NSString *bulletFour;
@property (copy  , nonatomic, readonly) NSString *learnMore;
@property (copy  , nonatomic, readonly) NSString *learnMoreButton;
@property (copy  , nonatomic, readonly) NSString *continueTitle;
@property (assign, nonatomic) NSInteger stackPop;

// auto login
@property (copy  , nonatomic, readonly) NSString *username;
@property (copy  , nonatomic, readonly) NSString *password;

- (void)showBenefits;
- (void)close;

@end
