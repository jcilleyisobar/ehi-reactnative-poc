//
//  EHIReviewModifyPrepayBannerViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 5/18/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIReviewModifyPrepayBannerViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) NSString *subtitle;
@property (copy  , nonatomic, readonly) NSString *totalAmount;
@property (assign, nonatomic) BOOL updated;
@property (assign, nonatomic) BOOL isNAAirport;
@end
