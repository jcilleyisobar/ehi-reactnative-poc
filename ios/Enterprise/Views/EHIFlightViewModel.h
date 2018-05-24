//
//  EHIReviewAirlineViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 6/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIFlightViewModel : EHIViewModel <MTRReactive>

@property (copy, nonatomic, readonly) NSAttributedString *detailsTitle;
@property (copy, nonatomic, readonly) NSString *title;
@property (copy, nonatomic, readonly) NSString *subtitle;
@property (copy, nonatomic, readonly) NSAttributedString *addTitle;

// computed
@property (assign, nonatomic, readonly) BOOL showsAddButton;

- (void)addFlightDetails;

@end

NS_ASSUME_NONNULL_END