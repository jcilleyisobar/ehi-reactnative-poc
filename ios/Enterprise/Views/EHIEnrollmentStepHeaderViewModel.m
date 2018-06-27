//
//  EHIEnrollmentStepHeaderViewModel.m
//  Enterprise
//
//  Created by Rafael Machado on 8/4/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIEnrollmentStepHeaderViewModel.h"
#import "EHIViewModel_Subclass.h"

@interface EHIEnrollmentStepHeaderViewModel ()
@property (assign, nonatomic) EHIEnrollmentStep step;
@end

@implementation EHIEnrollmentStepHeaderViewModel

- (instancetype)initWithStep:(EHIEnrollmentStep)step
{
    if(self = [super init]) {
        self.step = step;
    }
    
    return self;
}

- (NSString *)stepTitle
{
    NSInteger stepNumber = self.step;
    switch (self.step) {
        case EHIEnrollmentStepProfileEmeraldClub:
        case EHIEnrollmentStepProfileFound: {
            stepNumber = EHIEnrollmentStepTwo;
            break;
        }
        default: break;
    }
    
    NSString *title = EHILocalizedString(@"enroll_license_info_step", @"Step #{step} of #{step_count}", @"");
    return [title ehi_applyReplacementMap:@{
        @"step" : @(stepNumber).description,
        @"step_count" : @(EHIEnrollmentStepThree).description
    }];
}

- (NSString *)stepDetail
{
    switch (self.step) {
        case EHIEnrollmentStepOne:
            return EHILocalizedString(@"enroll_license_info_header", @"We'll first need your license info.", @"");
        case EHIEnrollmentStepTwo:
            return EHILocalizedString(@"enroll_step_2_new_user_header", @"Lets get your address.", @"");
        case EHIEnrollmentStepThree:
            return EHILocalizedString(@"enroll_third_step_title", @"Let's get your phone, email and create a password.", @"");
        case EHIEnrollmentStepProfileFound:
            return EHILocalizedString(@"enroll_driver_profile_found_title", @"We found you! Looks like you've rented from us before.", @"");
        case EHIEnrollmentStepProfileEmeraldClub:
            return EHILocalizedString(@"enroll_driver_profile_found_title", @"We found you! Looks like you've rented from us before.", @"");
//            return EHILocalizedString(@"enroll_nalmo_found_title", @"We found you! You've rented from either National or Alamo.", @"");
        default: return nil;
    }
}

- (NSString *)scanButtonTitle
{
    return @"Scan License (optional)";
}

- (BOOL)hideScan
{
    return YES;
}

- (void)scanLicense
{
    [EHIAnalytics trackAction:EHIAnalyticsEnrollmentScanLicense handler:nil];
}

@end
