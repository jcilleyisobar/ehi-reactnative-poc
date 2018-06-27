//
//  EHIDashboardHistoryViewModel.h
//  Enterprise
//
//  Created by mplace on 5/18/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIDashboardHistoryViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic, readonly) NSString *iconImageName;
@property (copy  , nonatomic, readonly, nullable) NSString *title;
@property (copy  , nonatomic, readonly) NSAttributedString *details;
@property (copy  , nonatomic, readonly, nullable) NSString *trackButtonTitle;

// computed
@property (assign, nonatomic, readonly) BOOL hidesTitle;
@property (assign, nonatomic, readonly) BOOL hidesTrackButton;

- (void)enableTracking;

@end

NS_ASSUME_NONNULL_END