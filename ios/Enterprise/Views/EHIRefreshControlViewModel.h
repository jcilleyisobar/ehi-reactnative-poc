//
//  EHIRefreshControl.h
//  Enterprise
//
//  Created by Ty Cobb on 7/15/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIRefreshControlViewModel : EHIViewModel <MTRReactive>

/** The current percent complete for the refresh control */
@property (assign, nonatomic) CGFloat percentComplete;
/** @c YES if the refresh control is currently enabled */
@property (assign, nonatomic) BOOL isDisabled;
/** @c YES if the a refresh is in progress */
@property (assign, nonatomic) BOOL isRefreshing;

@end
