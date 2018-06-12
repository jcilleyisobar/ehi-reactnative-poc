//
//  EHIReservationPriceItemViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 7/14/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIAnalytics.h"

@interface EHIReservationPriceItemViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic) NSAttributedString *title;
@property (copy  , nonatomic) NSAttributedString *accessoryTitle;
@property (assign, nonatomic) BOOL isLearnMore;
@property (assign, nonatomic) BOOL isLastInSection;
@property (assign, nonatomic) BOOL isCharged;

- (void)showDetail;

@end
