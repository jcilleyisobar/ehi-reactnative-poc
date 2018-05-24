//
//  EHIReservationReviewLoadingIndicator.h
//  Enterprise
//
//  Created by fhu on 4/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIView.h"

@interface EHIReviewBookingLoadingIndicator : EHIView

@property (assign, nonatomic) BOOL isAnimating;
@property (assign, nonatomic) BOOL isGreen;

- (void)finishLoadingWithSuccess:(BOOL)didSucceed completion:(void(^)(BOOL completed))handler;

@end
