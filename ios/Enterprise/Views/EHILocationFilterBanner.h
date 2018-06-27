//
//  EHILocationFilterBanner.h
//  Enterprise
//
//  Created by Michael Place on 4/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIView.h"

@protocol EHILocationFilterBannerDelegate;

@interface EHILocationFilterBanner : EHIView
@property (weak, nonatomic) IBOutlet id<EHILocationFilterBannerDelegate> delegate;
@end

@protocol EHILocationFilterBannerDelegate <NSObject>
- (void)banner:(EHILocationFilterBanner *)banner didTapClearButton:(UIButton *)button;
@end