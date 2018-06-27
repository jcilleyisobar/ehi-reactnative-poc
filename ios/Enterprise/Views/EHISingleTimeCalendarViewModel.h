//
//  EHISingleTimeCalendarViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 5/16/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHITimePickerTime.h"
#import "EHIButtonType.h"
#import "EHISingleDateCalendarEnums.h"

typedef NS_ENUM(NSInteger, EHISingleTimeCalendarType) {
    EHISingleTimeCalendarTypePickupTime,
    EHISingleTimeCalendarTypeReturnTime
};

typedef void (^EHISingleTimeCalendarHandler)(NSDate *pickupTime, NSDate *returnTime);

@interface EHISingleTimeCalendarViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) NSArray *times;
@property (copy  , nonatomic, readonly) NSString *selectionButtonTitle;
@property (assign, nonatomic) BOOL infoButtonIsHidden;
@property (assign, nonatomic) BOOL currentTimeIsSelectable;
@property (assign, nonatomic) BOOL isPickingReturnTime;
@property (strong, nonatomic) NSIndexPath *indexPathForCurrentTime;
@property (strong, nonatomic) NSDate *pickupTime;
@property (strong, nonatomic) NSDate *returnTime;
@property (copy  , nonatomic) EHISingleTimeCalendarHandler handler;
@property (assign, nonatomic) EHISingleDateCalendarFlow flow;

- (BOOL)shouldSelectTimeAtIndexPath:(NSIndexPath *)indexPath;
- (void)selectTimeAtIndexPath:(NSIndexPath *)indexPath;

- (NSIndexPath *)initialIndexPath;

@end
