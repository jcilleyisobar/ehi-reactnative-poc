//
//  EHIReviewTravelPurposeViewModel.h
//  Enterprise
//
//  Created by fhu on 6/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIReviewTravelPurposeViewModel : EHIViewModel

@property (copy, nonatomic) NSString *title;
@property (copy, nonatomic) NSString *subtitle;
@property (copy, nonatomic) NSString *segmentedControlFirstTitle;
@property (copy, nonatomic) NSString *segmentedControlSecondTitle;

- (void)selectTravelPurposeAtIndex:(NSInteger)index;

@end
