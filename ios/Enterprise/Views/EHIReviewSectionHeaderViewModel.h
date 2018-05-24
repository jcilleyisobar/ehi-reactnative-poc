//
//  EHIReviewSectionHeaderViewModel.h
//  Enterprise
//
//  Created by Rafael Machado on 8/1/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIReviewSectionHeaderViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic) NSString *title;
@property (assign, nonatomic) BOOL hideDivider;
@end
