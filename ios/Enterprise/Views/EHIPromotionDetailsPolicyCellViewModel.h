//
//  EHIPromotionDetailsPolicyCellViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 3/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIPromotionDetailsPolicyCellViewModel : EHIViewModel <MTRReactive>
@property (copy, nonatomic, readonly) NSString *policiesButtonTitle;
- (void)didTapPolicies;
@end
