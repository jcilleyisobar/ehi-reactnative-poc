//
//  EHICalendarHeaderView.m
//  Enterprise
//
//  Created by Ty Cobb on 3/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICalendarHeaderView.h"
#import "EHICalendarHeaderViewModel.h"

@interface EHICalendarHeaderView ()
@property (strong, nonatomic) EHICalendarHeaderViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *monthLabel;
@property (weak  , nonatomic) IBOutlet UILabel *closedDatesLabel;
@property (strong, nonatomic) IBOutletCollection(UILabel) NSArray *weekdayLabels;
@end

@implementation EHICalendarHeaderView

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHICalendarHeaderViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
   
    // sort the labels left-to-right
    self.weekdayLabels.sortBy(^(UILabel *label) {
        return label.frame.origin.x;
    });
}

# pragma mark - Reactions

- (void)registerReactions:(EHICalendarHeaderViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.monthTitle)  : dest(self, .monthLabel.text),
        source(model.closedTitle) : dest(self, .closedDatesLabel.text),
        source(model.weekdayTitles) : ^(NSArray *weekdayTitles) {
            weekdayTitles.each(^(NSString *title, NSInteger index) {
                [self.weekdayLabels[index] setText:title];
            });
        }
    });
}

# pragma mark - Visibility

- (void)setMonthIsVisible:(BOOL)isVisible animated:(BOOL)animated
{
    UIView.animate(animated)
    .duration(0.3).delay(isVisible ? 0.1 : 0.0)
    .option(UIViewAnimationOptionCurveEaseOut).transform(^{
        self.monthLabel.alpha = isVisible ? 1.0f : 0.0f;
    }).start(nil);
}

# pragma mark - Accessors

- (CGFloat)titlePosition
{
    return self.monthLabel.frame.origin.y;
}

- (CGFloat)containerHeight
{
    return self.bounds.size.height - [self.weekdayLabels.firstObject frame].size.height;
}

@end
