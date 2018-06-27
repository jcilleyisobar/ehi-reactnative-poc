//
//  EHICalendarMonthCell.m
//  Enterprise
//
//  Created by Ty Cobb on 3/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICalendarMonthCell.h"

@interface EHICalendarMonthCell ()
@property (strong, nonatomic) EHICalendarDay *day;
@property (weak  , nonatomic) IBOutlet UILabel *monthLabel;
@end

@implementation EHICalendarMonthCell

- (void)layoutSubviews
{
    [super layoutSubviews];

    self.clipsToBounds = NO;
    self.monthLabel.layer.ehi_showsShadow = YES;
    self.monthLabel.layer.shadowColor = [UIColor whiteColor].CGColor;
    self.monthLabel.layer.shadowOpacity = 1.0f;
}

- (void)updateWithModel:(EHICalendarDay *)day metrics:(EHILayoutMetrics *)metrics
{
    [super updateWithModel:day metrics:metrics];
   
    self.day = day;
    self.monthLabel.text = day.monthTitle;
}

# pragma mark - Visibility

- (void)setIsVisible:(BOOL)isVisible animated:(BOOL)animated
{
    UIView.animate(animated)
        .duration(0.3).delay(isVisible ? 0.0 : 0.1)
        .option(UIViewAnimationOptionCurveEaseOut).transform(^{
            self.contentView.alpha = isVisible ? 1.0f : 0.0f;
        }).start(nil);
}

# pragma mark - Accessors

- (CGFloat)titlePosition
{
    return self.monthLabel.frame.origin.y;
}

@end
