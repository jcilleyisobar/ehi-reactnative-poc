//
//  EHICalendarMonthCell.h
//  Enterprise
//
//  Created by Ty Cobb on 3/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"
#import "EHICalendarDay.h"

@interface EHICalendarMonthCell : EHICollectionViewCell
/** The y-position of the title in points */
@property (nonatomic, readonly) CGFloat titlePosition;
/** Animates the header view's visibility; may optionally be animated */
- (void)setIsVisible:(BOOL)isVisible animated:(BOOL)animated;
@end
