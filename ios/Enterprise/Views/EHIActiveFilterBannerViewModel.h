//
//  EHIActiveFilterBannerViewModel.h
//  Enterprise
//
//  Created by Michael Place on 4/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIActiveFilterBannerViewModel : EHIViewModel <MTRReactive>
/** Title containing a list of active filters */
@property (copy  , nonatomic) NSAttributedString *attributedTitle;
/** Title for the clear button */
@property (copy  , nonatomic) NSString *clearButtonTitle;
@end
