//
//  EHICalendarDayCell.h
//  Enterprise
//
//  Created by Ty Cobb on 3/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"
#import "EHICalendarDayViewModel.h"

@interface EHICalendarDayCell : EHICollectionViewCell
/** The view model for the calendar day cell */
@property (strong, nonatomic) EHICalendarDayViewModel *viewModel;
@end
