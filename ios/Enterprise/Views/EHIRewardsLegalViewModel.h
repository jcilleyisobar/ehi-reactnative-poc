//
//  EHIRewardsLegalViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 1/23/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIRewardsLegalViewModel : EHIViewModel <MTRReactive>
@property (copy, nonatomic, readonly) NSAttributedString *legal;
@end
