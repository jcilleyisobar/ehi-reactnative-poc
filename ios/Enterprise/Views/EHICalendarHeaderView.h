//
//  EHICalendarHeaderView.h
//  Enterprise
//
//  Created by Ty Cobb on 3/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIView.h"

@interface EHICalendarHeaderView : EHIView
/** The y-position of the title in points */
@property (nonatomic, readonly) CGFloat titlePosition;
/** The height of the container in points */
@property (nonatomic, readonly) CGFloat containerHeight;
/** Animates the month label's visibility; may optionally be animated */
- (void)setMonthIsVisible:(BOOL)isVisible animated:(BOOL)animated;
@end
