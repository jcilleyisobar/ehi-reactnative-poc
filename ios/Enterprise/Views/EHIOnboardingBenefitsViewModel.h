//
//  EHIOnboardingBenefitsViewModel.h
//  Enterprise
//
//  Created by Stu Buchbinder on 1/18/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIOnboardingBenefitsViewModel : EHIViewModel

@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) NSArray<NSString *> *benefits;

@end
