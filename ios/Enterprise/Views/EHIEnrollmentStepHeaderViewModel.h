//
//  EHIEnrollmentStepHeaderViewModel.h
//  Enterprise
//
//  Created by Rafael Machado on 8/4/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

typedef NS_ENUM(NSInteger, EHIEnrollmentStep) {
    EHIEnrollmentStepZero,
    EHIEnrollmentStepOne,
    EHIEnrollmentStepTwo,
    EHIEnrollmentStepThree,
    EHIEnrollmentStepProfileFound,
    EHIEnrollmentStepProfileEmeraldClub
};

@interface EHIEnrollmentStepHeaderViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic) NSString *stepTitle;
@property (copy  , nonatomic) NSString *stepDetail;
@property (copy  , nonatomic) NSString *scanButtonTitle;
@property (assign, nonatomic) BOOL hideScan;
- (instancetype)initWithStep:(EHIEnrollmentStep)step;

- (void)scanLicense;

@end
