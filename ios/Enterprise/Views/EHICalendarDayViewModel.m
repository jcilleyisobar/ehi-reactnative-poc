//
//  EHICalendarDayViewModel.m
//  Enterprise
//
//  Created by Ty Cobb on 3/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICalendarDayViewModel.h"

@interface EHICalendarDayViewModel ()
@property (strong, nonatomic) EHICalendarDay *day;
@property (copy  , nonatomic) NSString *title;
@property (assign, nonatomic) BOOL isSelectable;
@property (assign, nonatomic) BOOL isWithinActiveMonth;
@end

@implementation EHICalendarDayViewModel

- (void)updateWithModel:(EHICalendarDay *)day
{
    [super updateWithModel:day];
    
    if([day isKindOfClass:[EHICalendarDay class]]) {
        self.day = day;
    }
}

# pragma mark - Day

- (void)setDay:(EHICalendarDay *)day
{
    // destroy dependencies from the old day
    [_day destroyDependencies];
    
    _day = day;
    
    self.title = day.title;
    
    [MTRReactor autorun:^(MTRComputation *computation) {
        self.isSelectable = day.isSelectable;
    }];
    
    [MTRReactor autorun:^(MTRComputation *computation) {
        self.isWithinActiveMonth = day.isWithinActiveMonth;
    }];
}

# pragma mark - Accessors

- (BOOL)isToday
{
    return self.day.isToday;
}

# pragma mark - MTRReactive

+ (NSArray *)nonreactiveProperties:(EHICalendarDayViewModel *)model
{
    return @[
        @key(model.day),
    ];
}

@end
