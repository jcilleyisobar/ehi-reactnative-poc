//
//  EHISurveyViewModel.h
//  Enterprise
//
//  Created by frhoads on 12/7/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHISurveyViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) NSString *greetingsTitle;
@property (copy  , nonatomic, readonly) NSString *instructionsTitle;
@property (copy  , nonatomic, readonly) NSAttributedString *customerDetailTitle;
@property (copy  , nonatomic, readonly) NSString *sendSurveyTitle;
@property (copy  , nonatomic, readonly) NSString *surveyPolicyTitle;
@property (copy  , nonatomic) NSString *customerDetail;
@property (assign, nonatomic) BOOL isInvalidInput;
@property (assign, nonatomic) BOOL isLoading;

- (void)submitContact;
- (void)showSurveyPolicy;

@end
