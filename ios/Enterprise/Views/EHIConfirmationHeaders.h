//
//  EHIConfirmationHeaders.h
//  Enterprise
//
//  Created by Rafael Ramos on 6/6/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReviewSectionHeaderViewModel.h"
#import "EHIConfirmationViewModel.h"

@interface EHIConfirmationHeaders : NSObject
- (EHIReviewSectionHeaderViewModel *)headerForSection:(EHIConfirmationSection)section;
@end
