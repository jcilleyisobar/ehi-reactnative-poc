//
//  EHITermsAndConditionsViewModel.h
//  Enterprise
//
//  Created by frhoads on 10/20/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

typedef NS_ENUM(NSInteger, EHITermsAndConditionsLayout) {
    EHITermsAndConditionsLayoutDefault,
    EHITermsAndConditionsLayoutReview
};

@interface EHITermsAndConditionsCellViewModel : EHIViewModel
@property (copy  , nonatomic, readonly) NSString *title;
@property (assign, nonatomic) EHITermsAndConditionsLayout layout;

@end
