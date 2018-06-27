//
//  EHILocationsFilterBannerViewModel.h
//  Enterprise
//
//  Created by Rafael Machado on 19/05/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHILocationsFilterBannerViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic) NSString *filters;
@property (assign, nonatomic) BOOL hideClear;
@property (assign, nonatomic) BOOL hasData;
@end
