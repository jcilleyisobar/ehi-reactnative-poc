//
//  EHISingleTimeCalendarViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/16/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHISingleTimeCalendarViewModel.h"
#import "EHIViewModel_Subclass.h"

@interface EHISingleTimeCalendarViewModel ()
@property (copy  , nonatomic) NSArray *times;
@property (assign, nonatomic) EHISingleTimeCalendarType type;
@end

@implementation EHISingleTimeCalendarViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        if([model isKindOfClass:NSNumber.class]) {
            self.type = (EHISingleTimeCalendarType)[model integerValue];
        }
    }
    
    return self;
}

# pragma mark - Actions

- (BOOL)shouldSelectTimeAtIndexPath:(NSIndexPath *)indexPath
{
    EHITimePickerTime *time = [self timeAtIndexPath:indexPath];
    return time != nil;
}

- (void)selectTimeAtIndexPath:(NSIndexPath *)indexPath
{
    if(self.flow == EHISingleDateCalendarFlowLocationsFilter) {
        [EHIAnalytics trackAction:EHIAnalyticsActionSelectTime handler:^(EHIAnalyticsContext *context) {
            context.macroEvent = EHIAnalyticsMacroEventLocationSelectDateTime;
        }];
    }

    EHITimePickerTime *time = [self timeAtIndexPath:indexPath];
    if(self.type == EHISingleTimeCalendarTypePickupTime) {
        self.pickupTime = time.date;
    } else {
        self.returnTime = time.date;
    }

    ehi_call(self.handler)(self.pickupTime, self.returnTime);
}

- (void)setIndexPathForCurrentTime:(NSIndexPath *)indexPathForCurrentTime
{
    BOOL selectable = [self shouldSelectTimeAtIndexPath:indexPathForCurrentTime];
    _indexPathForCurrentTime = selectable ? indexPathForCurrentTime : nil;
}

# pragma mark - Accessors

- (NSString *)title
{
    if(self.type == EHISingleTimeCalendarTypePickupTime) {
        return EHILocalizedString(@"time_select_pickup_title", @"Pick-up Time", @"");
    } else {
        return EHILocalizedString(@"time_select_return_title", @"Return", @"");
    }
}

- (NSArray *)times
{
    if(!_times) {
        NSDate *firstValidTime = [NSDate ehi_today];
        NSDate *lastValidTime = [firstValidTime ehi_addDays:1];
        NSArray *times = @(0).upTo(([firstValidTime ehi_hoursUntilDate:lastValidTime] * 2) - 1);
        
        // map the offsets into times on the half hour
        _times = times.map(^(NSNumber *offset) {
            NSDate *date = [firstValidTime ehi_addMinutes:offset.integerValue * 30];
            EHITimePickerTime *time = [[EHITimePickerTime alloc] initWithDate:date];
            
            return time;
        });
    }
    
    return _times;
}

- (NSString *)selectionButtonTitle
{
    return EHILocalizedString(@"time_picker_button_title", @"SELECT", @"");
}

- (EHITimePickerTime *)timeAtIndexPath:(NSIndexPath *)indexPath
{
    return indexPath.item < self.times.count ? self.times[indexPath.item] : nil;
}

- (BOOL)isPickingReturnTime
{
    return self.type == EHISingleTimeCalendarTypeReturnTime;
}

- (BOOL)currentTimeIsSelectable
{
    return self.indexPathForCurrentTime != nil;
}

- (NSIndexPath *)initialIndexPath
{
    // return the center index (12pm)
    NSInteger item = self.times.count / 2;
    NSIndexPath *initialIndexPath = [NSIndexPath indexPathForItem:item inSection:0];
    NSDate *initialTime = self.type == EHISingleTimeCalendarTypePickupTime
        ? self.pickupTime.copy
        : self.returnTime.copy;
    if(initialTime) {
        EHITimePickerTime *target = (self.times ?: @[]).find(^(EHITimePickerTime *time){
            return [time.date isEqual:initialTime];
        });
        NSInteger item = (self.times ?: @[]).indexOf(target);
        if(item < self.times.count) {
            initialIndexPath = [NSIndexPath indexPathForItem:item inSection:0];
        }
    }

    return initialIndexPath;
}

@end
