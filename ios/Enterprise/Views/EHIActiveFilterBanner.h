//
//  EHIActiveFilterBanner.h
//  Enterprise
//
//  Created by Michael Place on 4/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIView.h"

@interface EHIActiveFilterBanner : EHIView

@end

@protocol EHIActiveFilterBannerActions <NSObject> @optional
- (void)didTapClearButtonForFilterBanner:(EHIActiveFilterBanner *)banner;
@end