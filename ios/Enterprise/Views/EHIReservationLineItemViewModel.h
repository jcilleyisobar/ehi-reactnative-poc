//
//  EHIReservationLineItemViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 4/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIReservationLineItemViewModel : EHIViewModel <MTRReactive>
/** Title of the line item cell */
@property (copy  , nonatomic) NSAttributedString *title;
/** Accessory title of the line item cell */
@property (copy  , nonatomic) NSAttributedString *accessoryTitle;
/** Subtitle of the line item cell */
@property (copy  , nonatomic) NSString *subtitle;

@property (assign, nonatomic) BOOL isLearnMore;

@end
