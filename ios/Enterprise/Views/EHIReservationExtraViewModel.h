//
//  EHIReservationExtraViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 4/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIReservationExtraViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic) NSAttributedString *title;
@property (copy  , nonatomic) NSString *sectionTitle;
@property (assign, nonatomic) BOOL isEditable;
@property (assign, nonatomic) BOOL hasExtras;
@property (assign, nonatomic) BOOL lastInSection;
@end
