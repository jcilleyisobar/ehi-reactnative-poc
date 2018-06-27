//
//  EHIRewardsAboutTiersHeaderViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 1/16/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIRewardsAboutTiersHeaderViewModel : EHIViewModel <MTRReactive>
@property (copy, nonatomic, readonly) NSString *title;
@property (copy, nonatomic, readonly) NSString *subtitle;
@end
