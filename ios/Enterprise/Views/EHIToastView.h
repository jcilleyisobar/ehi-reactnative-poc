//
//  EHIToastView.h
//  Enterprise
//
//  Created by Alex Koller on 4/7/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIView.h"
#import "EHIToast.h"

@interface EHIToastView : EHIView

/** Construct a new toast view from the nib and binds the toast model */
+ (instancetype)instanceWithToast:(EHIToast *)toast;
/** Animates the toast view in; must already be in the view hierarchy */
- (void)show;
/** Animates the toast view out; must already be in the view hierarchy */
- (void)hide;

- (EHILayoutMetrics *)metrics;

@end
