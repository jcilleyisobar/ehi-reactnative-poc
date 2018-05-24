//
//  EHIReviewHeaders.h
//  Enterprise
//
//  Created by Rafael Ramos on 6/3/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReviewSectionHeaderViewModel.h"
#import "EHIReviewViewModel.h"

@interface EHIReviewHeaders : NSObject
- (EHIReviewSectionHeaderViewModel *)headerForSection:(EHIReviewSection)section;
@end
