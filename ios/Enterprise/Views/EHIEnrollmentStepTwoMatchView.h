//
//  EHIEnrollmentStepTwoMatchView.h
//  Enterprise
//
//  Created by Rafael Machado on 05/01/18.
//Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIView.h"

@interface EHIEnrollmentStepTwoMatchView : EHIView

@end

@protocol EHIEnrollmentStepTwoMatchActions <NSObject>
- (void)enrollmentStepTwoMatchDidTapChange;
- (void)enrollmentStepTwoMatchDidTapKeep;
@end
