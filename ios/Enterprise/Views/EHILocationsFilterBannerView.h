//
//  EHILocationsFilterBannerView.h
//  Enterprise
//
//  Created by Rafael Machado on 19/05/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIView.h"

@interface EHILocationsFilterBannerView : EHIView

@end

@protocol EHILocationsFilterBannerViewActions <NSObject>
- (void)filterBannerDidTapClear;
@end
